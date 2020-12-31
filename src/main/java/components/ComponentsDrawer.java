package components;

import lombok.NoArgsConstructor;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@NoArgsConstructor
public class ComponentsDrawer {

    private static final int MIN_PIXELS_INSIDE_COMPONENT = 10;

    public void drawAndSave(String imagePath, int numberOfThreads) {
        Mat image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);

        ComponentsFinder componentsFinder = new ComponentsFinder(numberOfThreads, image);
        int[] components = componentsFinder.findComponents();

        Map<Integer, Integer> compCounter = countComponents(components);
        Map<Integer, Integer[]> colorMap = new HashMap<>();

        Mat resultImage = new Mat(image.rows(), image.cols(), CvType.CV_32SC3);
        for (int row = 0; row < image.rows(); ++row) {
            for (int col = 0; col < image.cols(); ++col) {
                int linearIndex = Utils.cellToLinearIndex(row, col, image);
                if (compCounter.get(components[linearIndex]) >= ComponentsDrawer.MIN_PIXELS_INSIDE_COMPONENT) {
                    resultImage.put(row, col, getColor(colorMap, components[linearIndex]));
                } else {
                    resultImage.put(row, col, new int[]{0, 0, 0});
                }
            }
        }

        System.out.println("Number of components: " + colorMap.size());
        String[] tokens = imagePath.split("/");
        String imageName = tokens[tokens.length - 1];
        Imgcodecs.imwrite("results/grayscale_" + imageName, image);
        Imgcodecs.imwrite("results/components_" + imageName, resultImage);
    }

    private int[] getColor(Map<Integer, Integer[]> colorMap, int componentId) {
        if (!colorMap.containsKey(componentId)) {
            colorMap.put(componentId, Utils.generateRandomRGBColor());
        }

        return Arrays.stream(colorMap.get(componentId)).mapToInt(Integer::intValue).toArray();
    }

    private Map<Integer, Integer> countComponents(int[] components) {
        Map<Integer, Integer> compCount = new HashMap<>();
        for (int comp: components) {
            compCount.put(comp, compCount.getOrDefault(comp, 0) + 1);
        }

        return compCount;
    }
}
