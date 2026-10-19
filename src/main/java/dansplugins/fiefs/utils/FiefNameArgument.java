package dansplugins.fiefs.utils;

import preponderous.ponder.misc.ArgumentParser;

import java.util.List;

/**
 * Reads a fief name from a command's arguments.
 *
 * <p>Fiefs are created and renamed with their name in double quotes, so a name may hold spaces.
 * Commands that look a fief up by name accept it either quoted ({@code "South Reach"}) or bare
 * ({@code South Reach}); a bare name is every argument joined with single spaces (#237).
 */
public final class FiefNameArgument {

    private FiefNameArgument() {
        // static helper only
    }

    /**
     * @return the first double-quoted argument if there is one, otherwise all arguments joined
     * with single spaces
     */
    public static String parse(String[] args) {
        List<String> quotedArgs = new ArgumentParser().getArgumentsInsideDoubleQuotes(args);
        if (!quotedArgs.isEmpty()) {
            return quotedArgs.get(0);
        }
        return String.join(" ", args);
    }
}
