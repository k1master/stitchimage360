package com.stitch360.imagestitching;
import georegression.struct.homo.Homography2D_F64;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;
import georegression.transform.homo.HomographyPointOps_F64;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.ddogleg.fitting.modelset.ModelMatcher;
import org.ddogleg.fitting.modelset.ransac.Ransac;

import com.stitch360.stream.StitchThread;

import boofcv.abst.feature.associate.AssociateDescription;
import boofcv.abst.feature.associate.ScoreAssociation;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.alg.distort.ImageDistort;
import boofcv.alg.distort.PixelTransformHomography_F32;
import boofcv.alg.distort.impl.DistortSupport;
import boofcv.alg.feature.UtilFeature;
import boofcv.alg.interpolate.impl.ImplBilinearPixel_F32;
import boofcv.alg.sfm.robust.DistanceHomographySq;
import boofcv.alg.sfm.robust.GenerateHomographyLinear;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.associate.FactoryAssociation;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.gui.image.HomographyStitchPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.FastQueue;
import boofcv.struct.feature.AssociatedIndex;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.feature.TupleDesc;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.MultiSpectral;

/**
 * <p> Exampling showing how to combines two images together by finding the best fit image transform with point
 * features.</p>
 * <p>
 * Algorithm Steps:<br>
 * <ol>
 * <li>Detect feature locations</li>
 * <li>Compute feature descriptors</li>
 * <li>Associate features together</li>
 * <li>Use robust fitting to find transform</li>
 * <li>Render combined image</li>
 * </ol>
 * </p>
 *
 * @author Peter Abeles
 */
public class ImageStitching {

	/**
	 * Using abstracted code, find a transform which minimizes the difference between corresponding features
	 * in both images.  This code is completely model independent and is the core algorithms.
	 */
	static int counter = 0;
	static int widthcounter = 0;
	static int resizedWidth = 0;
	static String userHomeDir, picDir, srcDir;
	static Homography2D_F64 H = null;
	//static HomographyStitchPanel panel = null;
	
	public static<T extends ImageSingleBand, FD extends TupleDesc> Homography2D_F64
	computeTransform( T imageA , T imageB ,
					  DetectDescribePoint<T,FD> detDesc ,
					  AssociateDescription<FD> associate ,
					  ModelMatcher<Homography2D_F64,AssociatedPair> modelMatcher )
	{
		// get the length of the description
		List<Point2D_F64> pointsA = new ArrayList<Point2D_F64>();
		FastQueue<FD> descA = UtilFeature.createQueue(detDesc,100);
		List<Point2D_F64> pointsB = new ArrayList<Point2D_F64>();
		FastQueue<FD> descB = UtilFeature.createQueue(detDesc,100);

		// extract feature locations and descriptions from each image
		describeImage(imageA, detDesc, pointsA, descA);
		describeImage(imageB, detDesc, pointsB, descB);

		// Associate features between the two images
		associate.setSource(descA);
		associate.setDestination(descB);
		associate.associate();

		// create a list of AssociatedPairs that tell the model matcher how a feature moved
		FastQueue<AssociatedIndex> matches = associate.getMatches();
		List<AssociatedPair> pairs = new ArrayList<AssociatedPair>();

		for( int i = 0; i < matches.size(); i++ ) {
			AssociatedIndex match = matches.get(i);

			Point2D_F64 a = pointsA.get(match.src);
			Point2D_F64 b = pointsB.get(match.dst);

			pairs.add( new AssociatedPair(a,b,false));
		}

		// find the best fit model to describe the change between these images
		if( !modelMatcher.process(pairs) )
			throw new RuntimeException("Model Matcher failed!");

		// return the found image transform
		return modelMatcher.getModel().copy();
	}

	/**
	 * Detects features inside the two images and computes descriptions at those points.
	 */
	private static <T extends ImageSingleBand, FD extends TupleDesc>
	void describeImage(T image,
					   DetectDescribePoint<T,FD> detDesc,
					   List<Point2D_F64> points,
					   FastQueue<FD> listDescs) {
		detDesc.detect(image);

		listDescs.reset();
		for( int i = 0; i < detDesc.getNumberOfFeatures(); i++ ) {
			points.add( detDesc.getLocation(i).copy() );
			listDescs.grow().setTo(detDesc.getDescription(i));
		}
	}

