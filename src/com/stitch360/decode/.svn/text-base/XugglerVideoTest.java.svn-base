package com.stitch360.decode;
import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.demos.VideoImage;
import java.awt.image.BufferedImage;
/**
 * Small test on the Xuggler library as we try and get the cameras
 * streaming video that we can access through java.
 * @author Ryan
 */
public class XugglerVideoTest {
private static VideoImage mScreen = null;
private static IMediaListener mediaListener = new MediaListenerAdapter() {
    @Override
    public void onVideoPicture(IVideoPictureEvent event) {
         try {
            BufferedImage bi = event.getImage();
            if (bi != null)
            updateJavaWindow(bi);
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
    IMediaReader mediaReader = ToolFactory.makeReader("C:/Users/Brian/Desktop/CSE_145/workspace/stitchimage360/IMG_1003.MOV");
//    IMediaReader mediaReader = ToolFactory.makeReader("cucina01.avi"); //file
    
    mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
//    mediaReader.setAddDynamicStreams(false);
    mediaReader.setQueryMetaData(false);
    mediaReader.addListener(mediaListener);
//    mediaReader.getContainer().setInputBufferLength(64000000);
//    mediaReader.getContainer().setProperty("probesize", 500000);
    
//    IMediaWriter mediaWriter = ToolFactory.makeWriter("receivedData.mp4", mediaReader);
//    mediaReader.addListener(mediaWriter);
    openJavaWindow();
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
  private static void openJavaWindow()
  {
    mScreen = new VideoImage();
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
