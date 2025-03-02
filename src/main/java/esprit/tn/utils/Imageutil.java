package esprit.tn.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.nio.ByteBuffer;

public class Imageutil {

    public static Image mat2Image(Mat frame) {
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);
        WritableImage image = new WritableImage(frame.cols(), frame.rows());
        PixelWriter pw = image.getPixelWriter();
        ByteBuffer buffer = ByteBuffer.allocate(frame.channels() * frame.cols() * frame.rows());
        frame.get(0, 0, buffer.array());

        for (int y = 0; y < frame.rows(); y++) {
            for (int x = 0; x < frame.cols(); x++) {
                int index = (y * frame.cols() + x) * 3;
                int r = buffer.get(index) & 0xFF;
                int g = buffer.get(index + 1) & 0xFF;
                int b = buffer.get(index + 2) & 0xFF;
                pw.setColor(x, y, javafx.scene.paint.Color.rgb(r, g, b));
            }
        }
        return image;
    }
}
