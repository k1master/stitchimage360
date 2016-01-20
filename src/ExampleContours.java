import boofcv.abst.feature.detect.edge.DetectEdgeContour;
import boofcv.alg.misc.GImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.edge.FactoryDetectEdgeContour;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;
import georegression.struct.point.Point2D_I32;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * Demonstrates how to use a high level interface to detect image feature contours/edges using
 * different algorithms.  Results are then displayed in several windows.
 *
 * @author Peter Abeles
 */
public class ExampleContours {

	public static void main( String args[] ) {
		BufferedImage image = UtilImageIO.loadImage("C:/Users/trietduc/workspace/stitchimage360/src/simple_objects.jpg");

		ImageUInt8 gray = ConvertBufferedImage.convertFrom(image,(ImageUInt8)null);

		// In many situations the mean pixel value produces reasonable results as a threshold
		double mean = GImageStatistics.mean(gray);
		DetectEdgeContour<ImageUInt8> simple =
				FactoryDetectEdgeContour.binarySimple(mean,true);

		// canny edge detector threshold values are often highly image dependent.  Too low and you
		// get too many lines, too high too few
		DetectEdgeContour<ImageUInt8> canny =
				FactoryDetectEdgeContour.canny(30,200,false,ImageUInt8.class,ImageSInt16.class);

		// Which is why there is the dynamic option, which sets the threshold as a function of
		// the image's edge intensity
		DetectEdgeContour<ImageUInt8> cannyD =
				FactoryDetectEdgeContour.canny(0.05,0.15,true,ImageUInt8.class,ImageSInt16.class);

		// Show the results
		ShowImages.showWindow(image,"Original Image");
		visualize("Binary",simple,gray);
		visualize("Canny",canny,gray);
		visualize("Canny Dynamic",cannyD,gray);
	}

	/**
	 * Draws each edge in the image a different color
	 */
	public static void visualize( String name ,
								  DetectEdgeContour<ImageUInt8> contour ,
								  ImageUInt8 input )
	{
		contour.process(input);

		List<List<Point2D_I32>> edges = contour.getContours();

		// draw each edge a different color
		BufferedImage out = new BufferedImage(input.width,input.height,BufferedImage.TYPE_INT_BGR);

		Random rand = new Random();
		for( List<Point2D_I32> l : edges ) {

			int rgb = rand.nextInt() | 0x101010;

			for( Point2D_I32 p : l ) {
				out.setRGB(p.x,p.y,rgb);
			}
		}

		ShowImages.showWindow(out,name);
	}
}