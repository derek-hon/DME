package ec.app.DMEtutorial.Fitness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.awt.image.BufferedImage;

import ec.EvolutionState;
import ec.distributedME.Elite;
import ec.util.Parameter;

public class Haar {
    double ZERO = 0.00001;
    int TOTAL_MAX_MAGNITUDE = 35;
    double[] a;
    double[] d;
    int[] truncatedColours;
    ImageQueryData HaarData;
    int width, height;
    double[][] coeffs = null;
    double[][] coefficients = null;
    int CYCLES = 1;
    public double[][] truncated = null;

    public Haar(EvolutionState state, BufferedImage img) {
        if (state.parameters.exists(new Parameter("coeff"), new Parameter("coeff")))
            TOTAL_MAX_MAGNITUDE = state.parameters.getInt(new Parameter("coeff"), new Parameter("coeff"));
        
        this.width = img.getWidth();
        this.height = img.getHeight();
        
        double[][] data = PSDExtensions.ImageToDouble2D(img, null);

        this.coefficients = std_decomp(state, data, width, height);
        // state.output.fatal(Arrays.deepToString(coefficients));

        // double[] samp = new double[width * height];
        // for (int y = 0 ; y < height ; y ++) {
        //     for (int x = 0 ; x < width ; x ++)
        //         samp[y * width + x] = data[x][y];
        // }
        // double[] coeff = decompose2D(samp);
        
        this.truncated = truncateCoefficients2D(state, coefficients);
        // state.output.fatal(Arrays.deepToString(truncated));
        this.HaarData = new ImageQueryData(coefficients[0][0]/255.0, truncated);
    }

    public Haar(EvolutionState state, int[][] pixels) {
        if (state.parameters.exists(new Parameter("coeff"), new Parameter("coeff")))
            TOTAL_MAX_MAGNITUDE = state.parameters.getInt(new Parameter("coeff"), new Parameter("coeff"));

        double[][] data = new double[pixels.length][pixels.length];
        this.width = pixels.length;
        this.height = pixels.length;


        for (int x = 0 ; x < width ; x ++ ) {
            for (int y = 0 ; y < height ; y ++)
                data[x][y] = (double)pixels[x][y];
        }
        this.coefficients = std_decomp(state, data, width, height);
        // state.output.fatal(Arrays.deepToString(coefficients));

        // double[] samp = new double[width * height];
        // for (int y = 0 ; y < height ; y ++) {
        //     for (int x = 0 ; x < width ; x ++)
        //         samp[y * width + x] = data[x][y];
        // }
        // double[] coeff = decompose2D(samp);
        
        this.truncated = truncateCoefficients2D(state, coefficients);
        // state.output.fatal(Arrays.deepToString(truncated));
        this.HaarData = new ImageQueryData(coefficients[0][0]/255.0, truncated);
    }

    public Haar(EvolutionState state, Elite e) {
        if (state.parameters.exists(new Parameter("coeff"), new Parameter("coeff")))
            TOTAL_MAX_MAGNITUDE = state.parameters.getInt(new Parameter("coeff"), new Parameter("coeff"));

        int[][] pixels = e.getPixels();
        double[][] data = new double[pixels.length][pixels.length];
        this.width = pixels.length;
        this.height = pixels.length;


        for (int x = 0 ; x < width ; x ++ ) {
            for (int y = 0 ; y < height ; y ++)
                data[x][y] = (double)pixels[x][y];
        }
        this.coefficients = std_decomp(state, data, width, height);
        // state.output.fatal(Arrays.deepToString(coefficients));

        // double[] samp = new double[width * height];
        // for (int y = 0 ; y < height ; y ++) {
        //     for (int x = 0 ; x < width ; x ++)
        //         samp[y * width + x] = data[x][y];
        // }
        // double[] coeff = decompose2D(samp);
        
        this.truncated = truncateCoefficients2D(state, coefficients);
        // state.output.fatal(Arrays.deepToString(truncated));
        this.HaarData = new ImageQueryData(coefficients[0][0]/255.0, truncated);
        // state.output.message("rank len: " + this.HaarData.getRankData().length);
    }

