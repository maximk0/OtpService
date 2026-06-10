package api;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import model.User;
import model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtils;
import util.TokenManager;

import java.io.IOException;

/**
 * Фильтр аутентификации и авторизации для HTTP-контроллеров.
 * <p>
 * Проверяет наличие заголовка Authorization: Bearer &lt;token&gt;,
 * валидирует токен через TokenManager и проверяет требуемую роль.
 * Если проверка проходит, сохраняет объект User в
 * exchange.setAttribute("user", user) и передаёт управление дальше.
 * Иначе возвращает соответствующий HTTP-статус:
 * <ul>
 *   <li>401 Unauthorized — при отсутствии или недействительном токене</li>
 *   <li>403 Forbidden — при недостаточности прав</li>
 * </ul>
 * </p>
 */
public class AuthFilter extends Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    private final UserRole requiredRole;

    /**
     * @param requiredRole минимальная роль пользователя для доступа к ресурсу
     */
    public AuthFilter(UserRole requiredRole) {
        this.requiredRole = requiredRole;
    }

    @Override
    public String description() {
        return "Фильтр аутентификации и проверки роли (ROLE >= " + requiredRole + ")";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            HttpUtils.sendError(exchange, 401, "Missing or invalid Authorization header");
            return;
        }
        String token = authHeader.substring(7);
        // Получаем пользователя по токену
        User user = TokenManager.getUser(token);
        if (user == null) {
            logger.warn("Invalid or expired token: {}", token);
            HttpUtils.sendError(exchange, 401, "Invalid or expired token");
            return;
        }

        if (user.getRole().ordinal() < requiredRole.ordinal()) {
            logger.warn("The operation is not allowed. User role: {}", user.getRole());
            HttpUtils.sendError(exchange, 403, "Forbidden");
            return;
        }
        exchange.setAttribute("user", user);
        exchange.setAttribute("userId", user.getId());

        chain.doFilter(exchange);
    }
}