import java.awt.*;        // Using AWT container and component classes
import java.awt.event.*;  // Using AWT event classes and listener interfaces
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

/**
 * 
 */

/**
 * @author gerwazy
 *
 */
public class UrlConverter implements ActionListener, WindowListener {

	private Label lblURL;    // Declare component Label
	   private Label lblNew_URL;    // Declare component Label
	   private TextField tfURL; // Declare component TextField
	   private TextField tfNew_URL; // Declare component TextField
	   private Button btnConvert;   // Declare component Button
	   private Button btnAddRule; //Declare button for adding new textboxes for rules
	   private String new_URL;     // New_URL value
	   private static final String REGEX = "\\b&t=\\b\\d";
	   private JTextField tfield;
	   private int count;
	   private String nameTField;
	   private JFrame frame;
	   private JList<String> choices;
	   private JScrollPane listScroller;
	   
	   /** Constructor to setup GUI components and event handling */
	   public UrlConverter () {
	      
		  // initialize variables
		  count = 0;
	      nameTField = "tField";
		  
	      //initialize textfields
	      tfURL = new TextField(90); // construct TextField
	      tfNew_URL = new TextField(90); // construct TextField
	      tfNew_URL.setEditable(true);       // set to read-only
	      
	      //initialize labels
	      lblURL = new Label("URL");  // construct Label
	      lblNew_URL = new Label("New URL");  // construct Label
	      //
	      String data[] = {"\\b&t=\\b\\d","Item 2", "Item 3"};
	      
	      //initialize JList and its scroller
	      choices = new JList<String>(data);
	      choices.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	      choices.setLayoutOrientation(JList.VERTICAL);
	      choices.setVisibleRowCount(-1);
	      listScroller = new JScrollPane(choices);
	      listScroller.setPreferredSize(new Dimension(250,80));
	      
	      //initialize buttons
	      btnConvert = new Button("Convert");   // construct Button
	      btnAddRule = new Button("Add Rule");
	      btnAddRule.addActionListener(this);
	      btnConvert.addActionListener(this); // Clicking Button (source object) fires an ActionEvent.

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
	public void displayGUI(){
		frame = new JFrame("Url Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        // set the layout of the frame to FlowLayout, which arranges
        // the components from left-to-right, and flow to next row from top-to-bottom.
        frame.add(lblURL);                    // add label to the frame
        frame.add(tfURL);                     // Frame adds TextField
        frame.add(lblNew_URL);                    // Frame adds Label
        frame.add(tfNew_URL);                     // Frame adds TextField
        frame.add(btnConvert);                    // Frame adds Button
        frame.add(btnAddRule);					// Add Rule button to the frame
        //frame.add(choices);	//add choices listbox to the frame
        frame.add(listScroller);
	    frame.addWindowListener(this);
	        // Frame (source) fires WindowEvent.
	        // Frame adds "this" object as a WindowEvent listener.
	    frame.setTitle("URL Converter");  // "super" Frame sets its title
	    frame.setSize(700, 400);        // "super" Frame sets its initial window size
	    frame.setVisible(true);         // "super" Frame shows
	}
	/**
	 * The entry main() method 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	      // Invoke the constructor to setup the GUI, by allocating an instance
	      //UrlConverter app = new UrlConverter();
	         // or simply "new AWTCounter();" for an anonymous instance
		SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new UrlConverter().displayGUI();
            }
        });
	}
	
	/** ActionEvent handler - Called back upon button-click. */
	   @Override
	   public void actionPerformed(ActionEvent evt) {
	      
		   if(evt.getSource() == btnConvert){
			   //revalidate();
		      //new_URL = tfURL.getText();
		      convertString();
		      removeTime();
		      // Display the counter value on the TextField tfCount
		      //tfNew_URL.setText(new_URL); 
		   } else if(evt.getSource() == btnAddRule){
			   tfield = new JTextField(50);
               tfield.setName(nameTField + count);
               count++;
               frame.add(tfield);
               frame.revalidate();  // For JDK 1.7 or above.
               //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
               frame.repaint();  
		   }
	   }

}
