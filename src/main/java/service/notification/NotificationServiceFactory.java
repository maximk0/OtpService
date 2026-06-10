package service.notification;

/**
 * Фабрика для получения нужной реализации NotificationService по каналу.
 */
public class NotificationServiceFactory {

    /**
     * Возвращает реализацию NotificationService под указанный канал.
     */
    public NotificationService getService(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> new EmailNotificationService();
            case SMS -> new SmsNotificationService();
            case TELEGRAM -> new TelegramNotificationService();
            case FILE -> new FileNotificationService();
            default -> throw new IllegalArgumentException("Unsupported channel: " + channel);
        };
    }
}
