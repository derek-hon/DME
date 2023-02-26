package ec.app.DMEtutorial.BehaviourEvaluations;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.imgproc.Imgproc;

import ec.app.DMEtutorial.Fitness.OCVExtensions;
import ec.app.DMEtutorial.Fitness.PSDExtensions;

public class GeometryEvaluationFunctions {

    /**
     * Probablistic Hough Line Transform from OpenCV
     * https://docs.opencv.org/4.x/d9/db0/tutorial_hough_lines.html
     * @param pixels
     * @return
     */
    public static double LineDetection(int[][] pixels) {
        double linesDetected = 0.0;

        int imageWidth = pixels.length;
        int imageHeight = pixels[0].length;
        Mat image = new Mat(imageWidth, imageHeight, CvType.CV_8U);
        Mat cannyEdge = new Mat();
        Mat lines = new Mat();
        int rgb = 0;
        double grey = 0;

        for (int x = 0 ; x < imageWidth ; x ++) {
            for (int y = 0 ; y < imageHeight ; y ++) {
                rgb = pixels[x][y];
                grey = ((rgb & 0xFF0000) >> 16) + ((rgb & 0x00FF00) >> 8) + (rgb & 0x0000FF)/3.0;
                image.put(x, y, grey);
            }
        }
        
        Imgproc.Canny(image, cannyEdge, 50, 200, 3, false);
        Imgproc.HoughLinesP(cannyEdge, lines, 1, Math.PI/180, 50, 50, 10 );
        linesDetected = lines.rows();
        
        // FastLineDetector detector = Ximgproc.createFastLineDetector();
        // detector.detect(image, lines);

        // linesDetected = lines.cols();
        
        return linesDetected;
    }

    /**
     * used sobel instead of canny
     * https://docs.opencv.org/4.x/df/d0d/tutorial_find_contours.html
     * @param pixels
     * @return
     */
    public static double ContourDetection(int[][] pixels) {
        double contoursDetected = 0.0;

        int imageWidth = pixels.length;
        int imageHeight = pixels[0].length;
        Mat image = OCVExtensions.greyMatify(PSDExtensions.Int2DToImage(pixels));
        List<MatOfPoint> contours = new ArrayList<>();
        int rgb = 0;
        double grey = 0;

        Mat sobelX = new Mat();
        Mat sobelY = new Mat();
        Mat absX = new Mat();
        Mat absY = new Mat();
        Mat sobelResult = new Mat();
        Mat heirarchy = new Mat();

        Imgproc.Sobel(image, sobelX, CvType.CV_64F, 1, 0, 3);
        Imgproc.Sobel(image, sobelY, CvType.CV_64F, 0, 1, 3);
        Core.convertScaleAbs(sobelX, absX);
        Core.convertScaleAbs(sobelY, absY);
        Core.addWeighted(absX, 0.5, absY, 0.5, 0, sobelResult);

        Imgproc.findContours(sobelResult, contours, heirarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        contoursDetected = contours.size();

        return contoursDetected;
    }

    public static double CornerDetection(int[][] pixels) {
        double edgeCount = 0.0;

        int imageWidth = pixels.length;
        int imageHeight = pixels[0].length;
        Mat image = OCVExtensions.greyMatify(PSDExtensions.Int2DToImage(pixels));
        
        Mat sobelX = new Mat();
        Mat sobelY = new Mat();
        Mat absX = new Mat();
        Mat absY = new Mat();
        Mat sobelResult = new Mat();
        MatOfPoint corners = new MatOfPoint();

        Imgproc.Sobel(image, sobelX, CvType.CV_64F, 1, 0, 3);
        Imgproc.Sobel(image, sobelY, CvType.CV_64F, 0, 1, 3);
        Core.convertScaleAbs(sobelX, absX);
        Core.convertScaleAbs(sobelY, absY);
        Core.addWeighted(absX, 0.5, absY, 0.5, 0, sobelResult);

        Imgproc.goodFeaturesToTrack(sobelResult, corners, Integer.MAX_VALUE, 0.5, 10, new Mat(), 3, 3, false, 0.04);
        int[] cornersData = new int[(int)(corners.total() * corners.channels())];
        corners.get(0, 0, cornersData);
        edgeCount = corners.rows();

        return edgeCount;
    }

    /**
     * Experimental detection
     * @param pixels
     * @return
     */
    public static double BlobDetection(int[][] pixels) {
        double blobCount = 0.0;

        int imageWidth = pixels.length;
        int imageHeight = pixels[0].length;
        Mat image = OCVExtensions.greyMatify(PSDExtensions.Int2DToImage(pixels));
        int rgb = 0;
        double grey = 0;

        SimpleBlobDetector_Params params = new SimpleBlobDetector_Params();
        params.set_minThreshold(10);
        params.set_maxThreshold(200);
        params.set_filterByArea(true);
        params.set_minArea(1500);
        params.set_filterByCircularity(true);
        params.set_minCircularity(0.1f);
        params.set_filterByConvexity(true);
        params.set_minConvexity(0.87f);
        params.set_filterByInertia(true);
        params.set_minInertiaRatio(0.01f);

        SimpleBlobDetector blobDetector = SimpleBlobDetector.create(params);
        MatOfKeyPoint keypoints = new MatOfKeyPoint();

        blobDetector.detect(image, keypoints);
        blobCount = keypoints.rows();

        return blobCount;
    }
}
