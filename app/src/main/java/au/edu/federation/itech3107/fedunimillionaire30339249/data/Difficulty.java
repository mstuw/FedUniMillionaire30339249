package au.edu.federation.itech3107.fedunimillionaire30339249.data;

public enum Difficulty {
    EASY,
    MEDIUM,
    HARD;

    /**
     * Returns the Difficulty matching the provided name. Similar to Enum.valueOf() but case insensitive.
     */
    public static Difficulty valueOfIgnoreCase(String name) {
        for (Difficulty t : Difficulty.values()) {
            if (t.name().equalsIgnoreCase(name))
                return t;
        }
        return null;
    }

}