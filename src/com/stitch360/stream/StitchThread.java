package com.stitch360.stream;
import boofcv.gui.image.HomographyStitchPanel;
import boofcv.struct.image.ImageFloat32;
import com.stitch360.imagestitching.*;
//import com.xuggle.xuggler.demos.VideoImage;
import com.stitch360.recode.VideoImage;

import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;

public class StitchThread extends Thread{
	static BlockingQueue<Tuple> q1;
	static BlockingQueue<Tuple> q2;
	static BlockingQueue<Tuple> q3;
	BufferedImage bi1;
	BufferedImage bi2;
	Tuple t1;
	Tuple t2;
	public HomographyStitchPanel panel = null;
	public static VideoImage mScreen = null;
	public boolean flag = false;
	public StitchThread(BlockingQueue<Tuple> queue1, BlockingQueue<Tuple> queue2, BlockingQueue<Tuple> queue3) {
		// TODO Auto-generated constructor stub
		StitchThread.q1 = queue1;
		StitchThread.q2 = queue2;
		StitchThread.q3 = queue3;
	}

	public void run(){
		while(true) {
			//System.out.println("Running stitchthread");
			t1 = null;
			t2 = null;
			Sync.Take(q1,q2);
			
		   bi1 = t1.getImage();
		   bi2 = t2.getImage();
		  if (bi1 != null && bi2 != null){
			  //System.out.println("can stitch pic "+ bi1.toString() + " and " + bi2.toString());
			  //System.out.println("can stitch pic "+ 1 + " and " + 2);
			  //synchronized(this) {
			  //System.err.println("bi1: "+bi1+"   bi2: "+bi2);
			  BufferedImage stitched =ImageStitching.stitch(bi1, bi2, ImageFloat32.class);
			  long min = Math.min(t1.getTime(),t2.getTime());
              Tuple tuple = new Tuple(stitched, min);
			  try {
				q3.put(tuple);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 // }
			  bi1 = null;
			  bi2 = null;
			  if(!flag)
				  Sync.openJavaWindow(stitched.getWidth(), stitched.getHeight());

			  updateJavaWindow(stitched);
			  //System.err.println("stitched");
		  }//else if (bi1 == null && bi2 == null){
			 // break;
		  //}
		  
		}
	}

	private static void updateJavaWindow(BufferedImage javaImage)
	  {
	    mScreen.setImage(javaImage);
	  }
	 private static void closeJavaWindow()
	  {
	    System.exit(0);
	  }

	/*private static void openJavaWindow()
	  {
	    mScreen = new VideoImage();
	  }*/
	
}
