package dansplugins.fiefs.integrators;

import dansplugins.fiefs.testsupport.FakeBukkitServer;
import dansplugins.fiefs.utils.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for the point at which {@link MedievalFactionsIntegrator} looks Medieval Factions up.
 *
 * <p>The integrator is built in a field initializer of the main class, before the config
 * service exists, and its logger reads the config through the main class — so anything it
 * logs during construction is a NullPointerException that stops the plugin loading (#188).
 * The lookup, and the logging that goes with it, therefore has to wait for
 * {@link MedievalFactionsIntegrator#initialize()}, which the main class calls from
 * {@code onEnable()} once the config has been read.
 *
 * <p>{@code getFactionForPlayer(...)} is not exercised here: it reaches through the real
 * Medieval Factions services, which cannot be stood up outside a running server.
 */
class MedievalFactionsIntegratorTest {

    private static final String FOUND_MESSAGE = "[DEBUG] Medieval Factions was found successfully!";

    private final List<String> logged = new ArrayList<>();

    /** A logger that records what it is asked to log instead of reading the plugin's config. */
    private final Logger recordingLogger = new Logger(null) {
        @Override
        public void log(String message) {
            logged.add(message);
        }
    };

    /**
     * A logger in the state the real one is in during construction of the main class: the
     * plugin it would read the debug flag through cannot answer yet, so any log call blows up.
     */
    private final Logger notYetUsableLogger = new Logger(null) {
        @Override
        public void log(String message) {
            throw new IllegalStateException("logged before the plugin was ready: " + message);
        }
    };

    @BeforeEach
    void installServer() {
        FakeBukkitServer.install();
    }

    @Test
    void constructor_doesNotLogEvenWhenMedievalFactionsIsInstalled() {
        // The lookup logs when it finds the plugin, so the plugin being present is what turned
        // construction-time logging into the crash on the boot gate.
        FakeBukkitServer.registerPlugin("MedievalFactions");

        assertDoesNotThrow(() -> new MedievalFactionsIntegrator(notYetUsableLogger));
    }

    @Test
    void initialize_logsThatMedievalFactionsWasFoundOnceItIsCalled() {
        FakeBukkitServer.registerPlugin("MedievalFactions");
        MedievalFactionsIntegrator integrator = new MedievalFactionsIntegrator(recordingLogger);

        integrator.initialize();

        assertEquals(Collections.singletonList(FOUND_MESSAGE), logged);
    }

    @Test
    void initialize_leavesTheApiUnavailableWhenTheInstalledPluginIsNotMedievalFactionsItself() {
        // The registered plugin carries the name but is not an instance of the Medieval
        // Factions main class — the case of a plugin that borrowed the name, or of a Medieval
        // Factions build the integrator cannot cast to — so the API stays unavailable.
        FakeBukkitServer.registerPlugin("MedievalFactions");
        MedievalFactionsIntegrator integrator = new MedievalFactionsIntegrator(recordingLogger);

        integrator.initialize();

        assertFalse(integrator.isMedievalFactionsAPIAvailable());
        assertNull(integrator.getAPI());
    }

    @Test
    void initialize_leavesTheApiUnavailableAndLogsNothingWhenMedievalFactionsIsNotInstalled() {
        MedievalFactionsIntegrator integrator = new MedievalFactionsIntegrator(recordingLogger);

        integrator.initialize();

        assertFalse(integrator.isMedievalFactionsAPIAvailable());
        assertNull(integrator.getAPI());
        assertEquals(Collections.emptyList(), logged);
    }
}