	/**
	 * Given two input images create and display an image where the two have been overlayed on top of each other.
	 */
	public static <T extends ImageSingleBand>
	BufferedImage stitch( BufferedImage imageA , BufferedImage imageB , Class<T> imageType)
	{
		StitchThread t = (StitchThread) Thread.currentThread();
		T inputA = ConvertBufferedImage.convertFromSingle(imageA, null, imageType);
		T inputB = ConvertBufferedImage.convertFromSingle(imageB, null, imageType);

		// Detect using the standard SURF feature descriptor and describer
		// Original max feature per scale is 200, increased the max amount of feature taken in
		// Original threshold is 1, increased it to increase range of describe points
		DetectDescribePoint detDesc = FactoryDetectDescribe.surfStable(
				new ConfigFastHessian(4, 3, 1450, 1, 9, 4, 4), null,null, imageType);
		ScoreAssociation<SurfFeature> scorer = FactoryAssociation.scoreEuclidean(SurfFeature.class,true);
		AssociateDescription<SurfFeature> associate = FactoryAssociation.greedy(scorer,2,true);

		// fit the images using a homography.  This works well for rotations and distant objects.
		GenerateHomographyLinear modelFitter = new GenerateHomographyLinear(true);
		DistanceHomographySq distance = new DistanceHomographySq();

		// Original values = (123,modelFitter,distance,60,9)
		ModelMatcher<Homography2D_F64,AssociatedPair> modelMatcher =
				new Ransac<Homography2D_F64,AssociatedPair>(123,modelFitter,distance,180,9);

		//Homography2D_F64 H = computeTransform(inputA, inputB, detDesc, associate, modelMatcher);
		if(H == null)
			H = computeTransform(inputA, inputB, detDesc, associate, modelMatcher);

		// draw the results
		t.panel = new HomographyStitchPanel(0.5,(inputA.width * 2),inputA.height);
		//panel = new HomographyStitchPanel(0.5,(int) ((inputA.width * (1.2))/1),inputA.height);
		//ShowImages.showWindow(imageA, "Original Image 1");
		//ShowImages.showWindow(imageB, "Original Image 2");
		t.panel.configure(imageA,imageB,H);
		if (widthcounter == 0)
			resizedWidth = imageResize(t.panel.getBufferedImage());
		widthcounter++;
		//System.out.println("ResizedWidth: " + resizedWidth);
		BufferedImage newimage = t.panel.getBufferedImage().getSubimage(t.panel.getBufferedImage().getTileGridXOffset(),
				t.panel.getBufferedImage().getTileGridYOffset(), resizedWidth, t.panel.getBufferedImage().getHeight());
		//ShowImages.showWindow(newimage, "Result Image"); // Before resize
		return newimage; // Need to return the stitched image in BufferedImage format
		//return panel.getBufferedImage();
	}
	
	/**
	 * Renders and displays the stitched together images
	 */
	public static void renderStitching( BufferedImage imageA, BufferedImage imageB, Homography2D_F64 fromAtoB )
	{
		// specify size of output image
		double scale = 0.5;
		int outputWidth = imageA.getWidth();
		int outputHeight = imageA.getHeight();

		// Convert into a BoofCV color format
		MultiSpectral<ImageFloat32> colorA = ConvertBufferedImage.convertFromMulti(imageA, null, ImageFloat32.class);
		MultiSpectral<ImageFloat32> colorB = ConvertBufferedImage.convertFromMulti(imageB, null, ImageFloat32.class);

		// Where the output images are rendered into
		MultiSpectral<ImageFloat32> work = new MultiSpectral<ImageFloat32>(ImageFloat32.class,outputWidth,outputHeight,3);

		// Adjust the transform so that the whole image can appear inside of it
		Homography2D_F64 fromAToWork = new Homography2D_F64(scale,0,colorA.width/4,0,scale,colorA.height/4,0,0,1);
		Homography2D_F64 fromWorkToA = fromAToWork.invert(null);

		// Used to render the results onto an image
		PixelTransformHomography_F32 model = new PixelTransformHomography_F32();
		ImageDistort<MultiSpectral<ImageFloat32>> distort =
				DistortSupport.createDistortMS(ImageFloat32.class, model, new ImplBilinearPixel_F32(), null);

		// Render first image
		model.set(fromWorkToA);
		distort.apply(colorA,work);

		// Render second image
		Homography2D_F64 fromWorkToB = fromWorkToA.concat(fromAtoB,null);
		model.set(fromWorkToB);
		distort.apply(colorB,work);

		// Convert the rendered image into a BufferedImage
		BufferedImage output = new BufferedImage(work.width,work.height,imageA.getType());
		ConvertBufferedImage.convertTo(work,output);

		// draw lines around the distorted image to make it easier to see
		Homography2D_F64 fromBtoWork = fromWorkToB.invert(null);
		Point2D_I32 corners[] = new Point2D_I32[4];
		corners[0] = renderPoint(0,0,fromBtoWork);
		corners[1] = renderPoint(colorB.width,0,fromBtoWork);
		corners[2] = renderPoint(colorB.width,colorB.height,fromBtoWork);
		corners[3] = renderPoint(0,colorB.height,fromBtoWork);
	}

	private static Point2D_I32 renderPoint( int x0 , int y0 , Homography2D_F64 fromBtoWork )
	{
		Point2D_F64 result = new Point2D_F64();
		HomographyPointOps_F64.transform(fromBtoWork, new Point2D_F64(x0, y0), result);
		return new Point2D_I32((int)result.x,(int)result.y);
	}
	
