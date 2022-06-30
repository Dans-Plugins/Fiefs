package dansplugins.fiefs.utils;

import dansplugins.fiefs.Fiefs;

import java.util.logging.Level;

/**
 * @author Daniel McCoy Stephenson
 */
public class Logger {
    private final Fiefs fiefs;

    public Logger(Fiefs fiefs) {
        this.fiefs = fiefs;
    }

    public void log(String message) {
        if (fiefs.isDebugEnabled()) {
            fiefs.getLogger().log(Level.INFO, "[Fiefs] " + message);
        }
    }
}