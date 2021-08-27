package kaantelypeli.utils;

/**
 * Miscellaneous parsing utilities.
 */
public class Parsing {
    private Parsing() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * Removes prettiness from Json String.
     * @param input JSON string
     * @return input stripped from newlines and spaces
     */
    public static String uglify(String input) {
        return input.replace(System.lineSeparator(), "").replace(" ", "");
    }

    /**
     * Parse String to Integer with default value if String isn't a valid Integer.
     * @param s String to parse
     * @param defaultValue int value to default to
     * @return Parsed Integer or the default
     */
    public static int valueOfWithDefault(String s, int defaultValue) {
        return s.matches("-?\\d+") ? Integer.parseInt(s) : defaultValue;
    }
}
