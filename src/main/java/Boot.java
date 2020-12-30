import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


public class Boot {

    public static void main(String[] args) {
        nu.pattern.OpenCV.loadShared();

        System.out.println("Connected components started.");
        System.out.println("Version: " + Core.VERSION);

        String file ="images/squares.jpg";
        Mat matrix = Imgcodecs.imread(file, Imgcodecs.IMREAD_GRAYSCALE);
        System.out.println("Image loaded. " + matrix.rows() + "x" + matrix.cols() + ", dims: " + matrix.dims() + ", channels: " + matrix.channels());
        Imgcodecs.imwrite("results/squares.jpg", matrix);
    }
}
