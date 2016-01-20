package com.stitch360.stream;

import java.awt.image.BufferedImage;

import boofcv.struct.image.ImageFloat32;

import com.stitch360.imagestitching.ImageStitching;
import com.stitch360.recode.ImageToVideo;
import com.stitch360.recode.VideoEncoder;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IError;

public class VidDecoder {
	protected IMediaReader mediaReader; // = new IMediaReader[4];
	protected MyIMediaListener mediaListener;
	protected static int index = 0;
	int vid;
	
	
	public VidDecoder(String url){
		
		mediaListener = new MyIMediaListener();
		mediaReader = ToolFactory.makeReader(url);
		mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
//	    mediaReader.setAddDynamicStreams(false);
	    mediaReader.setQueryMetaData(false);
	    mediaReader.addListener(mediaListener);
		vid = index;
		index++;
	}
	
	public boolean readFrame(){
		IError err = null;
		//while(mediaListener.getFrame() == null){
		 if (mediaReader != null){
			 //System.out.println("read packet");
	            err = mediaReader.readPacket();
	        //System.out.println("end packet");
	        if(err != null ){
	            System.out.println("Error: " + err);
	        }
	        
	        return true;
		 }else{
			 return false;
		 }
		//}
	}
	
	public BufferedImage getFrame(){
		return mediaListener.getFrame();
	}
	
	
	public static void main(String args[]){
		VidDecoder vid0 = new VidDecoder("C:/Users/Brian/Desktop/CSE_145/test2/warrenMall_3_left.MOV");
		VidDecoder vid1 = new VidDecoder("C:/Users/Brian/Desktop/CSE_145/test2/warrenMall_3_right.MOV");
		//VidDecoder vid0 = new VidDecoder("rtsp://192.168.1.2:554/live/ch01_0");
		//VidDecoder vid1 = new VidDecoder("rtsp://192.168.1.4:554/live/ch01_0");
		ImageToVideo vidOut = new ImageToVideo("C:/Users/Brian/Desktop/CSE_145/workspace/stitchimage360/target/iphone/libraryroofPan1.mpeg");
		BufferedImage stitched;
		System.out.println("Decoding video");
		while(true){
			boolean x = vid0.readFrame();
			boolean y = vid1.readFrame();
			
			if(!x || !y){
				break;
			}
			BufferedImage vbi1  = vid0.getFrame();
			BufferedImage vbi2 = vid1.getFrame();
			if(vbi1 != null && vbi2 != null){
				stitched = ImageStitching.stitch(vbi1, vbi2, ImageFloat32.class);
				vidOut.addImage(stitched);
			}else{
				//vidOut.AddFrame(null);
			}
		}
		System.out.println("finished!!");
	}
}
