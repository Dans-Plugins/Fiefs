package dansplugins.fiefs.utils;

import dansplugins.fiefs.Fiefs;
import dansplugins.fiefs.services.StorageService;
import org.bukkit.Bukkit;

/**
 * @author Daniel McCoy Stephenson
 */
public class Scheduler {
    private final Logger logger;
    private final Fiefs fiefs;
    private final StorageService storageService;

    public Scheduler(Logger logger, Fiefs fiefs, StorageService storageService) {
        this.logger = logger;
        this.fiefs = fiefs;
        this.storageService = storageService;
    }

    public void scheduleAutosave() {
        logger.log("Scheduling hourly autosave.");
        int delay = 60 * 60; // 1 hour
        int secondsUntilRepeat = 60 * 60; // 1 hour
        Bukkit.getScheduler().scheduleSyncRepeatingTask(fiefs, new Runnable() {
            @Override
            public void run() {
                logger.log("Saving. This will happen hourly.");
                storageService.save();
            }
        }, delay * 20, secondsUntilRepeat * 20);
    }
}