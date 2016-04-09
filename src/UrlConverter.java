import java.awt.*;        // Using AWT container and component classes
import java.awt.event.*;  // Using AWT event classes and listener interfaces

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */

/**
 * @author gerwazy
 *
 */
public class UrlConverter extends Frame implements ActionListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Label lblURL;    // Declare component Label
	   private Label lblNew_URL;    // Declare component Label
	   private TextField tfURL; // Declare component TextField
	   private TextField tfNew_URL; // Declare component TextField
	   private Button btnConvert;   // Declare component Button
	   private String new_URL;     // New_URL value
	   private static final String REGEX = "\\b&t=\\b\\d";
	   
	   /** Constructor to setup GUI components and event handling */
	   public UrlConverter () {
	      setLayout(new FlowLayout());
	         // "super" Frame (a Container) sets its layout to FlowLayout, which arranges
	         // the components from left-to-right, and flow to next row from top-to-bottom.
	 
	      lblURL = new Label("URL");  // construct Label
	      add(lblURL);                    // "super" Frame adds Label
	 
	      tfURL = new TextField(90); // construct TextField
	      tfURL.setEditable(true);       // set to read-only
	      add(tfURL);                     // "super" Frame adds TextField
	 
	      lblNew_URL = new Label("New URL");  // construct Label
	      add(lblNew_URL);                    // "super" Frame adds Label
	 
	      tfNew_URL = new TextField(90); // construct TextField
	      tfNew_URL.setEditable(true);       // set to read-only
	      add(tfNew_URL);                     // "super" Frame adds TextField
	      
	      btnConvert = new Button("Convert");   // construct Button
	      add(btnConvert);                    // "super" Frame adds Button
	 
	      btnConvert.addActionListener(this);
	         // Clicking Button (source object) fires an ActionEvent.
	         // btnCount (Button) registers this instance as an ActionEvent listener.
	 
	      addWindowListener(this);
	        // "super" Frame (source) fires WindowEvent.
	        // "super" Frame adds "this" object as a WindowEvent listener.
	      setTitle("URL Converter");  // "super" Frame sets its title
	      setSize(700, 200);        // "super" Frame sets its initial window size
	 
	      // For inspecting the components/container objects
	      // System.out.println(this);
	      // System.out.println(lblCount);
	      // System.out.println(tfCount);
	      // System.out.println(btnCount);
	 
	      setVisible(true);         // "super" Frame shows
	 
	      // System.out.println(this);
	      // System.out.println(lblCount);
	      // System.out.println(tfCount);
	      // System.out.println(btnCount);
	   }
	    /** WindowEvent handlers */
	   // Called back upon clicking close-window button
	   @Override
	   public void windowClosing(WindowEvent e) {
	      System.exit(0);  // Terminate the program
	   }
	   
	   // Not Used, but need to provide an empty body
	   @Override
	   public void windowOpened(WindowEvent e)  {
	    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
	    try{
	    String paste = (String) c.getContents(null).getTransferData(DataFlavor.stringFlavor);
	    tfURL.setText(paste);
	    
	    } catch(IOException error){
	        System.out.println("Error" + error.getMessage());
	    } catch (UnsupportedFlavorException flavorexcept){
	        System.out.println("Error" + flavorexcept.getMessage());
	    }
	    
	    }
	   @Override
	   public void windowClosed(WindowEvent e) { }
	   @Override
	   public void windowIconified(WindowEvent e) { }
	   @Override
	   public void windowDeiconified(WindowEvent e) { }
	   @Override
	   public void windowActivated(WindowEvent e) {
	    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
	    try{
	    String paste = (String) c.getContents(null).getTransferData(DataFlavor.stringFlavor);
	    tfURL.setText(paste);
	   
	    } catch(IOException error){
	        System.out.println("Error" + error.getMessage());
	    } catch (UnsupportedFlavorException flavorexcept){
	        System.out.println("Error" + flavorexcept.getMessage());
	    }}
	   @Override
	   public void windowDeactivated(WindowEvent e) { }
	   
	    public void removeTime(){
	           System.out.println(new_URL + "r");
	           Pattern p = Pattern.compile(REGEX);
	           Matcher m = p.matcher(new_URL); // get a matcher object
	           String con_str ="";
	           int count = 0;
	           
	           while(m.find()) {
	             count++;
	             //System.out.println("Match number "+count);
	             //System.out.println("start(): "+m.start());
	             //System.out.println("end(): "+m.end());
	             //int length = m.end() - m.start();
	             if (count == 1){
	              con_str = new_URL.substring(0, m.start()) + new_URL.substring(m.end() + 1);
	              //System.out.println(con_str);
	              
	              //System.out.println(newString);
	              tfNew_URL.setText(con_str);
	              
	            }

	          }
	    
	           
	        }
	        
	   public void convertString(){
	       String str_to_convert  = tfURL.getText();
	       new_URL = str_to_convert.replace("watch?v=", "embed/");
	       tfNew_URL.setText(new_URL);
	       System.out.println(new_URL);
	    }
	   
	/**
	 * The entry main() method 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	      // Invoke the constructor to setup the GUI, by allocating an instance
	      UrlConverter app = new UrlConverter();
	         // or simply "new AWTCounter();" for an anonymous instance
	}
	
	/** ActionEvent handler - Called back upon button-click. */
	   @Override
	   public void actionPerformed(ActionEvent evt) {
	      //revalidate();
	      //new_URL = tfURL.getText();
	      convertString();
	      removeTime();
	      // Display the counter value on the TextField tfCount
	      //tfNew_URL.setText(new_URL); 
	   }

}
