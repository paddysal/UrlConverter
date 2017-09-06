import java.awt.AWTException;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; // Using AWT event classes and listener interfaces
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * @author Patryk Salek
 */

/*
 * PLAYLISTS https://www.youtube.com/watch?v=c7nRTF2SowQ
 * https://www.youtube.com/watch?v=acXXyruMtaY&list=PLs1-
 * UdHIwbo5AZVWE4IKJuADxWpz7e-tw
 * https://www.youtube.com/watch?v=Aj0QhLjgxfU&list=PLs1-
 * UdHIwbo5AZVWE4IKJuADxWpz7e-tw&index=2 (\&list=[a-zA-Z 0-9 -]+\&?\index=?\d+?)
 */

/*
 * TIMESTAMPS https://youtu.be/xnER10j4ZBc?t=1m33s
 * https://www.youtube.com/watch?v=xnER10j4ZBc&t=215
 * https://youtu.be/dT9eI40RNoQ?t=1h51m41s
 * (\&?\??t=\d*?[h]?\d*[m]?\d*[s]|\&t=\d*) should match either style of
 * timestamp
 */

// \?t|\&t|\&l
// \?t.*|\&t.*|\&l.*
// [&|?][t|l].*

/*
 * COMBINED (\&?\??t=\d*?[h]?\d*[m]?\d+[s]|\&t=\d*|\&list=[a-zA-Z 0-9 -]+)
 */

/*
 * &feature=youtu.be
 */

/*
 * Adding library to listen to global events
 * https://github.com/kwhat/jnativehook/releases
 */

