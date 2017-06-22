package utils;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.LogManager;

public class LogFactory {

    public static Logger getLogger(Class clazz) {
        Logger logger = null;
        try {
            InputStream inputStream = new FileInputStream("./config/logging.properties");
            LogManager.getLogManager().readConfiguration(inputStream);
            logger = LoggerFactory.getLogger(clazz.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return logger;
    }
}
