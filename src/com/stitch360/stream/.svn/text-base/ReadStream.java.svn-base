package com.stitch360.stream;

import java.awt.image.BufferedImage;
import java.lang.reflect.Array;

import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.IError;

public class ReadStream extends Thread{
	//private MediaListenerAdapter[] mediaListener = new MediaListenerAdapter[4];
	private IMediaReader[] mediaReader = new IMediaReader[4]; 
	static int i = 0;
	int vid;
	
	public ReadStream(String url, IMediaListener mediaListener){
		mediaReader[i] = ToolFactory.makeReader(url);
		mediaReader[i].setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		mediaReader[i].setQueryMetaData(false);	
		mediaReader[i].addListener(mediaListener);
		vid = i;
		i++;
	}


	public IMediaReader getMediaReader(){
		return mediaReader[vid];
	}
	
	public IError getPacket(){
		return mediaReader[vid].readPacket();
	}
	
	public void runStream(){
		 while(true){
//		        System.out.println("reading packet");        
		        IError err = null;
		                if (mediaReader[vid] != null)
		                    err = mediaReader[vid].readPacket();
//		        System.out.println("end packet");
		        if(err != null ){
		            System.out.println("Error: " + err);
		            break;
		        }
		    }
	}
	
	public void run(){
		
		 while(true){
			 //System.out.println("reading packet " + vid);    
		        IError err = null;
		        if (mediaReader[vid] != null)
		            err = mediaReader[vid].readPacket();
//		        System.out.println("end packet");
		        if(err != null ){
		           // System.out.println("Error: " + err);
		            break;
		        }
		        
		        
		    }
	}
	
	public int getVidNum(){
		return vid;
	}
	
}

