package ec.app.DMEtutorial.Fitness;

import ij.*;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Multi-Scale SSIM implementation by: Gabriel Prieto Renieblas (gprietor at med.ucm.es)
 * Department of Radiology, Faculty of Medicine, Complutense University
 * Madrid, Spain
 * 
 * Edited out everything unrelated to the core algorithm
 * 
 * https://imagej.nih.gov/ij/plugins/mssim-index.html
 */
public class MultiScaleSSIM {
    protected ImagePlus imagePlusOne, imagePlusTwo;
	protected ImageProcessor imageProcessorOne, imageProcessorTwo;

    int  pointer, filter_length, image_height, image_width, image_dimension, a, b, c;
    //Bits per pixel for both images
    int bppOne, bppTwo;
	float[] filter_weights;
	double[] luminanceExponent = { 1, 1, 1, 1, 1, 0.1333};
	double[] contrastExponent  = { 1, 0.0448, 0.2856, 0.3001, 0.2363, 0.1333};
	double[] structureExponent = { 1, 0.0448, 0.2856, 0.3001, 0.2363, 0.1333};
	double luminanceComparison = 1;
	double contrastComparison = 1;
	double structureComparison = 1;
	double multiscaleSSIMIndex;
	double[] ssim_map;
	double ssim_index;

    double sigma_gauss = 1.5;
	int filter_width = 11;
	double K1 = 0.01; 
	double K2 = 0.03;
	double[] lod = {0.0378, -0.0238, -0.1106, 0.3774, 0.8527, 0.3774, -0.1106, -0.0238, 0.0378};

    double C1;
    double C2;

    double[] array_gauss_window;
    float[] window_weights;

    float[] lpf;
    int lpfWidth;

    int imageWidth, imageHeight, imageDimension;

    public MultiScaleSSIM(int[][] imageOnePixels, int[][] imageTwoPixels) {
        imageWidth = imageOnePixels.length;
        imageHeight = imageOnePixels[0].length;
        BufferedImage imageOne = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB), 
                      imageTwo = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        for (int x = 0 ; x < imageWidth ; x ++) {
            for (int y = 0 ; y < imageHeight ; y ++) {
                imageOne.setRGB(x, y, imageOnePixels[x][y]);
                imageTwo.setRGB(x, y, imageOnePixels[x][y]);
            }
        }

        imagePlusOne = new ImagePlus("", imageOne);
        imagePlusTwo = new ImagePlus("", imageTwo);
        imageProcessorOne = imagePlusOne.getProcessor();
        imageProcessorTwo = imagePlusTwo.getProcessor();

        bppOne = imageProcessorOne.getBitDepth();
        bppTwo = imageProcessorTwo.getBitDepth();

        C1 = (Math.pow(2, bppOne) - 1.0) * K1;
        C1 *= C1;
        C2 = (Math.pow(2, bppOne) - 1.0) * K2;
        C2 *= C2;

