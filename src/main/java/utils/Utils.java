package utils;

import org.opencv.core.Mat;

import java.util.concurrent.ThreadLocalRandom;


public class Utils {

    public static boolean areGrayscaleColorsSimilar(int pixel1, int pixel2) {
        return Math.abs(pixel1 - pixel2) < 7;
    }

    public static boolean isCellInsideRegion(int row, int col, int rowMin, int rowMax, int colMin, int colMax) {
        return row >= rowMin && row <= rowMax && col >= colMin && col <= colMax;
    }

    public static Integer[] generateRandomRGBColor() {
        int red = ThreadLocalRandom.current().nextInt(0, 256);
        int green = ThreadLocalRandom.current().nextInt(0, 256);
        int blue = ThreadLocalRandom.current().nextInt(0, 256);

        return new Integer[]{red, green, blue};
    }

    public static int cellToLinearIndex(int row, int col, Mat image) {
        int cols = image.cols();
        return row * cols + col;
    }
}
