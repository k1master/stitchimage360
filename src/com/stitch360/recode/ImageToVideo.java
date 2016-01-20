/*
 *  Reads images and makes a video.  Need to be able to read the images in order.
 */

package com.stitch360.recode;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class ImageToVideo {
	
    private static String outputFilename; // = "C:/Users/mmm/CSE145/DramLook.flv";

    private static long counter_mil = 0; 
    
    private static long counter = 0; 
    
    final IMediaWriter writer;
    final float FACTOR  = 4f;
    //public static double v_SECONDS_BETWEEN_FRAMES;
	  private double v_MICRO_SECONDS_BETWEEN_FRAMES;
    /*
     * Will create a writer for file named 'outputFilename'
     */
    public ImageToVideo(String Filename, double interval, int width, int height)
    {
    	outputFilename = Filename;
    	v_MICRO_SECONDS_BETWEEN_FRAMES = interval;
    	// let's make a IMediaWriter to write the file.
        writer = ToolFactory.makeWriter(outputFilename);
        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.
        // 4th and 5th parameter are width and height of the created video.
        writer.addVideoStream(0, 0, width, height);
        //writer.addVideoStream(0, 0, 100, 100);
        //System.err.println("hello");
    }
    
    /*  Encodes parameter BuggeredImage to video 'outputFilename'.
     *  Returns video file 'outputFilename' when parameter is null.
     */
    public void addImage(BufferedImage image, long timestamp)
    {
		if(image != null)
		{				
			BufferedImage bgrScreen = convertToType(image, 
	                   BufferedImage.TYPE_3BYTE_BGR);
			
			/*int scaleX = (int) (1910);
			int scaleY = (int) (1080);
			Image img = bgrScreen.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
			BufferedImage buffered = new BufferedImage(scaleX, scaleY, BufferedImage.TYPE_3BYTE_BGR);
			buffered.getGraphics().drawImage(img, 0, 0 , null);*/
			
			//try{
			//writer.encodeVideo(0, bgrScreen, counter_mil, 
	        //           TimeUnit.MICROSECONDS);
				writer.encodeVideo(0, bgrScreen, timestamp, 
								TimeUnit.MILLISECONDS);
			//}
			//catch(BufferOverflowException e){
				
			//}
			//writer.encodeVideo(0, bgrScreen, counter_mil, 
			//		TimeUnit.MICROSECONDS);
			//System.out.println("ImageToVideo: "+ counter_mil);
			//counter_mil += 80;
			//counter_mil += v_MICRO_SECONDS_BETWEEN_FRAMES;
		}
    }
    
    public static void main(String[] args) {

    	/*ImageToVideo vid = new ImageToVideo("C:/Users/mmm/CSE145/testing2.flv", 40000);
    	File file = null;
    	
    	while ( file == null ) {
        	BufferedImage image = null;
			try {
				image = ImageIO.read(new File("C:/Users/mmm/CSE145/DramLook-"+
	            		+ counter + ".jpeg"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.err.println(e1.getMessage());
				System.exit(0); //exits program if file doesn't exist
			}
        	vid.addImage(image); 
			counter += 40;
    	}*/
    	
    	/*
        // let's make a IMediaWriter to write the file.
        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);
        
        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_FLV1, 
                   320, 240);
                
        for ( ; ; ) {

        	BufferedImage image = null;
			try {
				image = ImageIO.read(new File("C:/Users/mmm/CSE145/DramLook-"+
	            		+ counter_mil + ".jpeg"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.err.println(e1.getMessage());
				System.exit(0); //exits program if file doesn't exist
			}
			System.err.println("read");
			BufferedImage bgrScreen = convertToType(image, 
	                   BufferedImage.TYPE_3BYTE_BGR);

            // encode the image to stream #0
            writer.encodeVideo(0, bgrScreen, counter_mil, 
                   TimeUnit.MILLISECONDS);
            
            counter_mil = counter_mil + 40;
            
        }
        
        // tell the writer to close and write the trailer if  needed
        //writer.close();
         */
    	System.out.println("done");
    }
    
    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        
        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(), 
                 sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
        
    }
    

}
