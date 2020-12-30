package utils;

import java.util.concurrent.ThreadLocalRandom;


public class Utils {

    public static Integer[] generateRandomRGBColor() {
        int red = ThreadLocalRandom.current().nextInt(0, 256);
        int green = ThreadLocalRandom.current().nextInt(0, 256);
        int blue = ThreadLocalRandom.current().nextInt(0, 256);

        return new Integer[]{red, green, blue};
    }

    public static int cellToLinearIndex(int row, int col) {
        //TODO: It should be implemented
        return 1000;
    }
}
