package ec.app.DMEtutorial.Fitness;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MSSIM {

    double C1 = 6.5025, C2 = 58.5225;
    int d     = CvType.CV_32F;
    double mssimValue = 0;


    /**
     * https://docs.opencv.org/4.x/dd/d3d/tutorial_gpu_basics_similarity.html
     * @param targetPixels
     * @param solutionPixels
     */
    public MSSIM(int[][] targetPixels, int[][] solutionPixels) {
        System.setProperty("java.library.path", System.getProperty("user.dir") + "/opencv/build/lib/");
        String path = System.getProperty("java.library.path");
        System.load(path + "libopencv_java470.so");

        int imageWidth = targetPixels.length;

        Mat target = new Mat(imageWidth, imageWidth, CvType.CV_8U);
        Mat solution = new Mat(imageWidth, imageWidth, CvType.CV_8U);
        double grey = 0;
        int rgb = 0;

        for (int y = 0 ; y < imageWidth ; y ++) {
            for (int x = 0 ; x < imageWidth ; x ++) {
                rgb = targetPixels[x][y];
                grey = ((rgb & 0xFF0000) >> 16) + ((rgb & 0x00FF00) >> 8) + (rgb & 0x0000FF)/3.0;
                target.put(y, x, grey);
                
                rgb = solutionPixels[x][y];
                grey = ((rgb & 0xFF0000) >> 16) + ((rgb & 0x00FF00) >> 8) + (rgb & 0x0000FF)/3.0;
                solution.put(x, y, grey);
            }
        }

        Mat TargetProcessed = new Mat(), SolutionProcessed = new Mat();
        target.convertTo(TargetProcessed, d);
        solution.convertTo(SolutionProcessed, d);

        /** TP = Target processed */
        Mat TPSquared = TargetProcessed.mul(TargetProcessed);
        
        /** SP = solution processed */
        Mat SPSquared = SolutionProcessed.mul(SolutionProcessed);

        /** TPSP = target processed x solution processed */
        Mat TPSP = TargetProcessed.mul(SolutionProcessed);
        
        Mat targetMu = new Mat(), solutionMu = new Mat();
        Imgproc.GaussianBlur(TargetProcessed, targetMu, new Size(11, 11), 1.5);
        Imgproc.GaussianBlur(SolutionProcessed, solutionMu, new Size(11, 11), 1.5);
        
        /** TM = target mu */
        Mat TMSquared = targetMu.mul(targetMu);

        /** SM = solution mu */
        Mat SMSquared = solutionMu.mul(solutionMu);

        /** TMSM = target mu x solution mu */
        Mat TMSM = targetMu.mul(solutionMu);

        /** TS = Target Sigma */
        Mat TSSquared = new Mat();
        
        /** SS = Solution Sigma */
        Mat SSSquared = new Mat();

        /** TSSS = Target Sigma * Solution Sigma */
        Mat TSSS = new Mat();
        
        Imgproc.GaussianBlur(TPSquared, TSSquared, new Size(11, 11), 1.5);
        Core.subtract(TSSquared, TMSquared, TSSquared);

        Imgproc.GaussianBlur(SPSquared, SSSquared, new Size(11, 11), 1.5);
        Core.subtract(SSSquared, SMSquared, SSSquared);

        Imgproc.GaussianBlur(TPSP, TSSS, new Size(11, 11), 1.5);
        Core.subtract(TSSS, TMSM, TSSS);
        
        Mat t1 = new Mat(), t2 = new Mat(), t3 = new Mat();

        Core.multiply(TMSM, new Scalar(2), t1);
        Core.add(t1, new Scalar(C1), t1);

        Core.multiply(TSSS, new Scalar(2), t2);
        Core.add(t2, new Scalar(C2), t2);
        
        t3 = t1.mul(t2); // t3 = ((2*mu1_mu2 + C1).*(2*sigma12 + C2))

        Core.add(TMSquared, SMSquared, t1);
        Core.add(t1, new Scalar(C1), t1);

        Core.add(TMSquared, SMSquared, t2);
        Core.add(t2, new Scalar(C2), t2);
        t1 = t1.mul(t2); // t1 =((mu1_2 + mu2_2 + C1).*(sigma1_2 + sigma2_2 + C2))

        Mat SSIM_Map = new Mat();
        Core.divide(t3, t1, SSIM_Map); // ssim_map =  t3./t1;

        Scalar mssim = Core.mean(SSIM_Map); // mssim = average of ssim map
        mssimValue = mssim.val[0];
    }

    public double getMSSIM() {
        return this.mssimValue;
    }
}
