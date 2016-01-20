package com.stitch360.stream;

import java.util.concurrent.BlockingQueue;

//import com.xuggle.xuggler.demos.VideoImage;
import com.stitch360.recode.VideoImage;

public class Sync
{
	public static synchronized void Take(BlockingQueue<Tuple> q1, BlockingQueue<Tuple> q2)
	{
		StitchThread t = (StitchThread) Thread.currentThread();
		try {
			t.t1  =  q1.take();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			t.t2 = q2.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static synchronized void openJavaWindow(long width, long height)
	  {
		StitchThread t = (StitchThread) Thread.currentThread();
		if(t.mScreen == null)
		{
			t.mScreen = new VideoImage(width, height);
			t.flag = true;
		}
	  }
}