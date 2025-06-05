package es.courselab.app.controller;


import es.courselab.app.enumerated.EAccountRole;
import es.courselab.app.enumerated.EAccountState;
import es.courselab.app.exception.EmailServiceException;
import es.courselab.app.exception.UserNotFoundException;
import es.courselab.app.jwt.JwtUtils;
import es.courselab.app.payload.request.AccountRequestLOGIN;
import es.courselab.app.model.AccountVerification;
import es.courselab.app.model.User;
import es.courselab.app.payload.request.AccountRequestPOST;
import es.courselab.app.payload.response.JwtResponse;
import es.courselab.app.payload.response.MessageResponse;
import es.courselab.app.repository.AccountVerificationRepository;
import es.courselab.app.repository.UserRepository;
import es.courselab.app.service.AccountVerificationServiceImpl;
import es.courselab.app.service.EmailService;
import es.courselab.app.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

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


    @Operation(summary = "Inicia sesión con una cuenta de Usuario/Administrador.")
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateAccount(@Valid @RequestBody AccountRequestLOGIN loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            if (!userService.userIsActive(loginRequest.getEmail())) {
                return new ResponseEntity<>("Todavía no has activado tu cuenta.", HttpStatus.NOT_ACCEPTABLE);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            User accountDetails = (User) authentication.getPrincipal();
            String role = accountDetails.getAuthorities().toString().replace("[", "").replace("]", "");

            Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
            assert user.isPresent();
            Integer timesLoggedIn = user.get().getLoginSuccesCounter();

            if (timesLoggedIn == 1) {
                Integer currentLoginTimes = user.get().getLoginSuccesCounter();
                user.get().setLoginSuccesCounter(currentLoginTimes + 1);
                userRepository.save(user.get());
                return ResponseEntity.ok(new JwtResponse(jwt, accountDetails.getIdUsuario(), accountDetails.getNombre(), true, accountDetails.getApellidos(), accountDetails.getUsername(), role));
            }
            Integer currentLoginTimes = user.get().getLoginSuccesCounter();
            user.get().setLoginSuccesCounter(currentLoginTimes + 1);
            userRepository.save(user.get());

            return ResponseEntity.ok(new JwtResponse(jwt, accountDetails.getIdUsuario(), accountDetails.getNombre(), false, accountDetails.getApellidos(), accountDetails.getUsername(), role));
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Crea una nueva Cuenta de Usuario.")
    @PostMapping("/signup/user")
    public ResponseEntity<?> registerUserAccount(@Valid @RequestBody AccountRequestPOST signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use."));
        }

        try {
            User account = signUpRequestToAccount(signUpRequest);

            AccountVerification accountVerification = new AccountVerification();
            accountVerification.setId(Base64.getEncoder().encodeToString(account.getEmail().getBytes()));
            accountVerification.setEmail(account.getEmail());

            emailService.sendActivationEmail(accountVerification.getId(), accountVerification.getEmail());
            userService.saveUser(account);
            accountVerificationRepository.save(accountVerification);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            throw new EmailServiceException(e.getMessage());
        }
        return ResponseEntity.ok(new MessageResponse("New Account registered."));
    }

    private User signUpRequestToAccount(AccountRequestPOST signUpRequest) {
        User account = new User();

        account.setEmail(signUpRequest.getEmail());
        account.setPassword(encoder.encode(signUpRequest.getPassword()));
        account.setRole(EAccountRole.ROLE_USER);
        account.setEstado(EAccountState.INACTIVE);
        account.setFechaCrecion(LocalDateTime.now());

        return account;
    }

    @Operation(summary = "Activa una Cuenta de Usuario.")
    @GetMapping(value = "/activate", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> activateAccount(@RequestParam(name = "token", defaultValue = "") String token, HttpServletRequest request) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String decodedString = new String(decodedBytes);

            User account = userRepository.findByEmail(decodedString).orElseThrow(() -> new AccountNotFoundException(decodedString));
            account.setEmailConfirmado(true);
            account.setLoginSuccesCounter(1);
            account.setEstado(EAccountState.ACTIVE);

            AccountVerification accountVerification = accountVerificationRepository.findByEmail(decodedString).orElseThrow(() -> new AccountNotFoundException("Account not found: " + decodedString));

            accountVerificationService.deleteAccountVerificationAndRegisterLog(accountVerification);
            userRepository.save(account);

            String content =
                    "<!DOCTYPE html>\n" +
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
                            "      <h4>Tu cuenta de usuario <span>" + account.getEmail() + "</span> ha sido activada!</h4>\n" +
                            "    </header>\n" +
                            "  </body>\n" +
                            "</html>";

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.TEXT_HTML);

            return new ResponseEntity<>(content, responseHeaders, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
