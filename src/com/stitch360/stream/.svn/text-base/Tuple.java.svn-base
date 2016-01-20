package com.stitch360.stream;

import java.awt.image.BufferedImage;
import java.io.File;

public class Tuple {
	
	public Tuple (BufferedImage img, long time)
	{
		bf = img;
		timestamp = time;
		file = null;
	}
	
	public Tuple (BufferedImage img, long time, File f)
    {
            bf = img;
            timestamp = time;
            file = f;
    }

    public BufferedImage getImage()
    {
            return bf;
    }
    
    public long getTime()
    {
            return timestamp;
    }
    
    public File getFile()
    {
    	return file;
    }
    
    private BufferedImage bf;
    private long timestamp;
    private File file;
}
