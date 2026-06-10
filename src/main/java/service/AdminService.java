package service;

import dao.OtpCodeDao;
import dao.OtpConfigDao;
import dao.UserDao;
import model.OtpConfig;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public record AdminService(OtpConfigDao configDao, UserDao userDao, OtpCodeDao codeDao) {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public void updateOtpConfig(int length, int ttlSeconds) {
        // Создаем объект OtpConfig (id обычно не важен при обновлении)
        OtpConfig cfg = new OtpConfig(1L, length, ttlSeconds);
        configDao.updateConfig(cfg);
        logger.info("OTP config updated: length={}, ttlSeconds={}", length, ttlSeconds);
    }

    public List<User> getAllUsersWithoutAdmins() {
        return userDao.findAllUsersWithoutAdmins();
    }

    public void deleteUserAndCodes(Long userId) {
        codeDao.deleteAllByUserId(userId);
        userDao.delete(userId);
        logger.info("Deleted user id: {}, and user's OTP codes", userId);
    }
}

