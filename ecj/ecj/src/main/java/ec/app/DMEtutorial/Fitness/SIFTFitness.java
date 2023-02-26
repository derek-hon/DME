package ec.app.DMEtutorial.Fitness;

import org.opencv.core.*;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.SIFT;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SIFTFitness {

    public double siftValue;
    public double tKeypoints;

    public SIFTFitness(int[][] pixelsOne, int[][] pixelsTwo) {
        System.setProperty("java.library.path", System.getProperty("user.dir") + "/opencv/build/lib/");
        String path = System.getProperty("java.library.path");
        System.load(path + "libopencv_java470.so");


        int imageWidth = pixelsOne.length;
        int imageHeight = pixelsTwo[0].length;
        // Mat target = new Mat(imageWidth, imageHeight, CvType.CV_8U);
        // Mat solution = new Mat(imageWidth, imageHeight, CvType.CV_8U);
        int rgb = 0;

        BufferedImage targetImg = PSDExtensions.Int2DToImage(pixelsOne);
        BufferedImage solutionImg = PSDExtensions.Int2DToImage(pixelsTwo);
        Mat target = OCVExtensions.greyMatify(targetImg);
        Mat solution = OCVExtensions.greyMatify(solutionImg);

        // for (int x = 0 ; x < imageWidth ; x ++) {
        //     for (int y = 0 ; y < imageHeight ; y ++) {
        //         // rgb = pixelsOne[x][y];
        //         // target.put(x, y, ((rgb & 0xFF0000) >> 16 + (rgb & 0x00FF00) >> 8 + (rgb & 0x0000FF))/3.0);
        //         // rgb = pixelsTwo[x][y];
        //         // solution.put(x, y, ((rgb & 0xFF0000) >> 16 + (rgb & 0x00FF00) >> 8 + (rgb & 0x0000FF))/3.0);
        //         // target.put(x, y, pixelsOne[x][y]);
        //         // solution.put(x, y, pixelsTwo[x][y]);
        //     }
        // }
        
        SIFT sift = SIFT.create(0, 4, 0.04, 10, 1.6);
        MatOfKeyPoint targetKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint solutionKeyPoints = new MatOfKeyPoint();
        Mat targetDescriptors = new Mat();
        Mat solutionDescriptors = new Mat();
        
        sift.detect(target, targetKeyPoints);
        sift.detect(solution, solutionKeyPoints);

        sift.compute(target, targetKeyPoints, targetDescriptors);
        sift.compute(solution, solutionKeyPoints, solutionDescriptors);

        tKeypoints = targetKeyPoints.rows();
        
        List<MatOfDMatch> matches = new ArrayList<>();
        DescriptorMatcher descriptorMatcher = new BFMatcher(Core.NORM_L2, false);
        // DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(Core.NORM_L2);
        // targetDescriptors.convertTo(targetDescriptors, CvType.CV_32F);
        // solutionDescriptors.convertTo(solutionDescriptors, CvType.CV_32F);
        // bruteForce.match(solutionDescriptors, targetDescriptors, matches, new Mat());
        // MatOfDMatch dMatches = new MatOfDMatch();
        // bruteForce.match(solutionDescriptors, targetDescriptors, dMatches);
        // bruteForce.knnMatch(solutionDescriptors, targetDescriptors, matches, 2);
        descriptorMatcher.knnMatch(solutionDescriptors, targetDescriptors, matches, 2);

        //-- Filter matches using the Lowe's ratio test
        double ratio = 0.75;
        List<DMatch> listOfGoodMatches = new ArrayList<>();
        for (int i = 0; i < matches.size(); i++) {
            if (matches.get(i).rows() > 1) {
                DMatch[] dMatches = matches.get(i).toArray();
                if (dMatches[0].distance < ratio * dMatches[1].distance) {
                    listOfGoodMatches.add(dMatches[0]);
                }
            }
        }
        if (matches.size() == 0)
            siftValue = 100000;
        else
            siftValue = Math.abs(1.0 - listOfGoodMatches.size()/matches.size());

        // try {
        //     ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //     ImageIO.write(imageOne, "png", baos);
        //     byte[] bytes = baos.toByteArray();
        //     target.put(0, 0, bytes);

        //     ImageIO.write(imageTwo, "png", baos);
        //     bytes = baos.toByteArray();
        //     solution.put(0, 0, bytes);
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     System.out.println("Error while converting image to bytes and writing to Mat");
        // }
    }

    /**
     * https://answers.opencv.org/question/28348/converting-bufferedimage-to-mat-in-java/
     * @param sourceImg
     * @return
     */
    public Mat matify(BufferedImage sourceImg) {
        DataBuffer dataBuffer = sourceImg.getRaster().getDataBuffer();
        byte[] imgPixels = null;
        Mat imgMat = null;
    
        int width = sourceImg.getWidth();
        int height = sourceImg.getHeight();
    
        if(dataBuffer instanceof DataBufferByte) {      
                imgPixels = ((DataBufferByte)dataBuffer).getData();
        }
    
        if(dataBuffer instanceof DataBufferInt) {
    
            int byteSize = width * height;      
            imgPixels = new byte[byteSize*3];
    
            int[] imgIntegerPixels = ((DataBufferInt)dataBuffer).getData();
    
            for(int p = 0; p < byteSize; p++) {         
                imgPixels[p*3 + 0] = (byte) ((imgIntegerPixels[p] & 0x00FF0000) >> 16);
                imgPixels[p*3 + 1] = (byte) ((imgIntegerPixels[p] & 0x0000FF00) >> 8);
                imgPixels[p*3 + 2] = (byte) (imgIntegerPixels[p] & 0x000000FF);
            }
        }
    
        if(imgPixels != null) {
            imgMat = new Mat(height, width, CvType.CV_8UC3);
            imgMat.put(0, 0, imgPixels);
        }
    
        return imgMat;
    }

    public double getSIFT() {
        return this.siftValue;
    }

    public double getTKeyPoints() {
        return this.tKeypoints;
    }
}


