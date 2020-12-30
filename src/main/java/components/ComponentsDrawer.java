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

    public void drawAndSave(String imagePath) {
        //        numpy_image = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
        //
        //        components_finder = ComponentsFinder(4, numpy_image)
        //        components = components_finder.find_components()
        //        comp_count = self.component_counter(components)

        Mat image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);

        ComponentsFinder componentsFinder = new ComponentsFinder();
        int[] components = componentsFinder.findComponents();

        Map<Integer, Integer> compCounter = countComponents(components);
        Map<Integer, Integer[]> colorMap = new HashMap<>();

        Mat resultImage = new Mat(image.rows(), image.cols(), CvType.CV_64FC1);
        for (int row = 0; row < image.rows(); ++row) {
            for (int col = 0; col < image.cols(); ++col) {
                int linearIndex = Utils.cellToLinearIndex(row, col);
                if (compCounter.get(components[linearIndex]) > ComponentsDrawer.MIN_PIXELS_INSIDE_COMPONENT) {
                    resultImage.put(row, col, getColor(colorMap, components[linearIndex]));
                } else {
                    resultImage.put(row, col, new int[]{0, 0, 0});
                }
            }
        }

        //        print(f'Number of components: {len(color_map)}')
        //        image_name = image_path.split('/')[-1]
        //        cv2.imwrite(f'results/grayscale_{image_name}', numpy_image)
        //        cv2.imwrite(f'results/{image_name}', result_rgb_image)
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
