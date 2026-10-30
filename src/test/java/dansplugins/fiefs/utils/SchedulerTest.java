package dansplugins.fiefs.utils;

import dansplugins.fiefs.services.StorageService;
import dansplugins.fiefs.testsupport.FakeBukkitServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Characterization tests for {@link Scheduler#scheduleAutosave()}, the hourly save of
 * {@code fiefs.json} and {@code claimedChunks.json} that {@code Fiefs.onEnable()} sets up.
 * The task the scheduler hands to Bukkit is captured by {@link FakeBukkitServer} and run
 * directly, so the callback's own logic is exercised rather than only its registration.
 *
 * <p>A live {@code Fiefs} plugin cannot be constructed outside a server, so the scheduler is
 * given null for it; the double records whatever plugin it was handed.
 */
class SchedulerTest {

    private static final long TICKS_PER_HOUR = 60L * 60L * 20L;

    /** Records messages instead of dereferencing the null {@code Fiefs} plugin reference. */
    private static final class RecordingLogger extends Logger {
        private final List<String> messages = new ArrayList<>();

        RecordingLogger() {
            super(null);
        }

        @Override
        public void log(String message) {
            messages.add(message);
        }
    }

    /** Counts saves instead of writing to {@code ./plugins/Fiefs/}. */
    private static final class CountingStorageService extends StorageService {
        private int saves;

        CountingStorageService() {
            super(null, null, null, null, null);
        }

        @Override
        public void save() {
            saves++;
        }
    }

    private final RecordingLogger logger = new RecordingLogger();
    private final CountingStorageService storageService = new CountingStorageService();
    private final Scheduler scheduler = new Scheduler(logger, null, storageService);

    @BeforeEach
    void installServer() {
        FakeBukkitServer.install();
    }

    @Test
    void scheduleAutosave_registersOneRepeatingTaskThatFirstRunsAfterAnHourAndThenHourly() {
        scheduler.scheduleAutosave();

        List<FakeBukkitServer.RepeatingTask> tasks = FakeBukkitServer.scheduledRepeatingTasks();
        assertEquals(1, tasks.size());
        assertEquals(TICKS_PER_HOUR, tasks.get(0).delayTicks);
        assertEquals(TICKS_PER_HOUR, tasks.get(0).periodTicks);
        assertNull(tasks.get(0).plugin);
    }

    @Test
    void scheduleAutosave_doesNotSaveWhenScheduling() {
        scheduler.scheduleAutosave();

        assertEquals(0, storageService.saves);
        assertEquals(Collections.singletonList("Scheduling hourly autosave."), logger.messages);
    }

    @Test
    void autosaveTask_savesOnEachRun() {
        scheduler.scheduleAutosave();
        Runnable task = FakeBukkitServer.scheduledRepeatingTasks().get(0).task;

        task.run();
        assertEquals(1, storageService.saves);

        task.run();
        assertEquals(2, storageService.saves);
    }

    @Test
    void autosaveTask_logsBeforeEachSave() {
        scheduler.scheduleAutosave();
        Runnable task = FakeBukkitServer.scheduledRepeatingTasks().get(0).task;

        task.run();
        task.run();

        assertEquals(Arrays.asList(
                "Scheduling hourly autosave.",
                "Saving. This will happen hourly.",
                "Saving. This will happen hourly."), logger.messages);
    }

    /**
     * Each call registers another task; nothing guards against scheduling the autosave twice.
     * {@code Fiefs.onEnable()} calls it once per enable, and Bukkit cancels a plugin's tasks
     * when the plugin is disabled, so a reload does not stack them.
     */
    @Test
    void scheduleAutosave_calledTwice_registersTwoTasks() {
        scheduler.scheduleAutosave();
        scheduler.scheduleAutosave();

        assertEquals(2, FakeBukkitServer.scheduledRepeatingTasks().size());
    }
}
