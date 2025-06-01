package es.courselab.app.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class EmailService {

    private static final String EMAIL_RESETPASSWORD_SUBJECT = "Tu nueva contraseña de Courselab.";
    private static final String EMAIL_CONFIRMATION_SUBJECT = "Confirma tu nueva cuenta de Courselab.";
    private final JavaMailSender javaMailSender;
    @Value("${courselab.email.serveraddresslink}")
    private String serveraddresslink;

    /**
     * Envía un correo reactivo para restablecer la contraseña.
     * Devuelve Mono<Void> que se completa cuando el envío ha terminado (o emite error).
     */
    public Mono<Void> sendPasswordResetEmail(String newPassword, String email) {
        String message = "Tu nueva contraseña es: " + newPassword;
        String from = "no-reply@courselab.org";
        return sendReactive(email, from, message, EMAIL_RESETPASSWORD_SUBJECT);
    }

    /**
     * Envía un correo reactivo de activación de cuenta.
     */
    public Mono<Void> sendActivationEmail(String activationToken, String email) {
        String message = "Haz click en el siguiente link para activar tu cuenta de Courselab:\n"
                + serveraddresslink + "auth/activate?token=" + activationToken;
        String from = "no-reply@courselab.org";
        return sendReactive(email, from, message, EMAIL_CONFIRMATION_SUBJECT);
    }

    /**
     * Implementación interna que envuelve el envío de correo en un Mono,
     * delegando el trabajo de bloqueo a un hilo de I/O mediante boundedElastic().
     */
    private Mono<Void> sendReactive(String to, String from, String text, String subject) {
        return Mono.fromCallable(() -> {
                    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                    helper.setFrom(from);
                    helper.setTo(to);
                    helper.setSubject(subject);
                    helper.setText(text, false);
                    javaMailSender.send(mimeMessage);
                    return Void.TYPE; // valor de retorno irrelevante
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
