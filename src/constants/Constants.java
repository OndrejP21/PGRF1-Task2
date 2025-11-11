package constants;

public class Constants {
    public static final int COLOR = 0xffffff;
    public static final int RED_COLOR = 0xff0000;
    public static final int MAX_FINDING_RADIUS = 100; // radius pro vyhledávání nejbližšího bodu
    public static final int PATTERN_COLOR_1 = 0x0000FF;
    public static final int PATTERN_COLOR_2 = 0x00FF00;

    /** Pattern pro vyplňování vzorem */
    public static final int[][] PATTERN = {
            { PATTERN_COLOR_1, PATTERN_COLOR_2, PATTERN_COLOR_2, PATTERN_COLOR_2, PATTERN_COLOR_1 },
            { PATTERN_COLOR_2, PATTERN_COLOR_1, PATTERN_COLOR_2, PATTERN_COLOR_1, PATTERN_COLOR_2 },
            { PATTERN_COLOR_2, PATTERN_COLOR_2, PATTERN_COLOR_1, PATTERN_COLOR_2, PATTERN_COLOR_2 },
            { PATTERN_COLOR_2, PATTERN_COLOR_1, PATTERN_COLOR_2, PATTERN_COLOR_1, PATTERN_COLOR_2 },
            { PATTERN_COLOR_1, PATTERN_COLOR_2, PATTERN_COLOR_2, PATTERN_COLOR_2, PATTERN_COLOR_1 }
    };
}
