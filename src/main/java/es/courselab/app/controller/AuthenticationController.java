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
    @GetMapping(value = "/activate", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<ResponseEntity<String>> activateAccount(
            @RequestParam(name = "token", defaultValue = "") String token) {

        return Mono.just(token)
                // Decodifico el token (Base64 -> email)
                .map(t -> {
                    byte[] decodedBytes = Base64.getDecoder().decode(t);
                    return new String(decodedBytes, StandardCharsets.UTF_8);
                })
                // Busco al usuario por email
                .flatMap(email ->
                        userRepository.findByEmail(email)
                                .switchIfEmpty(Mono.error(new AccountNotFoundException(email)))
                                // Marco el usuario como “ACTIVO” en BD
                                .flatMap(user -> {
                                    user.setEstado(EAccountState.ACTIVE);
                                    user.setEmailConfirmado(true);
                                    return userRepository.save(user);
                                })
                                // Luego busco el registro de verificación por email
                                .then(accountVerificationRepository.findByEmail(email)
                                        .switchIfEmpty(Mono.error(
                                                new AccountNotFoundException("Account not found: " + email))
                                        )
                                )
                                // Borro ese registro de verificación y anoto en bitácora
                                .flatMap(accountVerificationService::deleteAccountVerificationAndRegisterLog)
                                // UNA VEZ QUE TODO LO ANTERIOR HAYA CONCLUIDO CORRECTAMENTE:
                                .then(Mono.defer(() -> {
                                    // 1) Notifico a todos los clientes WebSocket conectados:
                                    String mensaje = "✅ La cuenta de " + email + " se ha activado correctamente.";
                                    notificationHandler.publish(mensaje);

                                    // 2) Devuelvo el HTML al navegador solicitante
                                    String html = "<!DOCTYPE html>\n" +
                                            "<html lang=\"es\">\n" +
                                            "  <head>\n" +
                                            "    <meta charset=\"UTF-8\" />\n" +
                                            "    <title>Cuenta Activada</title>\n" +
                                            "    <style>\n" +
                                            "      body { font-family: Arial, sans-serif; background: #f5f5f5; text-align: center; padding: 2rem; }\n" +
                                            "      header { background: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); display: inline-block; padding: 2rem; }\n" +
                                            "      h1 { margin: 0; font-size: 2rem; color: #333333; }\n" +
                                            "      h2 { margin: 0.5rem 0 1.5rem; font-size: 1.2rem; color: #666666; }\n" +
                                            "      h4 { margin-top: 1rem; font-size: 1rem; color: #444444; }\n" +
                                            "      span { color: #007bff; }\n" +
                                            "    </style>\n" +
                                            "  </head>\n" +
                                            "  <body>\n" +
                                            "    <header>\n" +
                                            "      <h1>Courselab</h1>\n" +
                                            "      <h2>Running App</h2>\n" +
                                            "      <h4>Tu cuenta de usuario <span>" + email + "</span> ha sido activada!</h4>\n" +
                                            "    </header>\n" +
                                            "  </body>\n" +
                                            "</html>";
                                    return Mono.just(
                                            ResponseEntity.ok()
                                                    .contentType(MediaType.TEXT_HTML)
                                                    .body(html)
                                    );
                                }))
                )
                // Si no encuentra account o cualquier otro error, respondo adecuadamente:
                .onErrorResume(AccountNotFoundException.class, ex ->
                        Mono.just(ResponseEntity.notFound().build())
                )
                .onErrorResume(ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                );
    }
}