        GaussianFilter();
        LowPassFilter();
        algorithm();
    }

    public double getMultiScaleSSIMValue() {
        return multiscaleSSIMIndex;
    }

    private void GaussianFilter() {
        double distance = 0;
        double total = 0;
        int center = (filter_width / 2);
        double sigma_sq = sigma_gauss * sigma_gauss;

        filter_length = filter_width * filter_width;
        window_weights = new float [filter_length];
        array_gauss_window = new double [filter_length];
		
      	for (int y = 0; y < filter_width ; y ++) {
			for (int x = 0; x < filter_width ; x ++) {
         				distance = Math.abs(x - center) * Math.abs(x - center) + Math.abs(y - center) * Math.abs(y - center);
				pointer = y * filter_width + x;
                			array_gauss_window[pointer] = Math.exp(-0.5 * distance / sigma_sq);
				total = total + array_gauss_window[pointer];
  			}
    	}
		for (pointer=0; pointer < filter_length; pointer++) {	
			array_gauss_window[pointer] = array_gauss_window[pointer] / total;
			window_weights [pointer] = (float) array_gauss_window[pointer];
		}
    }

    private void LowPassFilter() {
        lpf = new float [81]; 
        int lpfWidth = 9;
        float suma_lpf = 0;
        int cont = 0;
        
        for (a = 0; a < lpfWidth ; a ++) {
            for (b = 0; b < lpfWidth ; b ++) {
                lpf [a * lpfWidth + b] = (float) (lod[a] * lod[b]);
            }
        }
        
        for (cont = 0; cont < 81 ; cont ++) {
            suma_lpf= suma_lpf + lpf[cont];
        }
        for (cont = 0 ; cont < 81 ; cont ++) {
            lpf[cont]= lpf[cont] / suma_lpf;
        }        
    }

    private void algorithm() {
        ImageProcessor image1OriginalProcessor = imagePlusOne.getProcessor();
        ImageProcessor image2OriginalProcessor = imagePlusTwo.getProcessor();

        image1OriginalProcessor.setInterpolate(false);
        image2OriginalProcessor.setInterpolate(false);

        // WE ARE GOING TO USE ARRAYS OF 6 LEVELS INSTEAD OF 5.
        // WE WANT TO FORCE THAT THE INDEX OVER THE LEVEL WERE THE SAME THAN THE INDEX OVER THE ARRAY. 
        // REMEMBER THAT IN JAVA THE FIRST INDEX OF AN ARRAY IS THE "0" POSITION. WE WILL NEVER USE THIS POSITION IN THE FOLLOWING THREE ARRAYS.

        int level = 1;
        double[] contrast = new double[6];  
        double[] structure = new double[6];
        double[] luminance = new double[6];

        for (level = 1 ; level <= 5 ; level ++) {
            if (level != 1) {
                imageProcessorOne.convolve (lpf, lpfWidth, lpfWidth);
                imageProcessorTwo.convolve (lpf, lpfWidth, lpfWidth);
                imageProcessorOne.setInterpolate(false);			// IT'S CRITICAL TO THIS VALUE. DON'T USE TRUE
                imageProcessorTwo.setInterpolate(false);
                imageProcessorOne = imageProcessorOne.resize (imageWidth / 2);
                imageProcessorTwo = imageProcessorTwo.resize (imageWidth/2);
            }

            imageDimension = imageWidth * imageHeight;

            ImageProcessor mu1ImageProcessor = new FloatProcessor(imageWidth, imageHeight);
            ImageProcessor mu2ImageProcessor = new FloatProcessor(imageWidth, imageHeight);
            float[] mu1Values = (float[]) mu1ImageProcessor.getPixels();
            float[] mu2Values = (float[]) mu2ImageProcessor.getPixels();
            float[] mu1ValuesCopy = new float[imageDimension];
            float[] mu2ValuesCopy = new float[imageDimension];

            a = b = 0;

            for (pointer = 0 ; pointer < imageDimension ; pointer ++) {
                if (bppOne == 8) {
                    a = (0xff & imageProcessorOne.get(pointer));
                    b = (0xff & imageProcessorTwo.get(pointer));
                }
                if (bppOne == 16) {
                    a = (0xffff & imageProcessorOne.get(pointer));
                    b = (0xffff & imageProcessorOne.get(pointer));	
                }
                if (bppOne == 32) {
                    a = (imageProcessorOne.get(pointer));
                    b = (imageProcessorOne.get(pointer));
                }
                mu1Values [pointer] = mu1ValuesCopy [pointer] = a; // Float.intBitsToFloat(a);
			    mu2Values [pointer] = mu2ValuesCopy [pointer] = b; //Float.intBitsToFloat(b);
            } // for loop checking bits per pixel and shifting pointer accordingly
            mu1ImageProcessor.convolve (window_weights, filter_width, filter_width);
		    mu2ImageProcessor.convolve (window_weights, filter_width, filter_width);

            double[] mu1Square = new double [imageDimension];
		    double[] mu2Square = new double [imageDimension];
		    double[] mu1mu2 = new double [imageDimension];
            double [] sigma1 = new double [imageDimension];
            double [] sigma2 = new double [imageDimension];
            double [] sigma1Square = new double [imageDimension];
            double [] sigma2Square = new double [imageDimension];
            double [] sigma1sigma2 = new double [imageDimension];

            for (pointer = 0 ; pointer < imageDimension ; pointer ++) {
                mu1Square[pointer] = (double) (mu1Values[pointer] * mu1Values[pointer]);
                mu2Square[pointer] = (double) (mu2Values[pointer] * mu2Values[pointer]);
                mu1mu2[pointer] = (double) (mu1Values[pointer] * mu2Values[pointer]);

                sigma1Square[pointer] = (double) (mu1ValuesCopy[pointer] * mu1ValuesCopy[pointer]);
                sigma2Square[pointer] = (double) (mu2ValuesCopy[pointer] * mu2ValuesCopy[pointer]);
                sigma1sigma2[pointer] = (double) (mu1ValuesCopy[pointer] * mu2ValuesCopy[pointer]);
            }

            //	
            //THERE IS A METHOD IN IMAGEJ THAT CONVOLVES ANY ARRAY, BUT IT ONLY WORKS WITH IMAGE PROCESSORS. THIS IS THE REASON BECAUSE I CREATE THE FOLLOWING PROCESSORS
            //

            ImageProcessor imageProcesserSupport1 = new FloatProcessor (image_width, image_height);
            ImageProcessor imageProcesserSupport2 = new FloatProcessor (image_width, image_height);
            ImageProcessor imageProcesserSupport3 = new FloatProcessor (image_width, image_height);
            float [] supportArray1 =  (float []) imageProcesserSupport1.getPixels();
            float [] supportArray2 =  (float []) imageProcesserSupport2.getPixels();
            float [] supportArray3 =  (float []) imageProcesserSupport3.getPixels();

            for (pointer = 0; pointer < image_dimension ; pointer ++) {
                supportArray1[pointer] = (float) sigma1Square[pointer];
                supportArray2[pointer] = (float) sigma2Square[pointer];
                supportArray3[pointer] = (float) sigma1sigma2[pointer];
            }
            imageProcesserSupport1.convolve (window_weights, filter_width,  filter_width);
            imageProcesserSupport2.convolve (window_weights, filter_width,  filter_width); 
            imageProcesserSupport3.convolve (window_weights, filter_width,  filter_width);

            for (pointer =0; pointer<image_dimension; pointer++) {
                sigma1Square[pointer] =  supportArray1[pointer] - mu1Square[pointer];
                sigma2Square[pointer] =  supportArray2[pointer ]- mu2Square[pointer];
                sigma1sigma2[pointer] =  supportArray3[pointer] - mu1mu2[pointer];

                // THE FOLLOWING SENTENCES ARE VERY AD-HOC. SOMETIMES, FOR INTERNAL REASONS OF PRECISION OF CALCULATIONS AROUND THE BORDERS, SIGMA_SQ
                // CAN BE NEGATIVE. THE VALUE CAN BE AROUND 0.001 IN SOME POINTS (A FEW). THE PROBLEM IS THAT, FOR SIMPICITY I CALCULATE SIGMA1 
                // AS SQUARE ROOT OF sigma1Square OF COURSE, IF THE ALGORITHM FINDS NEGATIVE VALUES,
                // YOU GET THE MESSAGE  "IS NOT A NUMBER" IN RUN TIME.

                if (sigma1Square[pointer] < 0) {
                    sigma1Square[pointer] = 0;
                }
                if (sigma2Square[pointer] < 0) {
                    sigma2Square[pointer] = 0;
                }
                sigma1 [pointer] = Math.sqrt (sigma1Square[pointer]);
                sigma2 [pointer] = Math.sqrt (sigma2Square[pointer]);
            }

            // WE HAVE GOT ALL THE VALUES TO CALCULATE LUMINANCE, CONTRAST AND STRUCTURE
            double luminancePoint=1;
            double contrastPoint=0;
            double structurePoint = 0;
            luminance [level] = 0;
            contrast [level] = 0;
            structure [level] = 0;

            //Rouse/Hemami algorithm
            //https://www.semanticscholar.org/paper/Analyzing-the-role-of-visual-structure-in-the-of-Rouse-Hemami/d81d1d6be3da4ae539639f24665c3aaff4458cb0
            for (pointer = 0; pointer < image_dimension ; pointer ++) {

				if ( (mu1Square[pointer] + mu2Square[pointer]) == 0)
					luminancePoint = 1;
				else
					luminancePoint = (double) (( 2 * mu1mu2[pointer]) / (mu1Square[pointer] + mu2Square[pointer]));
				
				luminance[level] = luminance [level] + luminancePoint;

				if ( (sigma1Square[pointer] + sigma2Square[pointer]) == 0) 
					contrastPoint = 1;
				else
					contrastPoint = (double) ((2 * sigma1[pointer] * sigma2[pointer]) / (sigma1Square[pointer] + sigma2Square[pointer]));
				
				contrast [level] = contrast [level] + contrastPoint;

				if (((sigma1[pointer] == 0) | (sigma2[pointer] == 0)) & (sigma1[pointer] != sigma2[pointer]))
					structurePoint = 0;
				else
					if ((sigma1[pointer] == 0) & (sigma2[pointer] == 0))
						structurePoint = 1;
					else
						structurePoint = (double) ((sigma1sigma2[pointer]) / (sigma1[pointer] * sigma2[pointer]));

				structure[level] = structure[level] + structurePoint;
			}
            contrast[level] = (double) (contrast[level] / image_dimension);
		    structure[level] = (double) (structure[level] / image_dimension);

            if (level == 5)
                luminance[level] = (double) (luminance[level] / imageDimension);
            else
                luminance[level] = 1;
        } // end for loop for difference viewing levels

        for (level = 1 ; level <= 5 ; level ++) {
            if (structure[level] < 0)
                structure[level] *= -1.0;
            luminanceComparison = Math.pow ( luminance[level], luminanceExponent[level]) * luminanceComparison;
            contrastComparison = Math.pow (contrast[level], contrastExponent[level]) * contrastComparison;
            structureComparison = Math.pow (structure[level], structureExponent[level]) * structureComparison;
        }

        multiscaleSSIMIndex = luminanceComparison * contrastComparison * structureComparison;
        System.out.println(luminanceComparison + " " + contrastComparison + " " + structureComparison);

    } // end algorithm

    public double getMultiScaleSSIM() {
        return multiscaleSSIMIndex;
    }
}
