package dansplugins.fiefs.testsupport;

import com.dansplugins.factionsystem.faction.MfFaction;

import java.lang.reflect.Constructor;

/**
 * Builds Medieval Factions {@link MfFaction} values for tests that need a player to be in a
 * faction.
 *
 * <p>Every Fiefs command starts by asking the integrator for the sender's faction and stops when
 * there is none, so a test reaching anything past that check needs a non-null faction. The public
 * constructors of {@link MfFaction} need a running Medieval Factions plugin to fill in their
 * defaults, so the private all-arguments constructor is called by reflection instead, with the
 * plugin and every reference the code under test does not read left null. Only {@code getId()}
 * and {@code getName()} are meaningful on the result.
 *
 * <p>A signature change upstream fails here with the constructors the class actually declares,
 * rather than as an unexplained failure inside a test.
 */
public final class MedievalFactionsFactions {

    // (MedievalFactions, String id, int, String name, String, List, List, MfFlagValues, String,
    //  MfPosition, double, boolean, MfFactionRoles, Map, List) in Medieval Factions 5.7.2
    private static final int ALL_ARGUMENTS = 15;

    private MedievalFactionsFactions() {
        // static factory methods only
    }

    /** A faction with the given id and name and nothing else set. */
    public static MfFaction faction(String id, String name) {
        Object[] arguments = new Object[ALL_ARGUMENTS];
        arguments[1] = id;
        arguments[2] = 0;
        arguments[3] = name;
        arguments[10] = 0.0d;
        arguments[11] = false;

        for (Constructor<?> constructor : MfFaction.class.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == ALL_ARGUMENTS) {
                try {
                    constructor.setAccessible(true);
                    return (MfFaction) constructor.newInstance(arguments);
                } catch (ReflectiveOperationException | IllegalArgumentException e) {
                    throw cannotBuild(e);
                }
            }
        }
        throw cannotBuild(null);
    }

    private static IllegalStateException cannotBuild(Exception cause) {
        StringBuilder declared = new StringBuilder();
        for (Constructor<?> constructor : MfFaction.class.getDeclaredConstructors()) {
            if (declared.length() > 0) {
                declared.append("; ");
            }
            declared.append(constructor);
        }
        return new IllegalStateException("Could not build an MfFaction for the Medieval Factions version"
                + " on the test classpath. Declared constructors: " + declared, cause);
    }
}
