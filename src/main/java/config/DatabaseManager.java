package config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Утилитный класс для работы с подключением к БД.
 * Загружает настройки из application.properties и предоставляет методы
 * для получения Connection и закрытия ресурсов.
 */
public class DatabaseManager {
    private static final String PROPS_FILE = "application.properties";
    private static final String url;
    private static final String user;
    private static final String password;

    // Статический блок загружает параметры подключения при первом обращении
    static {
        try (InputStream is = DatabaseManager.class
                .getClassLoader()
                .getResourceAsStream(PROPS_FILE)) {
            if (is == null) {
                throw new RuntimeException("Не найден файл " + PROPS_FILE + " в classpath");
            }
            Properties props = new Properties();
            props.load(is);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Ошибка загрузки параметров БД из " + PROPS_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Возвращает новое JDBC-соединение.
     * @return Connection
     * @throws SQLException если не удалось установить соединение
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}