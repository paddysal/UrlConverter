import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
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
 * TIMESTAMPS https://youtu.be/xnER10j4ZBc?t=1m33s |
 * https://www.youtube.com/watch?v=xnER10j4ZBc&feature=youtu.be
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
	private JLabel lblOperationMode;
	private JLabel lblURL; // Declare component Label
	private JLabel lblNewURL; // Declare component Label
	private JLabel listInstructions;
	private JLabel listInfo;
	private JLabel urlInfo;
	private JLabel status;

	private JTextField tfURL; // Declare component TextField
	private JTextField tfNewURL; // Declare component TextField

	private JButton btnConvert; // Declare component Button
	private JButton btnSave;
	private JButton btnAddRule; // Declare button for adding new textboxes for rules

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
	private Boolean autoMode;
	private String conversionType;
	private JRadioButton auto;
	private JRadioButton manual;
	private ButtonGroup grpOperationMode;
	Box operationModeBox;
	TrayIcon trayIcon;
	SystemTray tray;

	// Set of currently pressed keys
	private final Set<String> keysPressed = new HashSet<String>();

	/** Logging */
	private static final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());

	UrlConverter() {
		super("SystemTray test");
		System.out.println("creating instance");

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		count = 0;
		nameTField = "tField";
		autoMode = true;

		auto = new JRadioButton("Auto");
		manual = new JRadioButton("Manual");

		operationModeBox = Box.createVerticalBox();
		grpOperationMode = new ButtonGroup();
		grpOperationMode.add(auto);
		grpOperationMode.add(manual);
		operationModeBox.add(auto);
		operationModeBox.add(manual);
		auto.setBackground(Color.DARK_GRAY);
		auto.setForeground(Color.WHITE);
		auto.setFont(new Font("Abel", Font.PLAIN, 16));
		manual.setBackground(Color.DARK_GRAY);
		manual.setForeground(Color.WHITE);
		manual.setFont(new Font("Abel", Font.PLAIN, 16));
		operationModeBox.setForeground(Color.WHITE);
		operationModeBox.setFont(new Font("Abel", Font.PLAIN, 16));
		operationModeBox.setBorder(BorderFactory.createTitledBorder("Mode"));

		// initialize textfields
		tfURL = new JTextField(60); // construct TextField
		tfNewURL = new JTextField(60); // construct TextField
		tfNewURL.setEditable(true); // set to read-only

		// initialize labels
		lblOperationMode = new JLabel(
				"<html>Manual operation mode disables automatic replacing of your cliboard contents. Automatic<br>"
						+ "mode auto converts all youtube urls detected including the ones detected in tray mode");
		lblOperationMode.setForeground(Color.WHITE);
		urlInfo = new JLabel(
				"This application checks your clipboard contents and fills in the textbox below whenever you copy a new youtube url.");
		urlInfo.setForeground(Color.WHITE);
		lblURL = new JLabel("URL"); // construct Label
		lblURL.setForeground(Color.WHITE);
		lblNewURL = new JLabel("New URL"); // construct Label
		lblNewURL.setForeground(Color.WHITE);
		listInstructions = new JLabel("<html>By default all four parts are removed from the youtube<br>"
				+ "url as otherwise full screen embeding will not work. Having said<br>"
				+ "that, feel free to change it by selecting only few options from<br>"
				+ "the list to the right. Ctrl click to select multiple options");
		listInstructions.setForeground(Color.WHITE);
		listInfo = new JLabel("Advanced settings, may give out unwanted results, proceed with caution");
		listInfo.setForeground(Color.WHITE);
		status = new JLabel("Application events will be displayed here");
		status.setForeground(Color.WHITE);

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
		btnConvert = new JButton("Convert"); // construct Button
		btnConvert.setForeground(Color.WHITE);
		btnConvert.setBackground(Color.DARK_GRAY);
		btnConvert.addActionListener(this); // Clicking Button (source object) fires an ActionEvent.
		btnAddRule = new JButton("Add Rule");
		btnAddRule.setForeground(Color.WHITE);
		btnAddRule.setBackground(Color.DARK_GRAY);
		btnAddRule.addActionListener(this);
		btnSave = new JButton("Add Rule");
		btnSave.setForeground(Color.WHITE);
		btnSave.setBackground(Color.DARK_GRAY);
		btnSave.addActionListener(this);

		lblOperationMode.setFont(new Font("Abel", Font.PLAIN, 16));
		urlInfo.setFont(new Font("Abel", Font.PLAIN, 16));
		lblURL.setFont(new Font("Abel", Font.PLAIN, 16));
		lblNewURL.setFont(new Font("Abel", Font.PLAIN, 16));
		listInstructions.setFont(new Font("Abel", Font.PLAIN, 16));
		listInfo.setFont(new Font("Abel", Font.PLAIN, 16));
		status.setFont(new Font("Abel", Font.PLAIN, 16));
		btnConvert.setFont(new Font("Abel", Font.PLAIN, 16));
		btnAddRule.setFont(new Font("Abel", Font.PLAIN, 16));

		// listed based on the row number

		addItem(panel, urlInfo, 0, 0, 4, 1, GridBagConstraints.SOUTH);

		addItem(panel, lblURL, 0, 1, 1, 1, GridBagConstraints.EAST);
		addItem(panel, tfURL, 1, 1, 1, 1, GridBagConstraints.CENTER);
		addItem(panel, btnConvert, 2, 1, 1, 1, GridBagConstraints.WEST);

		addItem(panel, lblNewURL, 0, 2, 1, 1, GridBagConstraints.EAST);
		addItem(panel, tfNewURL, 1, 2, 1, 1, GridBagConstraints.CENTER);
		addItem(panel, btnAddRule, 2, 2, 1, 1, GridBagConstraints.WEST);

		addItem(panel, listInstructions, 1, 3, 1, 1, GridBagConstraints.WEST);
		addItem(panel, listScroller, 1, 3, 1, 1, GridBagConstraints.EAST);

		addItem(panel, lblOperationMode, 1, 4, 1, 1, GridBagConstraints.WEST);
		addItem(panel, operationModeBox, 1, 4, 1, 1, GridBagConstraints.EAST);

		addItem(panel, status, 1, 5, 4, 1, GridBagConstraints.WEST);

		/*
		 * c.gridy = 0; // row 0
		 * 
		 * c.insets = new Insets(20, 0, 0, 0); c.gridx = 0; panel.add(operationModeBox,
		 * c);
		 * 
		 * c.gridy += 1;
		 * 
		 * c.insets = new Insets(10, 0, 0, 0); c.gridx = 0; c.gridwidth = 6;
		 * panel.add(urlInfo, c);
		 * 
		 * c.gridy += 1;
		 * 
		 * c.insets = new Insets(20, 0, 20, 0); // top padding c.gridx = 0; c.gridwidth
		 * = 1; panel.add(lblURL, c);
		 * 
		 * c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.gridwidth = 2;
		 * panel.add(tfURL, c);
		 * 
		 * c.gridx = 3; c.insets = new Insets(20, 10, 20, 10); // top padding c.fill =
		 * GridBagConstraints.NONE; c.gridwidth = 1; panel.add(btnConvert, c);
		 * 
		 * c.gridy += 1;
		 * 
		 * c.insets = new Insets(0, 0, 20, 0); // top padding c.gridx = 0;
		 * panel.add(lblNewURL, c);
		 * 
		 * c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; panel.add(tfNewURL, c);
		 * 
		 * c.gridy += 1;
		 * 
		 * c.ipady = 5; c.gridx = 0; c.fill = GridBagConstraints.NONE;
		 * panel.add(btnAddRule, c);
		 * 
		 * c.gridy += 1;
		 * 
		 * c.insets = new Insets(0, 0, 0, 0); // top padding c.ipady = 0; c.gridx = 1;
		 * panel.add(listInfo, c);
		 * 
		 * c.gridy += 1;
		 * 
		 * c.gridx = 1; panel.add(listInstructions, c);
		 * 
		 * c.gridy += 1;
		 * 
		 * c.gridx = 1; panel.add(listScroller, c);
		 * 
		 * c.gridy += 1;
		 * 
		 * c.gridx = 0; c.gridwidth = 6; panel.add(status, c);
		 */

		// setLayout(new FlowLayout());
		// set the layout of the frame to FlowLayout, which arranges
		// the components from left-to-right, and flow to next row from top-to-bottom.
		// add(urlInfo);
		// add(lblURL); // add label to the frame
		// add(tfURL); // Frame adds TextField
		// add(lblNew_URL); // Frame adds Label
		// add(tfNew_URL); // Frame adds TextField
		// add(btnConvert); // Frame adds Button
		// add(btnAddRule); // Add Rule button to the frame
		// add(listInfo);
		// add(listInstructions);
		// add(listScroller);
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

		panel.setBackground(Color.DARK_GRAY);
		// getContentPane().setBackground(Color.DARK_GRAY);
		getContentPane().add(panel);
		/*
		 * Do not use setSize() of JFrame. This will cause abnormal behaviour. Instead,
		 * let the frame size itself according to the size of its components. If you
		 * want the frame to be bigger, adjust not the size of the frame but the
		 * components inside it. You can either setpreferredSize or override the
		 * getpreferredsize of the component if you really want to adjust is size since
		 * GridBagLayout is one of those layout managers that respects the preferredSize
		 * of the component. Use pack() to remove the unnecessary space.
		 */
		pack();
		// setSize(800, 500); // "super" Frame sets its initial window size
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true); // "super" Frame shows
		fetchHistory();
	}

	private void addItem(JPanel p, JComponent c, int x, int y, int width, int height, int align) {
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = x;
		gc.gridy = y;
		gc.gridwidth = width;
		gc.gridheight = height;
		gc.weightx = 100.0;
		gc.weighty = 100.0;
		gc.insets = new Insets(5, 5, 5, 5);
		gc.anchor = align;
		gc.fill = GridBagConstraints.NONE;
		p.add(c, gc);
	}

	public void fetchHistory() {
		Scanner s;
		int count = 0;
		ArrayList<String> list = new ArrayList<String>();
		try {
			s = new Scanner(new File("History.txt"));
			while (s.hasNext()){
			    list.add(s.next());
			    count += 1;
			}
			System.out.println("Number of converted urls fetched: " + count);
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
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
		});
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
				tfNewURL.setText(convertedUrl);
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
				tfNewURL.setText(convertedUrl);
				appendHistoryLog(convertedUrl);
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
				tfNewURL.setText(convertedUrl);
			}
		}
	}

	public void appendHistoryLog(String convertedURL) {
		String url_to_save = convertedURL + "\n";
		try {
			Files.write(Paths.get("history.txt"), url_to_save.getBytes());
		} catch (IOException e) {
			// tfNewURL
			e.printStackTrace();
		}
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

	public String convertString() {
		String str_to_convert = tfURL.getText();
		new_URL = str_to_convert.replace("watch?v=", "embed/");
		tfNewURL.setText(new_URL);
		System.out.println(new_URL);
		return new_URL;
	}

	public void checkClipboardContents() {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		String is_youtube_link_l = "youtube.com"; // youtube url long version
		String is_youtube_link_s = "youtu.be";
		try {
			String paste = (String) c.getContents(null).getTransferData(DataFlavor.stringFlavor);
			if (paste.toLowerCase().contains(is_youtube_link_s.toLowerCase())) {

			}
			if (this.isActive()) {
				if (paste.toLowerCase().contains(is_youtube_link_l.toLowerCase())
						|| paste.toLowerCase().contains(is_youtube_link_s.toLowerCase())) {
					System.out.println("Youtube url detected, placing it in the textbox");
					status.setText("Youtube url detected, placing it in the textbox");
					tfURL.setText(paste);
				} else {
					status.setText("NON Youtube text detected in the clipboard, ignoring it");
					System.out.println("NON Youtube text detected in the clipboard, ignoring it");
				}
			} else {
				if (paste.toLowerCase().contains(is_youtube_link_l.toLowerCase())
						|| paste.toLowerCase().contains(is_youtube_link_s.toLowerCase())) {
					System.out.println("Youtube url while minimized detected, open the application window");
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
			setClipboardContents(tfNewURL.getText());// add the converted url to the clipboard
			status.setText("Converted link has been placed in your clipboard");
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
		// checkClipboardContents();
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

		// int key = e.getKeyCode();
		/*
		 * if (key == KeyEvent.VK_C && ke) { dx = -1; }
		 */
		// keysPressed.add(e.getKeyChar());
		/*
		 * char keyChar = e.getKeyChar(); if (keyChar == '?') {
		 * System.out.println("You typed 'a'"); }
		 */

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
