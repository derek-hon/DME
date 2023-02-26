package ec.app.DMEtutorial.Fitness;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

public class OCVExtensions {

    /**
     * https://answers.opencv.org/question/28348/converting-bufferedimage-to-mat-in-java/
     * @param sourceImg
     * @return
     */
    public static Mat matify(BufferedImage sourceImg) {
        System.setProperty("java.library.path", System.getProperty("user.dir") + "/opencv/build/lib/");
        String path = System.getProperty("java.library.path");
        System.load(path + "libopencv_java470.so");

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

    /**
     * https://answers.opencv.org/question/28348/converting-bufferedimage-to-mat-in-java/
     * @param sourceImg
     * @return
     */
    public static Mat greyMatify(BufferedImage sourceImg) {
        System.setProperty("java.library.path", System.getProperty("user.dir") + "/opencv/build/lib/");
        String path = System.getProperty("java.library.path");
        System.load(path + "libopencv_java470.so");

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
            imgPixels = new byte[byteSize];
    
            int[] imgIntegerPixels = ((DataBufferInt)dataBuffer).getData();
    
            for(int p = 0; p < byteSize; p++) {         
                imgPixels[p] = (byte) ((((imgIntegerPixels[p] & 0x00FF0000) >> 16) + ((imgIntegerPixels[p] & 0x0000FF00) >> 8) + (imgIntegerPixels[p] & 0x000000FF))/3);
                // imgPixels[p*3 + 1] = (byte) ;
                // imgPixels[p*3 + 2] = (byte) ;
            }
        }
    
        if(imgPixels != null) {
            imgMat = new Mat(height, width, CvType.CV_8UC1);
            imgMat.put(0, 0, imgPixels);
        }
    
        return imgMat;
    }
    
}
