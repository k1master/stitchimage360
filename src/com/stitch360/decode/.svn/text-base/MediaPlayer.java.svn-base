package com.stitch360.decode;
import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IError;
import com.stitch360.recode.VideoImage;
import com.stitch360.stream.Tuple;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;
/**
 * Small test on the Xuggler library as we try and get the cameras
 * streaming video that we can access through java.
 * @author Ryan
 */
public class MediaPlayer {
private static VideoImage mScreen = null;
static double lastTimeStamp = Global.NO_PTS;
static double timer_counter=0;
private static BlockingQueue<Tuple> queue = new ArrayBlockingQueue<Tuple>(10);
private static boolean first = true;
private static IMediaListener mediaListener = new MediaListenerAdapter() {
    @Override
    public void onVideoPicture(IVideoPictureEvent event) {
         try {
            BufferedImage bi = event.getImage();
            if (bi != null)
            {
            	if(first)
            	{
            		openJavaWindow(bi.getWidth(),bi.getHeight());
            		first = false;
            	}
            	Tuple tuple = new Tuple(bi, event.getTimeStamp(TimeUnit.MILLISECONDS));
            	queue.put(tuple);
            }
            //updateJavaWindow(bi);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
};

/**
 * @param args
 */
public static void main(String[] args) {
    //IMediaReader mediaReader = ToolFactory.makeReader("rtsp://192.168.1.2:554/live/ch01_0");
    IMediaReader mediaReader = ToolFactory.makeReader("C:/Users/mmm/workspace2/stitchimage360/target/TMC_jogging80_2.flv");
//    IMediaReader mediaReader = ToolFactory.makeReader("cucina01.avi"); //file
    
    mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
//    mediaReader.setAddDynamicStreams(false);
    mediaReader.setQueryMetaData(false);
    mediaReader.addListener(mediaListener);
//    mediaReader.getContainer().setInputBufferLength(64000000);
//    mediaReader.getContainer().setProperty("probesize", 500000);
    
//    IMediaWriter mediaWriter = ToolFactory.makeWriter("receivedData.mp4", mediaReader);
//    mediaReader.addListener(mediaWriter);
    //openJavaWindow();
    int delay = 10; //milliseconds
    ActionListener taskPerformer = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            Tuple peek = queue.peek();
            if(peek != null && peek.getImage() != null && peek.getTime() <= timer_counter)
            {

            	Tuple tuple = null;
				try {
					tuple = queue.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				updateJavaWindow(tuple.getImage());
            	lastTimeStamp = tuple.getTime();            	             
            }
            timer_counter += 10;
        }
    };
    new Timer(delay, taskPerformer).start();
    
    while(true){
//        System.out.println("reading packet");        
        IError err = null;
                if (mediaReader != null)
                    err = mediaReader.readPacket();
//        System.out.println("end packet");
        if(err != null ){
            System.out.println("Error: " + err);
            break;
        }
    }
    closeJavaWindow();    
}

  private static void updateJavaWindow(BufferedImage javaImage)
  {
    mScreen.setImage(javaImage);
  }

  /**
   * Opens a Swing window on screen.
   */
  private static void openJavaWindow(int width, int height)
  {
    mScreen = new VideoImage(width, height);
  }

  /**
   * Forces the swing thread to terminate; I'm sure there is a right
   * way to do this in swing, but this works too.
   */
  private static void closeJavaWindow()
  {
    System.exit(0);
  }
}
