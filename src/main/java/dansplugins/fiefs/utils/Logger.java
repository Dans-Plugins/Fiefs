package dansplugins.fiefs.utils;

import dansplugins.fiefs.Fiefs;

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
            System.out.println("[Fiefs] " + message);
        }
    }

}
