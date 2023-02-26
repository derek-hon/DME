package ec.app.DMEtutorial.Fitness;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class PSDExtensions 
{

	public static ImageToArrayStrategy ImageToArrayStrategyDefault = ImageToArrayStrategy.Mean;
	public enum ImageToArrayStrategy
	{	
		Mean,
		Y601,	// NTSC
		Y709,
		Channel_R,
		Channel_G,
		Channel_B
	}
	
	public static double[][] ImageToDouble2D(BufferedImage img, ImageToArrayStrategy strat)
	{
		final int iSide = PSDTools.SideLength;
		double[][] arr = new double[iSide][iSide];
		
		// Convert form to double array
		for(int y = 0; y < iSide; y++)
		for(int x = 0; x < iSide; x++)
		{
			int px = img.getRGB(x,y);
			int px_r = (px & 0xFF0000) >> 16;
			int px_g = (px & 0x00FF00) >> 8;
			int px_b = (px & 0x0000FF);
			double px_y = 0.0;
			
			if(strat==null) strat = ImageToArrayStrategyDefault;
			switch(strat)
			{	
				case Mean:			px_y = ( px_r + px_g + px_b ) / 3.0; break;
				case Y601:			px_y = (0.299*px_r) + (0.587*px_g) + (0.114*px_b); break;
				case Y709:          px_y = (0.213*px_r) + (0.715*px_g) + (0.072*px_b); break;
				case Channel_R:		px_y = px_r; break;
				case Channel_G:		px_y = px_g; break;
				case Channel_B:		px_y = px_b; break;
			}
			
			arr[x][y] = px_y;
		}
		
		return arr;
	}

	public static int[][] ImageToInt2D(BufferedImage img, ImageToArrayStrategy strat) {
		final int iSide = PSDTools.SideLength;
		int[][] arr = new int[iSide][iSide];
		
		// Convert form to double array
		for(int y = 0; y < iSide; y++)
		for(int x = 0; x < iSide; x++)
		{
			int px = img.getRGB(x,y);
			int px_r = (px & 0xFF0000) >> 16;
			int px_g = (px & 0x00FF00) >> 8;
			int px_b = (px & 0x0000FF);
			int px_y = 0;
			
			if(strat==null) strat = ImageToArrayStrategyDefault;
			switch(strat)
			{	
				case Mean:			px_y = ( px_r + px_g + px_b ) / 3; break;
				case Y601:			px_y = (int)((0.299*px_r) + (0.587*px_g) + (0.114*px_b)); break;
				case Y709:          px_y = (int)((0.213*px_r) + (0.715*px_g) + (0.072*px_b)); break;
				case Channel_R:		px_y = px_r; break;
				case Channel_G:		px_y = px_g; break;
				case Channel_B:		px_y = px_b; break;
			}
			
			arr[x][y] = px_y;
		}
		
		return arr;
	}

	public static int[] ImageToInt(BufferedImage img, ImageToArrayStrategy strat) {
		final int iSide = PSDTools.SideLength;
		int[] arr = new int[iSide * iSide];
		
		// Convert form to double array
		for(int y = 0; y < iSide; y++)
		for(int x = 0; x < iSide; x++)
		{
			int px = img.getRGB(x,y);
			int px_r = (px & 0xFF0000) >> 16;
			int px_g = (px & 0x00FF00) >> 8;
			int px_b = (px & 0x0000FF);
			double px_y = 0.0;
			
			if(strat==null) strat = ImageToArrayStrategyDefault;
			switch(strat)
			{	
				case Mean:			px_y = ( px_r + px_g + px_b ) / 3.0; break;
				case Y601:			px_y = (0.299*px_r) + (0.587*px_g) + (0.114*px_b); break;
				case Y709:          px_y = (0.213*px_r) + (0.715*px_g) + (0.072*px_b); break;
				case Channel_R:		px_y = px_r; break;
				case Channel_G:		px_y = px_g; break;
				case Channel_B:		px_y = px_b; break;
			}
			
			arr[y * iSide + x] = (int)px_y;
		}
		
		return arr;
	}

	public static BufferedImage IntToImage(int[] rgb) {
		BufferedImage img = new BufferedImage(PSDTools.SideLength, PSDTools.SideLength, BufferedImage.TYPE_INT_RGB);
		
		for (int x = 0 ; x < PSDTools.SideLength ; x ++ ) {
			for (int y = 0 ; y < PSDTools.SideLength ; y ++) {
				int value = rgb[x * PSDTools.SideLength + y];
				img.setRGB(x, y, value);
			}
		}
		return img;
	}

	public static BufferedImage Int2DToImage(int[][] pixels) {
		BufferedImage image = new BufferedImage(PSDTools.SideLength, PSDTools.SideLength, BufferedImage.TYPE_INT_RGB);

		for (int y = 0 ; y < pixels.length ; y ++) {
			for (int x = 0 ; x < pixels[y].length ; x ++) {
				image.setRGB( x, y, pixels[x][y] );
			}
		}

		return image;
	}
	
	public static BufferedImage Double2DToImage(double[][] arr)
	{
		BufferedImage img = new BufferedImage(PSDTools.SideLength, PSDTools.SideLength, BufferedImage.TYPE_INT_RGB);
		for(int y = 0; y < arr.length;    y++)
		for(int x = 0; x < arr[y].length; x++)
		{	
			int pxy = (int)( Math.max(Math.min(arr[x][y],255),0) );
			img.setRGB( x, y, (pxy+(pxy<<8)+(pxy<<16)) );
		}
		return img;
	}
	
	public static BufferedImage Double2DToImage_ForFFT(double[][] arr)
	{
		for(int y = 0; y < PSDTools.SideLength; y++)
		for(int x = 0; x < PSDTools.SideLength; x++)
			arr[x][y] = arr[x][y] * 256;
		return Double2DToImage(arr);
	}
	
	public static BufferedImage ChartSpectra(double[] imgPS)
	{
		// -- Get charting data;
		final double Scale = 2.0;
		final int    Dimen = (int)(PSDTools.SideLength*Scale);		
		double[] plotRange  = new double[4];
		double[] plotScale  = new double[2];
		double[] regression = new double[2];
		double[][] scaledPoints = new double[2][(PSDTools.SideLength >> 1) + 1];
		int validPoints = PSDTools.f_PlotSpectra_GetChartInfo(imgPS, plotRange, plotScale, regression, scaledPoints);
		
		
		BufferedImage img = new BufferedImage(Dimen, Dimen, BufferedImage.TYPE_INT_RGB);
		Graphics graph = img.getGraphics();
		
		// Background
		graph.setColor(Color.LIGHT_GRAY);
		graph.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		// X Axis
		graph.setColor(Color.GRAY);
		graph.drawLine(0, img.getHeight()/2, img.getWidth()-1, img.getHeight()/2);
		
		// Best Fit
		graph.setColor(Color.BLUE);
		for(int l = 0; l < 1; l++)
		{
			double x0 = 0;
			double y0 = ((x0/plotScale[0]) * regression[0]) + regression[1];
			       y0 = y0 * plotScale[1];
				   y0 = (Dimen-y0) - (0.5*Dimen);
			double x1 = Dimen-1;
			double y1 = ((x1/plotScale[0]) * regression[0]) + regression[1];
			       y1 = y1 * plotScale[1];
				   y1 = (Dimen-y1) - (0.5*Dimen);
			graph.drawLine((int)Math.round(x0), (int)Math.round(y0), (int)Math.round(x1), (int)Math.round(y1));
		}
		
		// Lines
		graph.setColor(Color.RED);
		for(int i = 0; i < validPoints-1; i++)
		{
			int x0 = (int)Math.round(scaledPoints[0][i+0]);
			int y0 = (int)Math.round(scaledPoints[1][i+0]);
			int x1 = (int)Math.round(scaledPoints[0][i+1]);
			int y1 = (int)Math.round(scaledPoints[1][i+1]);
			graph.drawLine(x0, y0, x1, y1);
		}
		
		// Points
		graph.setColor(Color.GREEN);
		for(int i = 0; i < validPoints; i++)
		{
			int x = (int)Math.round(scaledPoints[0][i]);
			int y = (int)Math.round(scaledPoints[1][i]);
			graph.fillRect(x-1, y-1, 3, 3);
		}
		
		return img;
	}
	
}
