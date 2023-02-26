package ec.app.DMEtutorial.Fitness;

import org.opencv.core.*;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.xfeatures2d.SURF;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SURFFitness {

    public double surfValue;
    public double tKeypoints;

    public SURFFitness(int[][] targetPixels, int[][] solutionPixels) {
        System.setProperty("java.library.path", System.getProperty("user.dir") + "/opencv/build/lib/");
        String path = System.getProperty("java.library.path");
        System.load(path + "libopencv_java470.so");
        

        int imageWidth = targetPixels.length;
        int imageHeight = solutionPixels[0].length;
        // Mat target = new Mat(imageWidth, imageHeight, CvType.CV_8U);
        // Mat solution = new Mat(imageWidth, imageHeight, CvType.CV_8U);
        int rgb = 0;

        BufferedImage targetImg = PSDExtensions.Int2DToImage(targetPixels);
        BufferedImage solutionImg = PSDExtensions.Int2DToImage(solutionPixels);
        Mat target = OCVExtensions.matify(targetImg);
        Mat solution = OCVExtensions.matify(solutionImg);

        //-- Step 1: Detect the keypoints using SURF Detector, compute the descriptors
        double hessianThreshold = 400;
        int nOctaves = 4, nOctaveLayers = 3;
        boolean extended = false, upright = false;
        
        SURF detector = SURF.create(hessianThreshold, nOctaves, nOctaveLayers, extended, upright);
        MatOfKeyPoint targetKP = new MatOfKeyPoint(), solutionKP = new MatOfKeyPoint();
        Mat targetDesc = new Mat(), solutionDesc = new Mat();
        
        detector.detectAndCompute(target, new Mat(), targetKP, targetDesc);
        detector.detectAndCompute(solution, new Mat(), solutionKP, solutionDesc);

        //-- Step 2: Matching descriptor vectors with a FLANN based matcher
        // Since SURF is a floating-point descriptor NORM_L2 is used
        // DescriptorMatcher matcher = FlannBasedMatcher.create();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        List<MatOfDMatch> knnMatches = new ArrayList<>();
        solutionDesc.convertTo(solutionDesc, CvType.CV_32F);
        targetDesc.convertTo(targetDesc, CvType.CV_32F);
        matcher.knnMatch(solutionDesc, targetDesc, knnMatches, 2);

        //-- Filter matches using the Lowe's ratio test
        float ratioThresh = 0.7f;
        List<DMatch> listOfGoodMatches = new ArrayList<>();
        for (int i = 0; i < knnMatches.size(); i++) {
            if (knnMatches.get(i).rows() > 1) {
                DMatch[] matches = knnMatches.get(i).toArray();
                if (matches[0].distance < ratioThresh * matches[1].distance) {
                    listOfGoodMatches.add(matches[0]);
                }
            }
        }
        System.out.println(listOfGoodMatches.size() + "\t" + knnMatches.size() + "\t" + targetKP.rows() + "\t" + targetKP.elemSize());
        this.surfValue = listOfGoodMatches.size() / knnMatches.size();
    }

    public double getSurfValue() {
        return this.surfValue;
    }
}


