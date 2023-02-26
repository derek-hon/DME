package ec.app.DMEtutorial.Fitness;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Random;
import javax.imageio.ImageIO;

import ec.app.DMEtutorial.Fitness.PSDExtensions.ImageToArrayStrategy;

// import ec.proctexture.PSDExtensions.ImageToArrayStrategy;;

// import PSDTools.PSDExtensions.ImageToArrayStrategy;

public class PSDTools_Tests 
{	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static final int iSide    = PSDTools.SideLength;
	static double[][] img     = new double[iSide][iSide];
	static double[]   imgPS   = new double[iSide+1];
	static double[][] imgFT   = new double[iSide][iSide];
	static double[][] imgFTa  = new double[iSide][iSide];
	static double[][] imgFTo  = new double[iSide][iSide];
	static double[][] imgWD   = new double[iSide][iSide];
	static BufferedImage bImg = new BufferedImage(iSide, iSide, BufferedImage.TYPE_INT_RGB);
	static long perfIters     = 5000;//0xFFFF;
	
	
	
	public static boolean LoadImage(File filename) throws IOException, Exception
	{
		BufferedImage bufImg = ImageIO.read(filename);
		Raster rasImg = bufImg.getRaster();
		
		if(bufImg.getHeight()!=iSide || bufImg.getWidth()!=iSide)
			throw new Exception("Image must have dimensions "+String.valueOf(iSide)+"x"+String.valueOf(iSide));
		
		for(int y = 0; y < iSide; y++)
		for(int x = 0; x < iSide; x++)
		{
			if(rasImg.getNumDataElements() > 1)
			{
				// Colour; Convert to rec601 Grayscale
				int px = bufImg.getRGB(x,y);
				int px_r = (px&0xFF0000) >> 16;
				int px_g = (px&0x00FF00) >> 8;
				int px_b = (px&0x0000FF);
				double px_y601 = (0.299*px_r) + (0.587*px_g) + (0.114*px_b);
				img[x][y] = px_y601 / 255.0;
			}
			else
			{
				// Grayscale
				double[] dat = new double[1];
				rasImg.getPixel(x, y, dat);
				img[x][y] = dat[0] / 255.0;
			}
		}
		
		return true;
	}
	
	public static void DataTest()
	{
		System.out.print("PSD Slope: ");
		double pulledNumber = Double.NaN;
		pulledNumber = PSDTools.f_PSD_Slope(img);
		System.out.println(String.valueOf(pulledNumber));
		
		System.out.print("PSD Regression(2): ");
		double[] regression = new double[]{ Double.NaN, Double.NaN };
		regression = PSDTools.f_PSD_LinearRegression(img);
		for(int i = 0; i < regression.length; i++)
			System.out.print( String.valueOf(regression[i]) + " " );
		System.out.println();
	}
	
	public static void PerfTest()
	{
		Runtime env = Runtime.getRuntime();
		long memStart;
		long memEnd;
		long memChange;
		Date dateStart;
		Date dateEnd;		
		Duration duration;
		
		System.out.println("-- Test: ImageToDouble2D - FirstChannel");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDExtensions.ImageToDouble2D(bImg,ImageToArrayStrategy.Channel_R);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: ImageToDouble2D - Y709");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDExtensions.ImageToDouble2D(bImg,ImageToArrayStrategy.Y709);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: Dummy 0");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDTools.f_PSD_Dummy0();
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: Dummy 1");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDTools.f_PSD_Dummy1(img);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: Dummy 2");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDTools.f_PSD_Dummy2(img);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: f_imageFFT");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDTools.f_ImageFFT(img, imgFTa, imgFTo);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: f_ImageFFTPower");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			imgFT = PSDTools.f_ImageFFTPower(img);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: f_RadialAverageSpectra");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDTools.f_RadialAverageSpectra(imgFT);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: f_PSD_Slope");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDTools.f_PSD_Slope(img);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: f_PSD_LinearRegression");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDTools.f_PSD_LinearRegression(img);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: f_PSD_Processing");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDTools.f_PSD_Processing(img,imgPS,imgFT,imgWD);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
		System.out.println("-- Test: ChartSpectra");
		memStart  = env.totalMemory() - env.freeMemory();
		dateStart = new Date();
		for(int i = 0; i < perfIters; i++)
			PSDExtensions.ChartSpectra(imgPS);
		dateEnd = new Date();
		memEnd  = env.totalMemory() - env.freeMemory();
		duration = Duration.between(dateStart.toInstant(), dateEnd.toInstant());
		memChange = memEnd - memStart;
		System.out.println( "Time Start   : " + dateFormat.format(dateStart) );
		System.out.println( "Time End     : " + dateFormat.format(dateEnd) );
		System.out.println( "Elapsed time : " + duration.toString() );
		System.out.println( "Mem Start    : " + memStart );
		System.out.println( "Mem End      : " + memEnd );
		System.out.println( "Mem Leak/Use : " + memChange );
		
	}
	
	public static class ImageData 
	{ 
		double[][] imgData   = new double[iSide][iSide]; 
		double[][] imgFTData = new double[iSide][iSide];
	}
	public static class IntegrityTestThread extends Thread 
	{
		int threadnum = 0;
		ImageData[] images = null;
		Random r = new Random();
		public IntegrityTestThread(int tnum, ImageData[] imgs)
		{
			threadnum = tnum;
			images = imgs;
		}
		@Override 
		public void run()
		{
			DoCheck();
		}
		public void DoCheck()
		{
			for(int i = 0; i < 500; i++)
			{	
				ImageData image = images[r.nextInt(images.length)];
				double[][] imageFT = new double[iSide][iSide];
				
				PSDTools.f_PSD_Processing_FTOnly(image.imgData, imageFT);
				
				boolean failed = false;
				for(int y = 0; y < iSide; y++)
				{
					for(int x = 0; x < iSide; x++)
					{
						if(image.imgFTData[x][y] != imageFT[x][y])
							failed = true;
						if(failed) break;
					}
					if(failed) break;
				}
				if(failed) System.out.println("Thread "+threadnum+" Iteration "+i+" failed.");
			}
		}
	}
	public static void IntegrityTest()
	{
		Random r = new Random();
		ImageData[] images = new ImageData[8];
		for(int i = 0; i < images.length; i++)
		{
			images[i] = new ImageData();
			for(int y = 0; y < iSide; y++)
			for(int x = 0; x < iSide; x++)
				images[i].imgData[x][y] = r.nextDouble();
			PSDTools.f_PSD_Processing_FTOnly(images[i].imgData, images[i].imgFTData);
		}
		
		IntegrityTestThread[] t = new IntegrityTestThread[4];
		for(int i = 0; i < t.length; i++)
			t[i] = new IntegrityTestThread(i,images);
		
		System.out.println("Single threaded attempt:");
		t[0].DoCheck();
		
		System.out.println("multi threaded attempt:");
		for(int i = 0; i < t.length; i++)
			t[i].start();
		for(int i = 0; i < t.length; i++)
		{
			try 
			{
				t[i].join();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) 
	{	
		
		if(args.length > 0)
		{
			String pathname = args[0];
			try{
				LoadImage(new File(pathname));
				System.out.println("Loaded Image ["+pathname+"]");
			}catch(Exception ex){
				ex.printStackTrace();
				return;
			}
		}
		else
		{
			System.out.println("Randomizing Image Data.");
			Random r = new Random();
			for(int y = 0; y < iSide; y++)
			for(int x = 0; x < iSide; x++)
				img[x][y] = r.nextDouble();
		}
		
		//DataTest();
		PerfTest();
		//IntegrityTest();
	}

}
