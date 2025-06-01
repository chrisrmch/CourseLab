package es.courselab.app.controller;

import es.courselab.app.enumerated.EAccountRole;
import es.courselab.app.enumerated.EAccountState;
import es.courselab.app.handler.NotificationHandler;
import es.courselab.app.jwt.JwtUtils;
import es.courselab.app.model.AccountVerification;
import es.courselab.app.model.User;
import es.courselab.app.payload.request.AccountRequestLOGIN;
import es.courselab.app.payload.request.AccountRequestSIGNUP;
import es.courselab.app.payload.response.JwtResponse;
import es.courselab.app.payload.response.MessageResponse;
import es.courselab.app.repository.AccountVerificationRepository;
import es.courselab.app.repository.UserRepository;
import es.courselab.app.service.AccountVerificationServiceImpl;
import es.courselab.app.service.EmailService;
import es.courselab.app.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.security.auth.login.AccountNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private ReactiveAuthenticationManager reactiveAuthManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AccountVerificationRepository accountVerificationRepository;

    @Autowired
    private AccountVerificationServiceImpl accountVerificationService;

    @Autowired
    private NotificationHandler notificationHandler;


    // ------------------------------------------------------------
    // ENDPOINT REACTIVO DE LOGIN: /auth/signin
    // ------------------------------------------------------------
    @Operation(summary = "Inicia sesión con una cuenta de Usuario/Administrador.")
    @PostMapping("/signin")
    public Mono<ResponseEntity<JwtResponse>> authenticateAccount(
            @Valid @RequestBody AccountRequestLOGIN loginRequest) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                );

        return reactiveAuthManager
                .authenticate(authenticationToken)
                .flatMap(authentication -> {
                    String jwt = jwtUtils.generateJwtToken(authentication);

                    User accountDetails = (User) authentication.getPrincipal();
                    String role = accountDetails.getAuthorities()
                            .toString()
                            .replace("[", "")
                            .replace("]", "");

                    JwtResponse jwtResponse = new JwtResponse(
                            jwt,
                            accountDetails.getUsuarioID(),
                            accountDetails.getNombre(),
                            accountDetails.getApellidos(),
                            accountDetails.getUsername(),
                            role
                    );

                    return Mono.just(ResponseEntity.ok(jwtResponse));
                })
                .doOnError(Throwable::printStackTrace)
                .onErrorResume(ex -> Mono.just(
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new JwtResponse(
                                        "",
                                        null,
                                        "",
                                        "",
                                        "",
                                        ""
                                ))
                ));
    }


    // ------------------------------------------------------------
    // ENDPOINT REACTIVO DE SIGNUP: /auth/signup/user
    // Ahora integramos el envío de correo de forma reactiva (emailService.sendActivationEmail)
    // y luego, al completarse todo, emitimos la notificación WebSocket.
    // ------------------------------------------------------------
    @Operation(summary = "Crea una nueva Cuenta de Usuario.")
    @PostMapping("/signup/user")
    public Mono<ResponseEntity<MessageResponse>> registerUserAccount(
            @Valid @RequestBody AccountRequestSIGNUP signUpRequest) {

        return userRepository.existsByEmail(signUpRequest.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.just(
                                ResponseEntity
                                        .badRequest()
                                        .body(new MessageResponse("Error: Email is already in use."))
                        );
                    }
                    // 1) Crear la entidad User a partir del DTO
                    User account = signUpRequestToUserObject(signUpRequest);
                    // 2) Crear el registro de verificación
                    AccountVerification verification = new AccountVerification();
                    verification.setId(Base64.getEncoder()
                            .encodeToString(account.getEmail().getBytes(StandardCharsets.UTF_8))
                    );
                    verification.setEmail(account.getEmail());
                    // 3) Guardar usuario → guardar verificación → enviar email (reactivo)
                    return userService.saveUser(account)
                            .then(
                                    accountVerificationRepository.save(verification).doOnNext(AccountVerification::setAsAccountVerificationExists)
                            )
                            .flatMap(savedAv ->
                                    // Aquí, en lugar de Mono.fromRunnable(...), usamos emailService.sendActivationEmail
                                    emailService.sendActivationEmail(savedAv.getId(), savedAv.getEmail())
                                            .then(Mono.just(
                                                    ResponseEntity.ok(
                                                            new MessageResponse("New Account registered.")
                                                    )
                                            ))
                            );
                })
                .doOnError(Throwable::printStackTrace)
                .onErrorResume(ex -> {
                    String msg = (ex.getCause() instanceof jakarta.mail.MessagingException)
                            ? "Failed to send activation email."
                            : "Internal server error.";
                    return Mono.just(
                            ResponseEntity
                                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(new MessageResponse(msg))
                    );
                })
                // 4) Cuando todo complete con éxito (200 OK), emitimos la notificación WebSocket
                .doOnSuccess(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        String email = signUpRequest.getEmail();
                        notificationHandler.publish("Nuevo usuario registrado: " + email);
                    }
                });
    }

    private User signUpRequestToUserObject(AccountRequestSIGNUP signUpRequest) {
        User account = new User();
        account.setEmail(signUpRequest.getEmail());
        account.setPassword(encoder.encode(signUpRequest.getPassword()));
        account.setRole(EAccountRole.ROLE_USER);
        account.setEstado(EAccountState.INACTIVE);
        account.setFechaCreacion(LocalDateTime.now());
        return account;
    }


    // ------------------------------------------------------------
    // ENDPOINT REACTIVO DE ACTIVACIÓN: /auth/activate
    // ------------------------------------------------------------
    @Operation(summary = "Activa una Cuenta de Usuario.")
    @GetMapping("/activate")
    public Mono<ResponseEntity<String>> activateAccount(
            @RequestParam(name = "token", defaultValue = "") String token) {

        return Mono.just(token)
                .map(t -> {
                    byte[] decodedBytes = Base64.getDecoder().decode(t);
                    return new String(decodedBytes, StandardCharsets.UTF_8);
                })
                .flatMap(email ->
                        userRepository.findByEmail(email)
                                .switchIfEmpty(Mono.error(new AccountNotFoundException(email)))
                                .flatMap(user -> {
                                    user.setEstado(EAccountState.ACTIVE);
                                    return userRepository.save(user);
                                })
                                .then(accountVerificationRepository.findByEmail(email)
                                        .switchIfEmpty(Mono.error(
                                                new AccountNotFoundException("Account not found: " + email))
                                        )
                                )
                                .flatMap(accountVerificationService::deleteAccountVerificationAndRegisterLog)
                                .map(ignored -> {
                                    String content = "<header>"
                                            + "<h1>Courselab</h1>"
                                            + "<h2>Running App</h2>"
                                            + "<h4><span>Tu cuenta de usuario </span>"
                                            + email
                                            + "<span> ha sido activada!</span></h4>"
                                            + "</header>";
                                    HttpHeaders headers = new HttpHeaders();
                                    headers.setContentType(MediaType.TEXT_HTML);
                                    return new ResponseEntity<>(content, headers, HttpStatus.OK);
                                })
                ).doOnError(Throwable::printStackTrace)
                .onErrorResume(AccountNotFoundException.class, ex ->
                        Mono.just(ResponseEntity.notFound().build())
                )
                .onErrorResume(ex ->
                        Mono.just(ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build())
                );
    }
}
