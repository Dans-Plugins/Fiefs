package dansplugins.fiefs.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link FiefNameArgument}: a fief name is read the same whether or not it is typed in
 * double quotes (#237).
 */
class FiefNameArgumentTest {

    @Test
    void parse_aBareName_returnsIt() {
        assertEquals("Millbrook", FiefNameArgument.parse(new String[]{"Millbrook"}));
    }

    @Test
    void parse_aQuotedName_dropsTheQuotes() {
        assertEquals("Millbrook", FiefNameArgument.parse(new String[]{"\"Millbrook\""}));
    }

    @Test
    void parse_aQuotedNameSpanningSeveralArguments_returnsTheWholeName() {
        assertEquals("South Reach", FiefNameArgument.parse(new String[]{"\"South", "Reach\""}));
    }

    @Test
    void parse_aBareNameSpanningSeveralArguments_joinsThemWithSpaces() {
        assertEquals("South Reach", FiefNameArgument.parse(new String[]{"South", "Reach"}));
    }

    @Test
    void parse_withAQuotedNameAmongOtherWords_returnsOnlyTheQuotedPart() {
        assertEquals("South Reach", FiefNameArgument.parse(new String[]{"the", "\"South", "Reach\"", "fief"}));
    }
}
