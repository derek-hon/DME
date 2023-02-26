package ec.app.DMEtutorial.BehaviourEvaluations;

public class ColourEvaluationFunctions {
    public static double colourEntropy(int[][] pixels) {
        int frequency[] = new int[256];
        int length = pixels.length;
        // int size = img.getWidth() * img.getHeight();


        for (int x = 0 ; x < length ; x ++) {
            for (int y = 0 ; y < length ; y ++) {
                int rgb = pixels[x][y];
                int index = (((rgb & 0xFF0000) >> 16) + ((rgb & 0x00FF00) >> 8) + (rgb & 0x0000FF))/3;
                frequency[index] ++;
            }
        }

        double entropyValue = 0,temp=0;
        double totalSize = length * length; //total size of all symbols in an image
        
        for(int i=0 ; i < 256 ; i++){ //the number of times a sybmol has occured
            if(frequency[i] > 0) { //log of zero goes to infinity
                temp = ( frequency[i] / totalSize ) * ( Math.log(frequency[i] / totalSize ));
                entropyValue += temp;
            }
        }
        return entropyValue * (-1);
    }

    public static double averageRed(int[][] pixels) {
        double red = 0.0;

        for (int x = 0 ; x < pixels.length ; x ++) {
            for (int y = 0 ; y < pixels.length ; y ++) {
                int rgb = pixels[x][y];
                red += ((rgb & 0xFF0000) >> 16);
            }
        }

        return red /= (pixels.length * pixels.length);
    }

    public static double averageGreen(int[][] pixels) {
        double green = 0.0;

        for (int x = 0 ; x < pixels.length ; x ++) {
            for (int y = 0 ; y < pixels.length ; y ++) {
                int rgb = pixels[x][y];
                green += ((rgb & 0x00FF00) >> 8);
            }
        }

        return green /= (pixels.length * pixels.length);
    }

    public static double averageBlue(int[][] pixels) {
        double blue = 0.0;

        for (int x = 0 ; x < pixels.length ; x ++) {
            for (int y = 0 ; y < pixels.length ; y ++) {
                int rgb = pixels[x][y];
                blue += (rgb & 0x0000FF);
            }
        }

        return blue /= (pixels.length  * pixels.length );
    }

    public static double averageIntensity(int[][] pixels) {
        double intensity = 0.0;

        for (int x = 0 ; x < pixels.length ; x ++) {
            for (int y = 0 ; y < pixels.length ; y ++) {
                int rgb = pixels[x][y];
                intensity += (((rgb & 0xFF0000) >> 16) + ((rgb & 0x00FF00) >> 8) + (rgb & 0x0000FF)) / 3.0;
            }
        }

        return intensity /= (pixels.length * pixels.length);
    }

    public static double averageHue(int[][] pixels) {
        double hue = 0.0;
        
        for (int x = 0 ; x < pixels.length ; x ++) {
            for (int y = 0 ; y < pixels.length ; y ++) {
                int rgb = pixels[x][y];
                hue += hue(((rgb & 0xFF0000) >> 16), ((rgb & 0x00FF00) >> 8), (rgb & 0x0000FF));
            }
        }

        return hue /= (pixels.length * pixels.length);
    }

    public static double averageSaturation(int[][] pixels) {
        double saturation = 0.0;
        
        for (int x = 0 ; x < pixels.length ; x ++) {
            for (int y = 0 ; y < pixels.length ; y ++) {
                int rgb = pixels[x][y];
                saturation += saturation(((rgb & 0xFF0000) >> 16), ((rgb & 0x00FF00) >> 8), (rgb & 0x0000FF));
            }
        }

        return saturation /= (pixels.length * pixels.length);
    }

    public static double intensitySkewness(int[][] pixels) {
        double skewness = 0.0;
        double mean = averageIntensity(pixels);
        double standardDeviation = 0.0;

        for (int x = 0 ; x < pixels.length ; x ++) {
            for (int y = 0 ; y < pixels.length ; y ++) {
                int rgb = pixels[x][y];
                double val = (((rgb & 0xFF0000) >> 16) + ((rgb & 0x00FF00) >> 8) + (rgb & 0x0000FF))/3.0;
                skewness += Math.pow((val - mean), 3);
                standardDeviation += Math.pow((val - mean), 2);
            }
        }
        standardDeviation /= (pixels.length * pixels.length);
        standardDeviation = Math.sqrt(standardDeviation);

        return skewness / (((pixels.length * pixels.length) - 1) * Math.pow(standardDeviation, 3));
    }

    public static double intensityKurtosis(int[][] pixels) {
        double kurtosis = 0.0;
        double mean = averageIntensity(pixels);
        double standardDeviation = 0.0;

        for (int x = 0 ; x < pixels.length ; x ++) {
            for (int y = 0 ; y < pixels.length ; y ++) {
                int rgb = pixels[x][y];
                double val = (((rgb & 0xFF0000) >> 16) + ((rgb & 0x00FF00) >> 8) + (rgb & 0x0000FF))/3.0;
                kurtosis += Math.pow((val - mean), 4);
                standardDeviation += Math.pow((val - mean), 2);
            }
        }
        standardDeviation /= (pixels.length * pixels.length);
        standardDeviation = Math.sqrt(standardDeviation);

        kurtosis = kurtosis / (Math.pow(standardDeviation, 4) * ((pixels.length * pixels.length)));
        return kurtosis;
    }

    private static int saturation(int red, int green, int blue) {
        int saturation = 0;

        double redPrime = red/255;
        double greenPrime = green/255;
        double bluePrime = blue/255;

        double max = Math.max(redPrime, Math.max(greenPrime, bluePrime));
        double min = Math.min(redPrime, Math.min(greenPrime, bluePrime));
        double delta = max - min;

        if (max == 0)
            saturation = 0;
        else {
            saturation = (int) (delta / max);
        }

        return saturation;
    }

    /**
     * https://stackoverflow.com/a/26233318
     * @param red
     * @param green
     * @param blue
     * @return
     */
    private static int hue(int red, int green, int blue) {
        float min = Math.min(Math.min(red, green), blue);
        float max = Math.max(Math.max(red, green), blue);

        if (min == max) {
            return 0;
        }

        float hue = 0f;
        if (max == red) {
            hue = (green - blue) / (max - min);

        } else if (max == green) {
            hue = 2f + (blue - red) / (max - min);

        } else {
            hue = 4f + (red - green) / (max - min);
        }

        hue = hue * 60;
        if (hue < 0) hue = hue + 360;

        return Math.round(hue);
    }
}
