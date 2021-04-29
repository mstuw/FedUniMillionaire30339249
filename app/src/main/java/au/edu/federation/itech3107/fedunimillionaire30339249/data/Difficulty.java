package au.edu.federation.itech3107.fedunimillionaire30339249.data;

public enum Difficulty {
    EASY(.1f, .75f),
    MEDIUM(.3f, .5f),
    HARD(.6f, .3f);

    private final float spread; // how much each of the percentages will vary between each answer.
    private final float correctAnswerWeighting; // how much the correct answer will change the percentage.

    Difficulty(float spread, float correctAnswerWeighting) {
        this.spread = spread;
        this.correctAnswerWeighting = correctAnswerWeighting;
    }

    /**
     * How much each of the percentages will vary between each answer.
     */
    public float getSpread() {
        return spread;
    }

    /**
     * How much the correct answer will change the percentage.
     */
    public float getCorrectAnswerWeighting() {
        return correctAnswerWeighting;
    }

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