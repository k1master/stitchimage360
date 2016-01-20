import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.media.*;

public class CustomMediaPlayer2 extends JPanel{
   private Player player;
   private File file;
   private Dimension area; //indicates area taken up by graphics
   private Vector<Rectangle> circles; //coordinates used to draw graphics
   private JPanel drawingPane;
   private static Component visualComponent;
   private static JScrollPane scroller;
   private static boolean flag = false;
   private static JComponent newContentPane;
   private static MyListener myListener;

public CustomMediaPlayer2()
   {
		//super(new BorderLayout());
		myListener = new MyListener();

		area = new Dimension(0,0);
		circles = new Vector<Rectangle>();
		
        //Set up the instructions.
        JLabel instructionsLeft = new JLabel(
                        "Click left mouse button to place a circle.");
        JLabel instructionsRight = new JLabel(
                        "Click right mouse button to clear drawing area.");
        JPanel instructionPanel = new JPanel(new GridLayout(0,1));
        instructionPanel.setFocusable(true);
        instructionPanel.add(instructionsLeft);
        instructionPanel.add(instructionsRight);
       
      
	   String JFFMPEG_VIDEO = "net.sourceforge.jffmpeg.VideoDecoder"; 
	   try {
	       Codec video = (Codec) Class.forName(JFFMPEG_VIDEO).newInstance();
	       PlugInManager.addPlugIn(JFFMPEG_VIDEO,
	               video.getSupportedInputFormats(),
	               video.getSupportedOutputFormats(null),
	               PlugInManager.CODEC);
	   } catch (Exception e) {
	       e.printStackTrace();
	   }

      JButton openFile = new JButton( "Open file to play" );
      openFile.addActionListener(
         new ActionListener() {
            public void actionPerformed( ActionEvent e )
            {
               openFile();
               try {
				createPlayer();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            }
         }
      );
      //add(instructionPanel, BorderLayout.PAGE_START);
      add( openFile, BorderLayout.NORTH );
  
      //setSize( 500, 500 );
      //show();
      setVisible(true);
   }

   private void openFile()
   {      
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.addMouseMotionListener(myListener);

      fileChooser.setFileSelectionMode(
         JFileChooser.FILES_ONLY );
      int result = fileChooser.showOpenDialog( this );

      // user clicked Cancel button on dialog
      if ( result == JFileChooser.CANCEL_OPTION )
         file = null;
      else
         file = fileChooser.getSelectedFile();
   }

   
private void createPlayer() throws MalformedURLException
   {
      if ( file == null )
         return;

      removePreviousPlayer();
      
      File dram = new File("C:/Users/mmm/CSE145/DramLook.mpeg");
      System.err.println(dram.toURI().toURL());

      try {
         // create a new player and add listener
    	 //player = Manager.createPlayer( file.toURI().toURL() );
         player = Manager.createPlayer(dram.toURI().toURL());
         player.addControllerListener( new EventHandler() );
         player.start();  // start player
      }
      catch ( Exception e ){
         JOptionPane.showMessageDialog( this,
            "Invalid file or location", "Error loading file",
            JOptionPane.ERROR_MESSAGE );
      }
   }

   private void removePreviousPlayer()
   {
      if ( player == null )
         return;

      player.close();

      Component visual = player.getVisualComponent();
      Component control = player.getControlPanelComponent();

      Container c = getRootPane();
     
      if ( visual != null ) 
         c.remove( visual );

      if ( control != null ) 
         c.remove( control );
   }

   public static void main(String args[])
   {
       javax.swing.SwingUtilities.invokeLater(new Runnable() {
           public void run() {
       //Create and set up the window.
       JFrame frame = new JFrame("CustomeMediaPlayer2");
       frame.setSize(500,500);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

       //Create and set up the content pane.
       newContentPane = new CustomMediaPlayer2();
       newContentPane.setOpaque(true); //content panes must be opaque
       frame.setContentPane(newContentPane);

       //Display the window.
       frame.pack();
       frame.setVisible(true);
       visualComponent.repaint();
       //scroller.revalidate();
       //scroller.repaint();
   }
});

      //CustomMediaPlayer2 app = new CustomMediaPlayer2();

      /*app.addWindowListener(
         new WindowAdapter() {
            public void windowClosing( WindowEvent e )
            {
               System.exit(0);
            }
         }
      );*/
   }
   
   public void mouseClicked(MouseEvent e){
	   System.err.println("hello");
   }

   // inner class to handler events from media player
   private class EventHandler implements ControllerListener {
	   
      public void controllerUpdate( ControllerEvent e ) {
         if ( e instanceof RealizeCompleteEvent ) {
            
            // load Visual and Control components if they exist
            visualComponent =
               player.getVisualComponent();

            if ( visualComponent != null )
            {
               //visualComponent.setPreferredSize(new Dimension(100, 240));
                scroller = new JScrollPane();
                scroller.setViewportView(visualComponent);
                scroller.setPreferredSize(new Dimension(200,200));
                
                scroller.getHorizontalScrollBar().addMouseMotionListener(myListener);
                scroller.getVerticalScrollBar().addMouseMotionListener(myListener);
                add( scroller, BorderLayout.CENTER );
                //flag = true;
            }

            Component controlsComponent =
               player.getControlPanelComponent();

            if ( controlsComponent != null )
               add( controlsComponent, BorderLayout.SOUTH );

            doLayout();
         }
      }
   }

   private class MyListener extends MouseInputAdapter {

	    public void mouseDragged(MouseEvent e) {
	    	visualComponent.repaint();
	    	//scroller.revalidate();
	        //scroller.repaint();
	    }

	    public void mouseMoved(MouseEvent e) {
	    }
   }

 
}
