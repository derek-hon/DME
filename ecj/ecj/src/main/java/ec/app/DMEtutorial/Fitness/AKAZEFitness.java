package ec.app.DMEtutorial.Fitness;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.imgcodecs.Imgcodecs;

public class AKAZEFitness {

    double totalMatches = 0.0;
    /**
     * https://docs.opencv.org/4.x/db/d70/tutorial_akaze_matching.html
     * @param targetPixels
     * @param solutionPixels
     */    
    public AKAZEFitness(int[][] targetPixels, int[][] solutionPixels) {
        // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        System.setProperty("java.library.path", System.getProperty("user.dir") + "/opencv/build/lib/");
        String path = System.getProperty("java.library.path");
        System.load(path + "libopencv_java470.so");

        int imageWidth = targetPixels.length;

        BufferedImage targetImage = PSDExtensions.Int2DToImage(targetPixels);
        BufferedImage solutionImage = PSDExtensions.Int2DToImage(solutionPixels);

        Mat target = OCVExtensions.matify(targetImage);
        Mat solution = OCVExtensions.matify(solutionImage);
        // Mat homography = Calib3d.findHomography(null, null)

        // Mat target = null;
        // Mat solution = null;
        
        // try {
        //     target = BufferedImage2Mat(targetImage);
        //     solution = BufferedImage2Mat(solutionImage);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        
        // byte[] targetBytes = ((DataBufferByte) targetImage.getRaster().getDataBuffer()).getData();
        // byte[] solutionBytes = ((DataBufferByte) solutionImage.getRaster().getDataBuffer()).getData();

        // Mat target = new Mat();
        // target.put(0, 0, targetBytes);
        // Mat solution = new Mat();
        // solution.put(0, 0, solutionBytes);

        // Mat target = new Mat(imageWidth, imageWidth, CvType.CV_8U);
        // Mat solution = new Mat(imageWidth, imageWidth, CvType.CV_8U);
        // double grey = 0;
        // int rgb = 0;

        // for (int y = 0 ; y < imageWidth ; y ++) {
        //     for (int x = 0 ; x < imageWidth ; x ++) {
        //         rgb = targetPixels[x][y];
        //         grey = ((rgb & 0xFF0000) >> 16) + ((rgb & 0x00FF00) >> 8) + (rgb & 0x0000FF)/3.0;
        //         target.put(y, x, grey);
                
        //         rgb = solutionPixels[x][y];
        //         grey = ((rgb & 0xFF0000) >> 16) + ((rgb & 0x00FF00) >> 8) + (rgb & 0x0000FF)/3.0;
        //         solution.put(x, y, grey);
        //     }
        // }

        AKAZE akaze = AKAZE.create();
        MatOfKeyPoint targetKP = new MatOfKeyPoint();
        MatOfKeyPoint solutionKP = new MatOfKeyPoint();
        Mat TargetDescriptor = new Mat();
        Mat SolutionDescriptor = new Mat();

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        List<MatOfDMatch> knnMatches = new ArrayList<>();
        
        float ratioThreshold = 0.8f; // Nearest neighbor matching ratio
        List<KeyPoint> listOfTargetMatched = new ArrayList<>();
        List<KeyPoint> listOfSolutionMatched = new ArrayList<>();
        List<KeyPoint> listOfTargetKeypoints = new ArrayList<>();
        List<KeyPoint> listOfSolutionKeypoints = new ArrayList<>();

        akaze.detectAndCompute(target, new Mat(), targetKP, TargetDescriptor);
        akaze.detectAndCompute(solution, new Mat(), solutionKP, SolutionDescriptor);

        matcher.knnMatch(SolutionDescriptor, TargetDescriptor, knnMatches, 2);

        for (int i = 0 ; i < knnMatches.size() ; i ++) {

            if (knnMatches.get(i).rows() > 1) {
                DMatch[] matches = knnMatches.get(i).toArray();
                if (matches[0].distance < ratioThreshold * matches[1].distance) {
                    listOfSolutionMatched.add(listOfSolutionKeypoints.get(matches[0].queryIdx));
                    listOfTargetMatched.add(listOfTargetKeypoints.get(matches[0].trainIdx));
                }
            }
        }

        if (listOfSolutionMatched.size() == 0 || listOfTargetMatched.size() == 0)
            totalMatches = 10000;
        else
            totalMatches = listOfSolutionMatched.size() / listOfTargetMatched.size();
    }

    public Mat BufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
    }

    public double getMatches() {
        return totalMatches;
    }
}
