package com.stitch360.stream;

import java.awt.image.BufferedImage;

import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class MyIMediaListener extends MediaListenerAdapter{

	protected BufferedImage bi;
	
	@Override
    public void onVideoPicture(IVideoPictureEvent event) {
         try {
            BufferedImage image = event.getImage();
            if (image != null){
            	this.bi = image;
            	//System.out.println("got image");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
	}
	
	
	public BufferedImage getFrame(){
		return bi;
	}
}
