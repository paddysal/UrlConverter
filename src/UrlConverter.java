import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;        // Using AWT container and component classes
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;  // Using AWT event classes and listener interfaces
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
 * @author Patryk Salek
 */


/*
 * PLAYLISTS
 * https://www.youtube.com/watch?v=c7nRTF2SowQ
 * https://www.youtube.com/watch?v=acXXyruMtaY&list=PLs1-UdHIwbo5AZVWE4IKJuADxWpz7e-tw
 * https://www.youtube.com/watch?v=Aj0QhLjgxfU&list=PLs1-UdHIwbo5AZVWE4IKJuADxWpz7e-tw&index=2
 * (\&list=[a-zA-Z 0-9 -]+\&?\index=?\d+?)
 */

/*
 * TIMESTAMPS
 * https://youtu.be/xnER10j4ZBc?t=1m33s
 * https://www.youtube.com/watch?v=xnER10j4ZBc&t=215 
 * https://youtu.be/dT9eI40RNoQ?t=1h51m41s
 * (\&?\??t=\d*?[h]?\d*[m]?\d*[s]|\&t=\d*) should match either style of timestamp
 */

//\?t|\&t|\&l
//\?t.*|\&t.*|\&l.*
//[&|?][t|l].*

/*
 * COMBINED
 * (\&?\??t=\d*?[h]?\d*[m]?\d+[s]|\&t=\d*|\&list=[a-zA-Z 0-9 -]+)
 */
public class UrlConverter implements ActionListener, WindowListener, ClipboardOwner {

	private Label lblURL;    // Declare component Label
	private Label lblNew_URL;    // Declare component Label
	private Label listInstructions;
	private Label listInfo;
	private Label urlInfo;

	private TextField tfURL; // Declare component TextField
	private TextField tfNew_URL; // Declare component TextField

	private Button btnConvert;   // Declare component Button
	private Button btnAddRule; //Declare button for adding new textboxes for rules

	private String new_URL;     // New_URL value
	private String convertedUrl;

	//REGEX patterns to match against provided url
	private static final String REGEX_REMOVE_TIME = "(\\&?\\??t=\\d*?[h]?\\d*[m]?\\d*[s]|\\&t=\\d*)";
	//private static final String REGEX = "[&|?][t|l].*";
	private static final String REGEX = "\\?t.*|\\&t.*|\\&l.*";
	private static final String REGEX_REMOVE_PLAYLIST = "(\\&list=[a-zA-Z 0-9 -]+\\&?\\index=?\\d+?)";
	private static final String REGEX_MATCH_ALL = "(\\&?\\??t=\\d*?[h]?\\d*[m]?\\d*[s]|\\&t=\\d*|\\&list=[a-zA-Z 0-9 -]*)";
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
		urlInfo = new Label("Textfield below gets strings from your clipboard, when you copy a new string,"
				+ " it will be pasted in the text box below");

		lblURL = new Label("URL");  // construct Label
		lblNew_URL = new Label("New URL");  // construct Label
		listInstructions = new Label("Ctrl click to select more than one option");
		listInfo = new Label("Advanced settings, may give out unwanted results, proceed with caution");
		//
		String data[] = {"Remove Time Stamp","Remove Feature", "Replace Start Tag", "Remove Playlist"};

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
		getClipboardContents();
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

	public void removeAll(String convertedString) {
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(convertedString); // get a matcher object
		int count = 0;
		convertedUrl = "";
		while(m.find()) {
			count++;
			if (count == 1){
				convertedUrl = convertedString.substring(0, m.start()) + convertedString.substring(m.end());;
				tfNew_URL.setText(convertedUrl);
			}
		}
	}
	
	public void removeTime(){
		
		Pattern p = Pattern.compile(REGEX_MATCH_ALL);
		Matcher m = p.matcher(new_URL); // get a matcher object
		if(Pattern.matches(REGEX, new_URL)) {
			System.out.println("found a match");
		}
		convertedUrl ="";
		int count = 0;

		while(m.find()) {
			count++;
			if (count == 1){
				convertedUrl = new_URL.substring(0, m.start()) + new_URL.substring(m.end() + 1);;
				tfNew_URL.setText(convertedUrl);
			}
		}
	}


	public String convertString(){
		String str_to_convert  = tfURL.getText();
		new_URL = str_to_convert.replace("watch?v=", "embed/");
		tfNew_URL.setText(new_URL);
		System.out.println(new_URL);
		return new_URL;
	}
	public void displayGUI(){
		frame = new JFrame("Url Converter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		// set the layout of the frame to FlowLayout, which arranges
		// the components from left-to-right, and flow to next row from top-to-bottom.
		frame.add(lblURL);                    // add label to the frame
		frame.add(urlInfo);
		frame.add(tfURL);                     // Frame adds TextField
		frame.add(lblNew_URL);                    // Frame adds Label
		frame.add(tfNew_URL);                     // Frame adds TextField
		frame.add(btnConvert);                    // Frame adds Button
		frame.add(btnAddRule);					// Add Rule button to the frame
		frame.add(listInfo);
		frame.add(listInstructions);
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

	/**
	 * Get the String residing on the clipboard.
	 *
	 * @return any text found on the Clipboard; if none found, return an
	 * empty String.
	 */
	public String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		//odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText =
				(contents != null) &&
				contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String)contents.getTransferData(DataFlavor.stringFlavor);
				tfURL.setText(result);
			}
			catch (UnsupportedFlavorException | IOException ex){
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Place a String on the clipboard, and make this class the
	 * owner of the Clipboard's contents.
	 */
	public void setClipboardContents(String aString){
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}
	/** ActionEvent handler - Called back upon button-click. */
	@Override
	public void actionPerformed(ActionEvent evt) {

		if(evt.getSource() == btnConvert){
			removeAll(convertString());
			setClipboardContents(tfNew_URL.getText()); //add the converted url to the clipboard
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
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub

	}

}
