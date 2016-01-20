package com.stitch360.stream;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
//import com.xuggle.xuggler.demos.VideoImage;
import com.stitch360.recode.VideoImage;
import javax.swing.*;

public class StreamRTSP extends ReadStream{
	
	  //public static final double SECONDS_BETWEEN_FRAMES = 1/3;
	  public static final double MICRO_SECONDS_BETWEEN_FRAMES = 131000;
	  private static double mLastPtsWrite = Global.NO_PTS;
	  private static double mLastPtsWrite2 = Global.NO_PTS; 
	  public static boolean record = true;
	  public static final String dir1 = "C:/Users/mmm/CSE145/stream1/";
	  public static final String dir2 = "C:/Users/mmm/CSE145/stream2/";

	private static VideoImage[] mScreen = new VideoImage[4];
	
	 /*@Override
	public void outputImage(BufferedImage frame) { 
		// TODO Auto-generated method stub
		//updateJavaWindow(frame);
		System.out.println("streaming from overide vid"+this.vid);
	}*/
	

	static BlockingQueue<Tuple>[] q = new BlockingQueue[4];
	public StreamRTSP(String url,IMediaListener mediaListener, BlockingQueue<Tuple> queue, int i) {
		super(url, mediaListener);
//		if(i == 0){
//			this.q0 = queue;
//		}else if(i == 1){
//			this.q1 = queue;
//		}else if(i == 2){
//			this.q2 = queue;
//		}else{
//			this.q3 = queue;
//		}
		StreamRTSP.q[i] = queue;
		
		// TODO Auto-generated constructor stub
	}
	
	private static void createAndShowGUI() {
        //Create and set up the window.
        final JFrame frame = new JFrame("RecordGUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		JButton openFile = new JButton( "Record" );
	      openFile.addActionListener(
	         new ActionListener() {
	            public void actionPerformed( ActionEvent e )
	            {
	               record = !record;
	            }
	         }
	      );
	      //add(instructionPanel, BorderLayout.PAGE_START);
	      frame.getContentPane().add( openFile, BorderLayout.NORTH );
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

	private static IMediaListener mediaListener1 = new MediaListenerAdapter() {
	    @Override
	    public void onVideoPicture(IVideoPictureEvent event) {
	         try {
	            BufferedImage bi = event.getImage();
	            if (bi != null && record)
	            {
	            	//System.err.println("1: "+event.getTimeStamp());
	            	if (mLastPtsWrite == Global.NO_PTS)
	                    mLastPtsWrite = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;

	                  if (event.getTimeStamp() - mLastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES)
	                  {
		                //System.out.println("at elapsed time of "+event.getTimeStamp(TimeUnit.MILLISECONDS)+" milliseconds elapsed1");
	                    Tuple tuple = new Tuple(bi, event.getTimeStamp(TimeUnit.MILLISECONDS));
	                    q[1].put(tuple);
	                    mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
	                  }
	            }
	        }catch(Exception ex){
	            ex.printStackTrace();
	        }
	    }
	};
	
	private static IMediaListener mediaListener2 = new MediaListenerAdapter() {
	    @Override
	    public void onVideoPicture(IVideoPictureEvent event) {
	         try {
	            BufferedImage bi = event.getImage();
	            if (bi != null && record)
	            {
	            	//System.err.println("2: "+event.getTimeStamp() + "micro: " +MICRO_SECONDS_BETWEEN_FRAMES);
                    //System.out.printf("at elapsed time of %6.60f seconds wrote2\n", MICRO_SECONDS_BETWEEN_FRAMES);
	            	if (mLastPtsWrite2 == Global.NO_PTS)
	                    mLastPtsWrite2 = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;
	            	
	                  if (event.getTimeStamp() - mLastPtsWrite2 >= MICRO_SECONDS_BETWEEN_FRAMES)
	                  {
	                    //System.out.println("at elapsed time of "+event.getTimeStamp(TimeUnit.MILLISECONDS)+" milliseconds elapsed2");
	                    Tuple tuple = new Tuple(bi, event.getTimeStamp(TimeUnit.MILLISECONDS));                  
	                    q[2].put(tuple);
	                    mLastPtsWrite2 += MICRO_SECONDS_BETWEEN_FRAMES;
	                  }
	                  
	            }
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
		BlockingQueue<Tuple> queue1 = new ArrayBlockingQueue<Tuple>(10);
		BlockingQueue<Tuple> queue2 = new ArrayBlockingQueue<Tuple>(10);
		BlockingQueue<Tuple> queueStitch = new ArrayBlockingQueue<Tuple>(10);
		StitchThread stitch = new StitchThread(queue1,queue2, queueStitch);
		StitchThread stitch2 = new StitchThread(queue1,queue2, queueStitch);
		StitchThread stitch3 = new StitchThread(queue1,queue2, queueStitch);
		//StitchThread stitch4 = new StitchThread(queue1,queue2, queueStitch);

		RecodeThread recode = new RecodeThread("C:/Users/mmm/workspace2/stitchimage360/target/lib_16.flv", queueStitch, MICRO_SECONDS_BETWEEN_FRAMES);
		StreamRTSP vid1 = new StreamRTSP("C:/Users/mmm/CSE145/TMC/1_TMC4.mp4", mediaListener1,queue1, 1);
		StreamRTSP vid2 = new StreamRTSP("C:/Users/mmm/CSE145/TMC/2_TMC4.mp4", mediaListener2,queue2, 2);
		//StreamRTSP vid1 = new StreamRTSP("rtsp://192.168.1.2:554/live/ch01_0", mediaListener1,queue1, 1);
		//StreamRTSP vid2 = new StreamRTSP("rtsp://192.168.1.4:554/live/ch01_0", mediaListener2,queue2, 2);
		
		/*javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });*/

		System.out.println("starting threads");
		vid1.start();
	    vid2.start();
	    stitch.start();
	    stitch2.start();
	    stitch3.start();
	    //stitch4.start();
	    recode.start();
	}


	private static void updateJavaWindow(BufferedImage javaImage, int i)
	  {
	    mScreen[i].setImage(javaImage);
	  }

	  /**
	   * Opens a Swing window on screen.
	 * @param i 
	   */
	  /*private static void openJavaWindow(int i)
	  {
	    mScreen[i] = new VideoImage();
	  }*/

	  /**
	   * Forces the swing thread to terminate; I'm sure there is a right
	   * way to do this in swing, but this works too.
	 * @param i 
	   */
	  private static void closeJavaWindow(int i)
	  {
	    System.exit(0);
	  }
	}