	public static int imageResize(BufferedImage image){
		
	    int w = image.getWidth();
	    int h = image.getHeight();
	    int red1 = 0, green1 = 0, blue1 = 0, red2 = 0, green2 = 0, blue2 = 0,
	    		topBlackCount = 0, botBlackCount = 0;
	    int botLoc = 0, topLoc = 0;
	    boolean foundTop = false, foundBot = false;

    	for (int y = w-1; y >= 0; y--) 
    	{
    		int pixel1 = image.getRGB(y, h-1);
    		int pixel2 = image.getRGB(y, 0);
	    	red1 = (pixel1 >> 16) & 0xff;
	    	green1 = (pixel1 >> 8) & 0xff;
	    	blue1 = (pixel1) & 0xff;
    		red2 = (pixel2 >> 16) & 0xff;
    		green2 = (pixel2 >> 8) & 0xff;
	    	blue2 = (pixel2) & 0xff;
	    	if (red1 == 0 && green1 == 0 && blue1 == 0)
	    		topBlackCount++;
	    	if (red2 == 0 && green2 == 0 && blue2 == 0)
	    		botBlackCount++;
	    	if (topBlackCount >= 25 && (red1 > 0 || green1 > 0 || blue1 > 0) && !foundTop)
	    	{
	    		topLoc = y;
	    		foundTop = true;
	    	}
	    	if (botBlackCount >= 25 && (red2 > 0 || green2 > 0 || blue2 > 0) && !foundBot)
	    	{
	    		botLoc = y;
	    		foundBot = true;
	    	}
	    }
    	//System.out.println("Top Loc: " + topLoc);
    	//System.out.println("Bot Loc: " + botLoc);
	    if (topLoc < w/3 || botLoc < w/3)
	    	return w;
	    else if (topLoc < botLoc)
	    	return topLoc + 1;
	    else
	    	return botLoc + 1;
	}
	
	public static String getLeftFilePath(){	
		userHomeDir = System.getProperty("user.home", ".");
		userHomeDir = userHomeDir.replace('\\', '/');
		// String picDir = userHomeDir + "/Documents/workspace/stitchimage360/pictures/"; // Stephanie Directory
		picDir = userHomeDir + "/workspace/stitchimage360/pictures/leftside"; // Minh Directory
		srcDir = userHomeDir + "/workspace/stitchimage360/src/MyResult/";
		System.out.println("Directory: " + picDir);
		return picDir;	
	}
	
	public static String getRightFilePath(){	
		userHomeDir = System.getProperty("user.home", ".");
		userHomeDir = userHomeDir.replace('\\', '/');
		// String picDir = userHomeDir + "/Documents/workspace/stitchimage360/pictures/"; // Stephanie Directory
		picDir = userHomeDir + "/workspace/stitchimage360/pictures/rightside"; // Minh Directory
		System.out.println("Directory: " + picDir);
		return picDir;	
	}
	
	public static void ConvertImage(BufferedImage image) throws IOException {
		if (image == null)
		{
			System.err.println("Critical Error: No BufferedImage Found");
			System.err.close();
		}
		//int resizedWidth = imageResize(image);
		//BufferedImage newimage = image.getSubimage(image.getTileGridXOffset(),
		//		image.getTileGridYOffset(), resizedWidth, image.getHeight());
		//ShowImages.showWindow(newimage, "Result Image"); // After resize
		// ImageIO.write(newimage, "jpeg", new File(srcDir + "savedimage" + counter + ".jpeg"));
		ImageIO.write(image, "jpeg", new File(srcDir + "savedimage" + counter + ".jpeg"));
		counter++;
	}
	
	public static void main( String args[] ) throws IOException {
		BufferedImage imageA,imageB,imageC,imageD,imageE,imageF,imageG;
		final File dirleft = new File(getLeftFilePath());
		final File dirright = new File(getRightFilePath());
		
		File [] fileListLeft = dirleft.listFiles();
		File [] fileListRight = dirright.listFiles();
		int i = 0;
		int j = 0;
		//go through each file in directory and load image for stitching
		while(i< fileListLeft.length && j < fileListRight.length) {
			
			// Stitch the first 2 images together
			imageA = UtilImageIO.loadImage(fileListLeft[i].toString());
			imageB = UtilImageIO.loadImage(fileListRight[j].toString());
			//imageB = UtilImageIO.loadImage(fileList[i+1].toString());
			imageE = stitch(imageA,imageB, ImageFloat32.class);
			ConvertImage(imageE);
			i++;
			j++;
			
			// Stitch the next 2 images together
			//imageC = UtilImageIO.loadImage(fileList[i+2].toString());
			//imageD = UtilImageIO.loadImage(fileList[i+3].toString());
			//imageF = stitch(imageC,imageD, ImageFloat32.class);
			// imageF = stitch(imageE,imageC, ImageFloat32.class);
			//ConvertImage(imageF);
			
			// Stitch the 2 stitched images together to get the result image
			//imageG = stitch(imageE, imageF, ImageFloat32.class);
			//imageG = stitch(imageF, imageD, ImageFloat32.class);
			//ConvertImage(imageG);
		}
	}
}