package concurrent.algorithms.components;

import lombok.NoArgsConstructor;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import concurrent.algorithms.utils.Utils;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@NoArgsConstructor
public class ComponentsDrawer {

    private static final int MIN_PIXELS_INSIDE_COMPONENT = 10;

    public void drawAndSave(String imagePath, int numberOfThreads, int similarity, int clusters, int compMinSize, boolean verbose) {
        if (compMinSize == -1) {
            compMinSize = MIN_PIXELS_INSIDE_COMPONENT;
        }

        Mat colorImage = Imgcodecs.imread(imagePath);
        if (clusters > 0) {
            colorImage = KMeansClustering.clusterize(colorImage, clusters);
        }
        Mat grayscaleImage = new Mat();
        Imgproc.cvtColor(colorImage, grayscaleImage, Imgproc.COLOR_BGR2GRAY);

        ComponentsFinder componentsFinder = new ComponentsFinder(numberOfThreads, grayscaleImage, similarity, verbose);
        componentsFinder.execute();

        Map<Integer, Integer> compCounter = countComponents(grayscaleImage, componentsFinder);
        Map<Integer, Integer[]> colorMap = new HashMap<>();

        Mat resultImage = new Mat(grayscaleImage.rows(), grayscaleImage.cols(), CvType.CV_32SC3);
        for (int row = 0; row < grayscaleImage.rows(); ++row) {
            for (int col = 0; col < grayscaleImage.cols(); ++col) {
                int pixelComponent = componentsFinder.getPixelComponent(row, col);
                if (compCounter.get(pixelComponent) >= compMinSize) {
                    resultImage.put(row, col, getColor(colorMap, pixelComponent));
                } else {
                    resultImage.put(row, col, new int[]{0, 0, 0});
                }
            }
        }

        System.out.println("Number of components: " + colorMap.size());
        saveImages(imagePath, grayscaleImage, colorImage, resultImage);
    }

    private void saveImages(String imagePath, Mat grayscaleImage, Mat clusterizedImage, Mat resultImage) {
        File directory = new File("results");
        if (! directory.exists()){
            directory.mkdir();
        }
        String[] tokens = imagePath.split(File.separator);
        String imageName = tokens[tokens.length - 1];
        Imgcodecs.imwrite("results" + File.separator + "grayscale_" + imageName, grayscaleImage);
        Imgcodecs.imwrite("results" + File.separator + "components_" + imageName, resultImage);
        Imgcodecs.imwrite("results" + File.separator + "clusters_" + imageName, clusterizedImage);
    }

    private int[] getColor(Map<Integer, Integer[]> colorMap, int componentId) {
        if (!colorMap.containsKey(componentId)) {
            colorMap.put(componentId, Utils.generateRandomRGBColor());
        }

        return Arrays.stream(colorMap.get(componentId)).mapToInt(Integer::intValue).toArray();
    }

    private Map<Integer, Integer> countComponents(Mat image, ComponentsFinder componentsFinder) {
        Map<Integer, Integer> compCount = new HashMap<>();
        for (int row = 0; row < image.rows(); ++row) {
            for (int col = 0; col < image.cols(); ++col) {
                int pixelComponent = componentsFinder.getPixelComponent(row, col);
                compCount.put(pixelComponent, compCount.getOrDefault(pixelComponent, 0) + 1);
            }
        }

        return compCount;
    }
}
