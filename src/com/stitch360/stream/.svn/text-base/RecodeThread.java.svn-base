package com.stitch360.stream;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.BlockingQueue;

import com.stitch360.recode.ImageToVideo;

public class RecodeThread extends Thread{
	BlockingQueue<Tuple> recode;
	BufferedImage bi;
	long timestamp;
	ImageToVideo vid;
	Tuple tuple;
	String name;
	double interv;
	boolean first = true;
	//int width, height;
	
	public RecodeThread(){
		
	}
	public RecodeThread(String outputFilename, BlockingQueue<Tuple> recode, double interval){
		//this.vid = new ImageToVideo(outputFilename, interval);
		this.name = outputFilename;
		this.interv = interval;
		this.recode = recode;
	}
	
	public void run(){
		//openJavaWindow();
		while(true) {
			//System.out.println("Running stitchthread");
			bi = null;
			timestamp = -1;
			try {
				tuple  =  recode.take();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			bi = tuple.getImage();
			timestamp = tuple.getTime();
		  if (bi != null && timestamp != -1){
			  if(first)
			  {
				  this.vid = new ImageToVideo(name, interv, bi.getWidth(), bi.getHeight());
				  first = false;
			  }
			  
			  vid.addImage(bi, timestamp);
			  //bi = null;
		  }
		  
		}
	}
	
	
}
