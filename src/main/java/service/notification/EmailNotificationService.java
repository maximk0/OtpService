package service.notification;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Реализация NotificationService для отправки OTP-кодов по Email.
 * Конфигурация берётся из файла email.properties в resources.
 */
public class EmailNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    private final Session session;
    private final String username;
    private final String password;
    private final String fromAddress;

    /**
     * Конструктор загружает настройки почты и инициирует JavaMail Session.
     */
    public EmailNotificationService() {
        Properties props = loadConfig();
        this.username = props.getProperty("email.username");
        this.password = props.getProperty("email.password");
        this.fromAddress = props.getProperty("email.from");
        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( username, password);
            }
        });
    }

    /**
     * Загрузка конфигурации из файла email.properties.
     */
    private Properties loadConfig() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            if (is == null) {
                throw new IllegalStateException("email.properties not found in classpath");
            }
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (IOException e) {
            logger.error("Failed to load email.properties", e);
            throw new RuntimeException("Could not load email configuration", e);
        }
    }

    /**
     * Отправляет письмо с кодом подтверждения на заданный email-адрес.
     */
    @Override
    public void sendCode(User user, String code, int operationNumber) {
        String recipientEmail = null;
        try {
            recipientEmail = user.getEmail();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Your OTP Code");
            message.setText("Your one-time confirmation code is: " + code);

            Transport.send(message);
            logger.info("OTP code sent via Email to {}", recipientEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send OTP email to {}", recipientEmail, e);
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
