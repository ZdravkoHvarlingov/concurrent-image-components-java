package concurrent.algorithms.components;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;


public class KMeansClustering {

    public static Mat clusterize(Mat initialRGBImage, int numberOfClusters) {
        return performKMeans(initialRGBImage, numberOfClusters);
    }

    private static Mat performKMeans(Mat image, int k) {
        Mat samples = image.reshape(1, image.cols() * image.rows());
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 3, Core.KMEANS_PP_CENTERS, centers);

        return drawClusters(image, labels, centers);
    }

    private static Mat drawClusters(Mat image, Mat labels, Mat centers) {
        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(3);

        Mat clusters = Mat.zeros(image.size(), image.type());
        int labelRow = 0;
        for(int row = 0; row < image.rows(); row++) {
            for(int col = 0; col < image.cols(); col++) {
                int label = (int)labels.get(labelRow, 0)[0];
                int red = (int)centers.get(label, 2)[0];
                int green = (int)centers.get(label, 1)[0];
                int blue = (int)centers.get(label, 0)[0];

                clusters.put(row, col, blue, green, red);
                labelRow++;
            }
        }

        return clusters;
    }
}