public class UrlConverter extends JFrame implements ClipboardOwner, ActionListener, WindowListener, NativeKeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Label lblURL; // Declare component Label
	private Label lblNew_URL; // Declare component Label
	private Label listInstructions;
	private Label listInfo;
	private Label urlInfo;

	private TextField tfURL; // Declare component TextField
	private TextField tfNew_URL; // Declare component TextField

	private Button btnConvert; // Declare component Button
	private Button btnAddRule; // Declare button for adding new textboxes for rules

	private String new_URL; // New_URL value
	private String convertedUrl;
	private String[] data = { "Remove Time Stamp", "Remove Feature", "Replace Start Tag", "Remove Playlist" };

	// REGEX patterns to match against provided url
	private static final String REGEX_REMOVE_TIME = "(\\&?\\??t=\\d*?[h]?\\d*[m]?\\d*[s]|\\&t=\\d*)";
	// private static final String REGEX = "[&|?][t|l].*";
	private static final String REGEX = "\\?t.*|\\&t.*|\\&l.*|\\&f.*";
	// private static final String REGEX_REMOVE_PLAYLIST = "(\\&list=[a-zA-Z 0-9
	// -]+\\&?\\index=?\\d+?)";
	// private static final String REGEX_MATCH_ALL =
	// "(\\&?\\??t=\\d*?[h]?\\d*[m]?\\d*[s]|\\&t=\\d*|\\&list=[a-zA-Z 0-9 -]*)";
	private JTextField tfield;
	private int count;
	private String nameTField;
	private JList<String> choices;
	private JScrollPane listScroller;
	TrayIcon trayIcon;
	SystemTray tray;

	// Set of currently pressed keys
	private final Set<String> keysPressed = new HashSet<String>();
	
	/** Logging */
	private static final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());

	UrlConverter() {
		super("SystemTray test");
		System.out.println("creating instance");

		// initialize variables
		count = 0;
		nameTField = "tField";

		// initialize textfields
		tfURL = new TextField(90); // construct TextField
		tfNew_URL = new TextField(90); // construct TextField
		tfNew_URL.setEditable(true); // set to read-only

		// initialize labels
		urlInfo = new Label("Textfield below gets strings from your clipboard, when you copy a new string,"
				+ " it will be pasted in the text box below");
		urlInfo.setForeground(Color.WHITE);
		lblURL = new Label("URL"); // construct Label
		lblURL.setForeground(Color.WHITE);
		lblNew_URL = new Label("New URL"); // construct Label
		lblNew_URL.setForeground(Color.WHITE);
		listInstructions = new Label("Ctrl click to select more than one option");
		listInstructions.setForeground(Color.WHITE);
		listInfo = new Label("Advanced settings, may give out unwanted results, proceed with caution");
		listInfo.setForeground(Color.WHITE);

		// Initialise JList and its scroller
		choices = new JList<String>(data);
		choices.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		choices.setLayoutOrientation(JList.VERTICAL);
		choices.setVisibleRowCount(-1);

		int start = 0;
		int end = choices.getModel().getSize() - 1;
		if (end >= 0) {
			choices.setSelectionInterval(start, end);
		}

		listScroller = new JScrollPane(choices);
		listScroller.setPreferredSize(new Dimension(250, 80));

		// initialize buttons
		btnConvert = new Button("Convert"); // construct Button
		btnAddRule = new Button("Add Rule");
		btnAddRule.addActionListener(this);
		btnConvert.addActionListener(this); // Clicking Button (source object) fires an ActionEvent.

		setLayout(new FlowLayout());
		// set the layout of the frame to FlowLayout, which arranges
		// the components from left-to-right, and flow to next row from top-to-bottom.
		add(lblURL); // add label to the frame
		add(urlInfo);
		add(tfURL); // Frame adds TextField
		add(lblNew_URL); // Frame adds Label
		add(tfNew_URL); // Frame adds TextField
		add(btnConvert); // Frame adds Button
		add(btnAddRule); // Add Rule button to the frame
		add(listInfo);
		add(listInstructions);
		add(listScroller);
		addWindowListener(this);

		// Disable parent logger and set the desired level.
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.ALL);

		// Add our custom formatter to a console handler.
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.WARNING);
		logger.addHandler(handler);
		
		
		try {
			System.out.println("setting look and feel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Unable to set LookAndFeel");
		}
		if (SystemTray.isSupported()) {
			System.out.println("system tray supported");
			tray = SystemTray.getSystemTray();

			Image image = Toolkit.getDefaultToolkit().getImage("Pinwheel-48.png");
			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Exiting....");
					System.exit(0);
				}
			};
			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem("Exit");
			defaultItem.addActionListener(exitListener);
			popup.add(defaultItem);
			defaultItem = new MenuItem("Open");
			defaultItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(true);
					setExtendedState(JFrame.NORMAL);
				}
			});
			popup.add(defaultItem);
			trayIcon = new TrayIcon(image, "SystemTray Demo", popup);
			trayIcon.setImageAutoSize(true);
		} else {
			System.out.println("system tray not supported");
		}
		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {

				if (e.getNewState() == ICONIFIED) {
					try {
						tray.add(trayIcon);
						trayIcon.displayMessage("Minimized!", "Will be here if you need me :)",
								TrayIcon.MessageType.INFO);
						setVisible(false);
						System.out.println("added to SystemTray");

					} catch (AWTException ex) {
						System.out.println("unable to add to tray");
					}
				}

				if (e.getNewState() == 7) {
					try {
						tray.add(trayIcon);
						setVisible(false);
						System.out.println("added to SystemTray");
					} catch (AWTException ex) {
						System.out.println("unable to add to system tray");
					}
				}

				if (e.getNewState() == MAXIMIZED_BOTH) {
					tray.remove(trayIcon);
					setVisible(true);
					System.out.println("Tray icon removed");
				}

				if (e.getNewState() == NORMAL) {
					tray.remove(trayIcon);
					setVisible(true);
					System.out.println("Tray icon removed");
				}
			}
		});
		// Path tray_icon = Paths.get(System.getProperty("user.home"),"My
		// Documents\\UrlConverter", "Pinwheel-48.png");
		// String tray_icon_str = tray_icon.toString();
		setIconImage(Toolkit.getDefaultToolkit().getImage("Pinwheel-48.png"));

		// trayIcon.displayMessage("Tester!", "Some action performed",
		// TrayIcon.MessageType.INFO)

		// Frame (source) fires WindowEvent.
		// Frame adds "this" object as a WindowEvent listener.
		setTitle("URL Converter"); // "super" Frame sets its title
		setSize(700, 400); // "super" Frame sets its initial window size
		getContentPane().setBackground(Color.DARK_GRAY);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true); // "super" Frame shows
	}

	public static void main(String[] args) {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(new UrlConverter());
		// new UrlConverter();
	}

	public void removeAll(String convertedString) {
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(convertedString); // get a matcher object
		int count = 0;
		convertedUrl = "";
		while (m.find()) {
			count++;
			if (count == 1) {
				convertedUrl = convertedString.substring(0, m.start()) + convertedString.substring(m.end());
				;
				tfNew_URL.setText(convertedUrl);
			}
		}
	}

	public String returnRegex() {

		/*
		 * Path path = Paths.get("src/main/resources/shakespeare.txt"); try
		 * (BufferedWriter writer = Files.newBufferedWriter(path,
		 * Charset.forName("UTF-8"))) {
		 * writer.write("To be, or not to be. That is the question."); } catch
		 * (IOException ex) { ex.printStackTrace(); }
		 */

		Object[] selectedIndexes = choices.getSelectedValuesList().toArray();
		Map<String, Integer> mappedIndexes = new HashMap<String, Integer>();
		int counter = 0;
		for (Object index : selectedIndexes) {
			mappedIndexes.put((String) index, counter);
			counter += 1;
		}

		int defaultRegex = 0;
		String chosenRegex;
		switch (defaultRegex) {
		case 1:
			chosenRegex = "(\\&?\\??t=\\d*?[h]?\\d*[m]?\\d*[s]|\\&t=\\d*)";
			break;
		case 2:
			chosenRegex = "(\\&list=[a-zA-Z 0-9 -]+\\&?\\index=?\\d+?)";
			break;
		case 3:
			chosenRegex = "(\\&?\\??t=\\d*?[h]?\\d*[m]?\\d*[s]|\\&t=\\d*|\\&list=[a-zA-Z 0-9 -]*)";
			break;
		default:
			chosenRegex = "\\?t.*|\\&t.*|\\&l.*|\\&f.*";
			break;
		}
		return chosenRegex;
	}

	public void modifyURL(String Regex, String convertedString) {
		Pattern p = Pattern.compile(Regex);
		Matcher m = p.matcher(convertedString); // get a matcher object
		convertedUrl = "";
		int count = 0;

		while (m.find()) {
			count++;
			if (count == 1) {
				convertedUrl = convertedString.substring(0, m.start()) + convertedString.substring(m.end());
				tfNew_URL.setText(convertedUrl);
			}
		}
	}

	public void removeTime() {

		Pattern p = Pattern.compile(REGEX_REMOVE_TIME);
		Matcher m = p.matcher(new_URL); // get a matcher object
		if (Pattern.matches(REGEX, new_URL)) {
			System.out.println("found a match");
		}
		convertedUrl = "";
		int count = 0;

		while (m.find()) {
			count++;
			if (count == 1) {
				convertedUrl = new_URL.substring(0, m.start()) + new_URL.substring(m.end() + 1);
				tfNew_URL.setText(convertedUrl);
			}
		}
	}

	public String convertString() {
		String str_to_convert = tfURL.getText();
		new_URL = str_to_convert.replace("watch?v=", "embed/");
		tfNew_URL.setText(new_URL);
		System.out.println(new_URL);
		return new_URL;
	}

	/**
	 * Get the String residing on the clipboard.
	 *
	 * @return any text found on the Clipboard; if none found, return an empty
	 *         String.
	 */
	public String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String) contents.getTransferData(DataFlavor.stringFlavor);
				tfURL.setText(result);
			} catch (UnsupportedFlavorException | IOException ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return result;
	}

	public void checkClipboardContents() {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		String is_youtube_link_l = "youtube.com"; // youtube url long version
		String is_youtube_link_s = "youtu.be";
		try {
			String paste = (String) c.getContents(null).getTransferData(DataFlavor.stringFlavor);
			if(this.isActive()) {
				if (paste.toLowerCase().contains(is_youtube_link_l.toLowerCase())
						|| paste.toLowerCase().contains(is_youtube_link_s.toLowerCase())) {
					System.out.println("Youtube url detected, placing it in the textbox");
					tfURL.setText(paste);
				} else {
					System.out.println("NON Youtube text detected, ignoring it");
				}
			} else {
				if (paste.toLowerCase().contains(is_youtube_link_l.toLowerCase())
						|| paste.toLowerCase().contains(is_youtube_link_s.toLowerCase())) {
					System.out.println("Youtube url detected, open the application window");
					trayIcon.displayMessage("Youtube!", "Youtube link has been detected!", TrayIcon.MessageType.INFO);
				} else {
					System.out.println("NON Youtube text detected, ignoring it minimized");
				}
				
			}
			

		} catch (IOException error) {
			System.out.println("Error" + error.getMessage());
		} catch (UnsupportedFlavorException flavorexcept) {
			System.out.println("Error" + flavorexcept.getMessage());
		}
	}
	
	/**
	 * Place a String on the clipboard, and make this class the owner of the
	 * Clipboard's contents.
	 */
	public void setClipboardContents(String aString) {
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub

	}

	/** ActionEvent handler - Called back upon button-click. */
	@Override
	public void actionPerformed(ActionEvent evt) {

		if (evt.getSource() == btnConvert) {
			// removeAll(convertString());
			modifyURL(returnRegex(), convertString());
			setClipboardContents(tfNew_URL.getText()); // add the converted url to the clipboard
			// Display the counter value on the TextField tfCount
			// tfNew_URL.setText(new_URL);
		} else if (evt.getSource() == btnAddRule) {
			tfield = new JTextField(50);
			tfield.setName(nameTField + count);
			count++;
			add(tfield);
			revalidate(); // For JDK 1.7 or above.
			repaint();
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		checkClipboardContents();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		System.exit(0); // Terminate the program
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		getClipboardContents();
	}

	static void displayAll(Collection<Character> col) {
		Iterator<Character> itr = col.iterator();
		while (itr.hasNext()) {
			String str = (String) itr.next().toString();
			System.out.print(str + " ");
		}
		System.out.println();
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		// TODO Auto-generated method stub

		//int key = e.getKeyCode();
		/*
		 * if (key == KeyEvent.VK_C && ke) { dx = -1; }
		 */
		// keysPressed.add(e.getKeyChar());
/*		char keyChar = e.getKeyChar();
        if (keyChar == '?') {
            System.out.println("You typed 'a'");
          }*/
		
		keysPressed.add(NativeKeyEvent.getKeyText(e.getKeyCode()));
		if (keysPressed.size() > 1) {
			// More than one key is currently pressed.
			// Iterate over pressed to get the keys.
			// displayAll(keysPressed);
			checkClipboardContents();
			Iterator<String> itr = keysPressed.iterator();
			while (itr.hasNext()) {
				String str = itr.next();
				System.out.print(str + " \n");
				System.out.print(keysPressed.size() + " pressed \n");
			}
		}

		System.out.println("Key Pressed (char): " + e.getKeyChar());
		System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

		if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		keysPressed.remove(NativeKeyEvent.getKeyText(e.getKeyCode()));
		System.out.print(keysPressed.size() + " released \n");
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Key Typed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}
}