    public BufferedImage getCoefficientImage() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        for (int y = 0 ; y < 64 ; y ++) {
            for (int x = 0 ; x < 64 ; x ++) {
                img.setRGB(x, y, getIntFromColor(255, 255, 255));
            }
        }
        for (int i = 0 ; i < TOTAL_MAX_MAGNITUDE ; i ++) {
            img.setRGB((int)this.truncated[i][0], (int)this.truncated[i][1], getIntFromColor(0, 0, 0));
        }

        return img;
    }
    
    // https://people.sc.fsu.edu/~jburkardt/cpp_src/haar/haar.cpp
    public double[] decompose2D(double[] sample) {
        int length = width * height;
        int k = 1;
        double[] temp = new double[length];
        double s = Math.sqrt(2.0);

        for (int x = 0 ; x < width ; x ++) {
            for (int y = 0 ; y < height ; y ++)
                temp[x * height + y] = sample[x * height + y]/255.0;
        }

        //
        // Determine K, the largest power of 2 such that K <= M.
        //
        while (k * 2 <= height)
            k *= 2;

        //
        // Transform all columns.
        //
        while (1 < k) {
            k /= 2;

            for (int x = 0 ; x < width ; x ++) {
                for (int y = 0 ; y < k ; y ++) {
                    temp[x * height + y] = (sample[x * height + 2 * y] + sample[x * height + 2 * y + 1]) / s;
                    temp[x * height + y + k] = (sample[x * height +  2 * y] - sample[x * height + 2 * y + 1]) / s;
                }
            }

            for (int x = 0 ; x < width ; x ++) {
                for (int y = 0 ; y < 2 * k ; y ++)
                    sample[x * height + y] = temp[x * height + y];
            }
        }

        //
        // Determine K, the largest power of 2 such that K <= M.
        //
        while (k * 2 <= height)
            k *= 2;
        //
        // Transform all rows.
        //
        while (1 < k) {
            k /= 2;

            for (int x = 0; x < k; x++) {
                for (int y = 0; y < height; y++) {
                    temp[x * height + y] = (sample[2 * x * height + y] + sample[(2 * x + 1) * height + y]) / s;
                    temp[(x + k) * height + y] = (sample[2 * x * height + y] - sample[(2 * x + 1) * height + y]) / s;
                }
            }

            for (int x = 0; x < 2 * k; x++) {
                for (int y = 0; y < height; y++)
                    sample[x * height + y] = temp[x * height + y];
            }
        }
        
        return sample.clone();
    }

    /**
     * Andrea Weins' Gentropy
     * @param state
     * @param data
     * @param width
     * @param height
     * @return
     */
    public double[][] std_decomp(EvolutionState state, double[][] data, int width, int height) {
        int d = 0;
        double[] coefficients = new double[width * height];

        for (int y = 0 ; y < height ; y ++) {
            for (int x = 0 ; x < width ; x ++) {
                data[x][y] /= 255.0;
            }
        }

        for (int y = 0 ; y < height ; y ++) {
            d = width;
            for (int x = 0 ; x < width ; x ++) {
                data[x][y] /= Math.sqrt(d);
            }

            while (d > 1) {
                d /= 2;
                for (int i = 0 ; i < d ; i ++) {
                    coefficients[i] = (data[2 * i][y] + data[2 * i + 1][y]) / Math.sqrt(2.0);
                    coefficients[d + i] = (data[2 * i][y] - data[2 * i + 1][y]) / Math.sqrt(2.0);
                }
                for (int j = 0 ; j < width ; j ++) {
                    data[j][y] = coefficients[j];
                }
            }
        }

        for (int x = 0 ; x < width ; x ++) {
            d = height;
            for (int y = 0 ; y < height ; y ++) {
                data[x][y] /= Math.sqrt(d);
            }

            while (d > 1) {
                d /= 2;
                for (int i = 0 ; i < d ; i ++) {
                    coefficients[i] = (data[x][2 * i] + data[x][2 * i + 1]) / Math.sqrt(2.0);
                    coefficients[d + i] = (data[x][2 * i] - data[x][2 * i + 1]) / Math.sqrt(2.0);
                }
                for (int j = 0 ; j < height ; j ++) {
                    data[x][j] = coefficients[j];
                }
            }
        }

        return data.clone();
    }

    public double[][] truncateCoefficients2D(EvolutionState state, double[][] coefficients) {
        ArrayList<double[]> cList = new ArrayList<>();
        double max = -1.0;
        int maxX = 0, maxY = 0, index = 0;
        double[][] truncated = new double[TOTAL_MAX_MAGNITUDE][3];
        // for (int i = 0 ; i < truncated.length ; i ++ ) {
        //     for (int j = 0 ; j < truncated[i].length ; j ++)
        //         truncated[i][j] = 0;
        // }
        //Convert it to an arraylist because we can remove the largest coefficients
        for (int y= 0 ; y < height ; y ++ ) {
            for (int x = 0 ; x < width ; x ++) {
                cList.add(new double[] {x, y, coefficients[x][y]});
            }
        }
        // for (double value : coefficients)
        //     cList.add(value);
        //Remove first index because it's the average of the values
        cList.remove(0);
        // state.output.message("length: " + cList.size());
        //Obtain the largest coefficients and their indices
        for (int i = 0 ; i < TOTAL_MAX_MAGNITUDE ; i ++) {
            max = -1.0;
            index = 0;
            for (int c = 0 ; c < cList.size() ; c ++) {
                if (Math.abs(cList.get(c)[2]) > max) {
                    max = Math.abs(cList.get(c)[2]);
                    maxX = (int)cList.get(c)[0];
                    maxY = (int)cList.get(c)[1];
                    index = c;
                }
            }
            // for (int x = 0 ; x < width ; x ++) {
            //     for (int y = 0 ; y < height ; y ++) {
            //         if ((x * height + y) >= (cList.size()))
            //             break;
            //         // if (cList.get(x * height + y) > 0 || cList.get(x * height + y) < 0)
            //         //     counter ++;
            //         if (Math.abs(cList.get(x * height + y)) > max) {
            //             max = Math.abs(cList.get(x * height + y));
            //             maxX = x;
            //             maxY = y;
            //         }
            //     }
            // }

            // state.output.fatal("max counter; " + counter);
            // state.output.message("coords: ( " + maxX + ", " + maxY + " )");
            truncated[i][0] = maxX;
            truncated[i][1] = maxY;
            truncated[i][2] = i/TOTAL_MAX_MAGNITUDE;
            cList.remove(index);
        }

        // state.output.message(Arrays.deepToString(truncated));

        // state.output.fatal(Arrays.deepToString(truncated));
        
        return truncated.clone();
    }

    public ImageQueryData getHaarData() {
        return this.HaarData;
    }

    public class ImageQueryData {
        double average;
        int[][] coordinates = null;
        double[] rank = null;
        double[] values = null;
        ArrayList<double[]> coefficientData = new ArrayList<>();
        double[][] imageData = null;

        public String printData() {
            return "Average: " + this.average + " rank data: " + Arrays.toString(this.rank) + " Coordinate data: \n" + Arrays.deepToString(this.coordinates);
        }

        ImageQueryData(EvolutionState state, double[] coefficients) {
            this.values = coefficients.clone();
        }

        public ImageQueryData(double avg, double[][] data) {
            this.average = avg;
            this.coordinates = new int[TOTAL_MAX_MAGNITUDE][2];
            this.rank = new double[TOTAL_MAX_MAGNITUDE];
            for (int i = 0 ; i < TOTAL_MAX_MAGNITUDE ; i ++) {
                // this.coefficientData.add(data[i]);
                this.coordinates[i][0] = (int)data[i][0];
                this.coordinates[i][1] = (int)data[i][1];
                this.rank[i] = data[i][2];
            }
        }

        ImageQueryData(EvolutionState state, double avg, double[][] data) {
            this.average = avg;
            this.coordinates = new int[TOTAL_MAX_MAGNITUDE][2];
            this.values = new double[TOTAL_MAX_MAGNITUDE];
            for (int i = 0 ; i < TOTAL_MAX_MAGNITUDE ; i ++) {
                this.coordinates[i][0] = (int)data[i][0];
                this.coordinates[i][1] = (int)data[i][1];
                this.values[i] = data[i][2];
            }
            // state.output.fatal(Arrays.deepToString(this.coordinates));
        }

        ImageQueryData(EvolutionState state, double[][] data) {
            this.average = data[0][0];
            this.imageData = new double[data.length][data[0].length];
            for (int x = 0 ; x < data.length ; x ++) {
                for (int y = 0 ; y < data[x].length ; y ++) {
                    imageData[x][y] = data[x][y];
                }
            }
        }

        double[] getRankData() {
            return this.rank;
        }

        double[] getData(int index) {
            return this.coefficientData.get(index);
        }

        int getIndex(int index) {
            return (int)this.coefficientData.get(index)[0];
        }
        
        double getValue(int index) {
            return this.coefficientData.get(index)[1];
        }

        int indexExists(int value) {
            for (int i = 0 ; i < this.coefficientData.size() ; i ++) {
                if (value == this.getIndex(i))
                    return i;
            }
            return -1;
        }

        int checkIndex(int[] index) {
            for (int i = 0 ; i < TOTAL_MAX_MAGNITUDE ; i ++) {
                if (this.coordinates[i][0] == index[0] && this.coordinates[i][1] == index[1]) {
                    return i;
                }
            }
            return -1;
        }

        double getCoefficient(int index) {
            return this.values[index];
        }

        double getRank(int index) {
            return this.rank[index];
        }

        public BufferedImage coefficientImage (EvolutionState state) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            int colour = 0;
            // state.output.fatal(Arrays.deepToString(this.coordinates));
            // System.out.println(Arrays.deepToString(this.coordinates));
            for (int y = 0 ; y < height ; y ++) {
                for (int x = 0 ; x < width ; x ++) {
                    int index = checkIndex(new int[]{x, y});
                    if (index > -1) {
                        image.setRGB(x, y, getIntFromColor(255, 255, 255));
                    }
                    else {
                        image.setRGB(x, y, getIntFromColor(0, 0, 0));
                    }
                }
            }
    
            return image;
        }

        public double compareTo(ImageQueryData comparisonHaar) {
            double comparison = 0.0;
            int index = 0;
            // Summation of equal index value pairs of coefficients
            // for (int i = 0 ; i < TOTAL_MAX_MAGNITUDE ; i ++) {
            //     index = indexExists(comparisonHaar.getIndex(i));
            //     if (index != -1) {
            //         comparison += Math.abs(this.getValue(index) - comparisonHaar.getValue(i));
            //     }
            //     else {
            //         comparison += 1000;
            //     }
            // }
            for (int i = 0 ; i < TOTAL_MAX_MAGNITUDE ; i ++) {
                index = checkIndex(comparisonHaar.coordinates[i]);
                if (index > -1) {
                    comparison += Math.abs(this.getRank(index) - comparisonHaar.getRank(index));
                }
                else {
                    comparison += 2;
                }
            }

            // for (int i = 0 ; i < TOTAL_MAX_MAGNITUDE ; i ++) {
            //     index = checkIndex(comparisonHaar.coordinates[i]);
            //     if (index > -1) {
            //         comparison += Math.abs(this.getCoefficient(index) - comparisonHaar.getCoefficient(index));
            //     }
            //     else {
            //         comparison += 1000;
            //     }
            // }

            // for (int x = 0 ; x < this.imageData.length ; x ++) {
            //     for (int y = 0 ; y < this.imageData[x].length ; y ++) {
            //         if (x == 0 && y == 0 || (this.imageData[x][y] != 0 && comparisonHaar.imageData[x][y] != 0)) {
            //             comparison += (this.imageData[x][y] - comparisonHaar.imageData[x][y]);
            //         }
            //         else {
            //             comparison += 1000;
            //         }
            //     }
            // }

            // for (int i = 0 ; i < TOTAL_MAX_MAGNITUDE ; i ++) {
            //     comparison += Math.abs(this.values[i] - comparisonHaar.values[i]);
            // }

            // Fast Multiresolution Image Querying Equation 4
            comparison = Math.abs(this.average - comparisonHaar.average) + comparison;

            return comparison;
        }
    }

    /**
     * https://stackoverflow.com/a/18037185
     * 
     * @param Red
     * @param Green
     * @param Blue
     * @return
     */
    public int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; // Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; // Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; // Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; // 0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}
