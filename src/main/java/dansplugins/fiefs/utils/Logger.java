package dansplugins.fiefs.utils;

import dansplugins.fiefs.Fiefs;

import java.util.logging.Level;

/**
 * @author Daniel McCoy Stephenson
 */
public class Logger {
    private static Logger instance;

    private Logger() {

    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        if (Fiefs.getInstance().isDebugEnabled()) {
            Fiefs.getInstance().getLogger().log(Level.INFO, "[Fiefs] " + message);
        }
    }
}