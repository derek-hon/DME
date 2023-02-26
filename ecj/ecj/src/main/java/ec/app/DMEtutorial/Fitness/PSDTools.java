package ec.app.DMEtutorial.Fitness;

public class PSDTools 
{	
	
	static 
	{ 
		System.loadLibrary("PSDTools_JNI");
		initialize();
	}
	
	public static final int SideLength = 64;
	
	
	// DON'T FORGET THE PSDTools_initialize() !
	// We might not have huge mounds of static data to initialize, but it sets
	// up the PSDToolsTLS struct with stack information. 
	public static native void initialize();
	
	
	// Dummy methods for timing tests
	public static native double f_PSD_Dummy0();
	public static native double f_PSD_Dummy1(
				double[][] img
			);
	public static native double f_PSD_Dummy2(
				double[][] img
			);
	public static native double f_PSD_Dummy3(
				double[][][] img_3channel
			);
	
	
	// Regressions
	public static native double f_PSD_Slope(
				double[][] img
			);
	public static native double[] f_PSD_LinearRegression(
				double[][] img
			);
	
	// Namesake Methods
	public static native void f_ImageFFT(
				double[][] img,
				double[][] amplitude,
				double[][] offset
			);
	public static native void f_ImageFFTPowerPhase(
				double[][] img,
				double[][] power,
				double[][] phase
			);
	public static native double[][] f_ImageFFTPower(
				double[][] img
			);
	public static native double[] f_RadialAverageSpectra( 
				double[][] imgFT 
			);
	
	// General Processing and Charting
	public static native double f_PSD_Processing(
				double[][] img,
				double[]   imgPS,
				double[][] imgFT,
				double[][] imgWD
			);
	public static native void f_PSD_Processing_FTOnly(
				double[][] img,
				double[][] imgFT
			);
	public static native void f_PSD_Processing_PSOnly(
				double[][] img,
				double[]   imgPS
			);
	public static native int f_PlotSpectra_GetChartInfo(
				double[]   imgPS,
				double[]   plotRange,
				double[]   plotScale,
				double[]   regression, 
				double[][] scaledPoints
			);
	
}
