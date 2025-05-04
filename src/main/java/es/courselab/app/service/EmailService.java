package es.courselab.app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class EmailService {

    @Value("${courselab.email.serveraddresslink}")
    private String serveraddresslink;

    private final static String EMAIL_RESETPASSWORD_SUBJECT = "Tu nueva contraseña de Courselab.";
    private final static String EMAIL_CONFIRMATION_SUBJECT = "Confirma tu nueva cuenta de Courselab.";

    @Autowired
    private JavaMailSender javaMailSender;


    public void sendPasswordResetEmail(String newPassword, String email) throws MessagingException {
        String message = "Tu nueva contraseña es: " + newPassword;
        String from = "no-reply@courselab.org";

        //System.out.println(message);
        send(email, from, message, EMAIL_RESETPASSWORD_SUBJECT);
    }

    public void sendActivationEmail(String activationToken, String email) throws MessagingException {
        String message = "Haz click en el siguiente link para activar tu cuenta de Courselab: \n"
                + serveraddresslink + "auth/activate?token=" + activationToken;
        String from = "no-reply@courselab.org";

        //System.out.println(message);
        send(email, from, message, EMAIL_CONFIRMATION_SUBJECT);
    }

    @Async
    private void send(String to, String from, String email, String subject) throws MessagingException {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(email);
            javaMailSender.send(mimeMessage);
    }
}