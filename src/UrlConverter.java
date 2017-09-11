import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import resources.ResourceLoader;

// TODO: Auto-generated Javadoc
/**
 * The Class UrlConverter.
 *
 * @author Patryk Salek
 * @version 1.0.0.0
 * @category Utilities
 */

/*
 * PLAYLISTS https://www.youtube.com/watch?v=c7nRTF2SowQ
 * https://www.youtube.com/watch?v=acXXyruMtaY&list=PLs1-
 * UdHIwbo5AZVWE4IKJuADxWpz7e-tw
 * https://www.youtube.com/watch?v=Aj0QhLjgxfU&list=PLs1-
 * UdHIwbo5AZVWE4IKJuADxWpz7e-tw&index=2
 */

/*
 * TIMESTAMPS https://youtu.be/xnER10j4ZBc?t=1m33s
 * https://www.youtube.com/watch?v=xnER10j4ZBc&feature=youtu.be
 * https://www.youtube.com/watch?v=xnER10j4ZBc&t=215
 * https://youtu.be/dT9eI40RNoQ?t=1h51m41s
 * 
 */

/*
 * Adding library to listen to global events
 * https://github.com/kwhat/jnativehook/releases
 */

public class UrlConverter extends JFrame implements ClipboardOwner, ActionListener, WindowListener, NativeKeyListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The operation mode JLabel component. */
	private JLabel lblOperationMode;

	/** The add rule explanation JLabel component. */
	private JLabel lblAddRuleExplanation;

	/** The URL JLabel component. */
	private JLabel lblURL;

	/** The new URL JLabel component. */
	private JLabel lblNewURL;

	/** The list instructions JLabel component. */
	private JLabel lblListInstructions;

	/** The list information JLabel component. */
	private JLabel lblListInformation;

	/** JLabel component displayed at the top of the application window. */
	private JLabel lblUrlInformation;

	/** JLabel component used for displaying status of some application events. */
	private JLabel lblStatus;

	/**
	 * JLabel component used for displaying explanation for the JList with stored
	 * URLs.
	 */
	private JLabel lblSavedExplanation;

	/** The REGEX rule name JLabel component. */
	private JLabel lblRegexRuleName;

	/** The REGEX rule JLabel component. */
	private JLabel lblRegexRule;

	/** JTextField component used to display YouTube URL before conversion. */
	private JTextField tfURL;

	/** JTextField component used to display the YouTube URL after conversion */
	private JTextField tfNewURL;

	/** JTextField component used to capture new REGEX pattern name. */
	private JTextField tfNewRuleName;

	/** JTextField component used to capture new REGEX pattern. */
	private JTextField tfNewRule;

	/**
	 * JButton component used for initializing URL conversion process while the
	 * application window is in the active mode.
	 */
	private JButton btnConvert;

	/** JButton component used for calling the save converted URL event. */
	private JButton btnSave;

	/**
	 * JButton component used for opening JOptionPane in which new REGEX pattern
	 * rule details are collected and saved in a regex.txt text file.
	 */
	private JButton btnAddRule;

	/**
	 * JButton component used for opening selected URL within the savedURLs JList.
	 */
	private JButton btnOpenUrl;

	/**
	 * JButton component used for removing unwanted URLs from the saved URL's text
	 * file.
	 */
	private JButton btnRemoveUrl;

	/** Variable used to store the value of the new URL in one of the methods. */
	private String new_URL;

	/** Variable used to store the value of the converted URL. */
	private String convertedUrl;

	/**
	 * An Array of Strings containing names for default REGEX patterns for the
	 * application.
	 */
	private String[] data = { "Remove Everything", "Remove Time Stamp", "Remove Playlist", "Remove Feature" };

	/** JList used to store REGEX Pattern choices. */
	private JList<String> regexChoices;

	/** JList used to store saved URL's. */
	private JList<String> savedURLs;

	/** ListScroller for the list of available regex's. */
	private JScrollPane regexChoicesScroller;

	/** ListScroller for the saved URL's list. */
	private JScrollPane savedURLsScroller;

	/** The auto mode JRadioButton. */
	private JRadioButton auto;

	/** The manual mode JRadioButton. */
	private JRadioButton manual;

	/** Main panel used to store all of the other components. */
	private JPanel panel;

	/** Additional panel used to store buttons for managing saved YouTube URLs. */
	private JPanel panel1;

	/**
	 * Variable used for processing the detected YouTube URLs based on its value. By
	 * default it's value is set to 0.
	 */
	private int selectedRegex;

	/**
	 * Variable for storing state of operation for the application. By default set
	 * to false.
	 */
	private Boolean autoMode;

	/** The ButtonGroup to store operation mode JRadioBoxes. */
	private ButtonGroup grpOperationMode;

	/**
	 * The operation mode box used to store automatic and manual radio buttons via
	 * GroupBox above.
	 */
	Box operationModeBox;

	/** TrayIcon used to store the icon for the application. */
	TrayIcon trayIcon;

	/**
	 * Tray used for allocating the application to the system tray while in
	 * minimised mode.
	 */
	SystemTray tray;

	/** ImageIcon for the OFF state for either of the operation modes. */
	ImageIcon rBtnIconOff;

	/** ImageIcon for the ON state for either of the operation modes. */
	ImageIcon rBtnIconOn;

	/** The saved list model. */
	DefaultListModel<String> savedListModel;

	/** The rule inputs JComponent collection. */
	final JComponent[] ruleInputs;

	/** The keys pressed variable used to store set of currently pressed keys. */
	private final Set<String> keysPressed = new HashSet<String>();

	/** Logging. */
	private static final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());

	/**
	 * Instantiates a new URL converter.
	 */
	UrlConverter() {
		super("SystemTray test");
		System.out.println("creating instance");

		// Initialise Panels
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel1 = new JPanel();
		panel1.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		autoMode = false;

		// change the background colors of all panels and optionpanes
		UIManager.put("OptionPane.background", Color.DARK_GRAY);
		UIManager.put("Panel.background", Color.DARK_GRAY);

		// Initialise ListModel
		savedListModel = new DefaultListModel<String>();

		// Initialise RadioButtons
		auto = new JRadioButton("Automatic");
		manual = new JRadioButton("Manual");

		// Initialise ImageIcons

		// rBtnIconOff = new ImageIcon("off_state.png");
		Image iconOn = ResourceLoader.getImage("on_state.png");
		Image iconOff = ResourceLoader.getImage("off_state.png");
		rBtnIconOff = new ImageIcon(iconOff);
		rBtnIconOn = new ImageIcon(iconOn);

		// Initialise GroupBox with radio buttons
		operationModeBox = Box.createVerticalBox();
		grpOperationMode = new ButtonGroup();
		grpOperationMode.add(auto);
		grpOperationMode.add(manual);
		operationModeBox.add(auto);
		operationModeBox.add(manual);
		auto.setIcon(rBtnIconOff);
		auto.setBackground(Color.DARK_GRAY);
		auto.setForeground(Color.WHITE);
		auto.setFont(new Font("Abel", Font.PLAIN, 16));
		auto.addActionListener(this);
		manual.setIcon(rBtnIconOn);
		manual.setBackground(Color.DARK_GRAY);
		manual.setForeground(Color.WHITE);
		manual.setSelected(true);
		manual.addActionListener(this);
		manual.setFont(new Font("Abel", Font.PLAIN, 16));
		operationModeBox.setForeground(Color.WHITE);
		operationModeBox.setFont(new Font("Abel", Font.PLAIN, 16));
		operationModeBox.setBorder(BorderFactory.createTitledBorder("Mode"));

		// Initialise JTestFields
		tfURL = new JTextField(60); // construct TextField
		tfNewURL = new JTextField(60); // construct TextField
		tfNewURL.setEditable(true); // set to read-only
		tfNewRuleName = new JTextField();
		tfNewRule = new JTextField();

		// Initialise JLabels
		lblOperationMode = new JLabel(
				"<html>Manual operation mode disables automatic replacing of your cliboard contents. Automatic<br>"
						+ "mode auto converts all youtube urls detected including the ones detected in tray mode");
		lblOperationMode.setForeground(Color.WHITE);
		lblAddRuleExplanation = new JLabel(
				"<html>Pressing the Add Rule button will create two textboxes and a button. <br>"
						+ "You are supposed to provide new rule name and it's corresponding regex pattern. ");
		lblAddRuleExplanation.setForeground(Color.WHITE);
		lblUrlInformation = new JLabel(
				"This application checks your clipboard contents and fills in the textbox below whenever you copy a new youtube url.");
		lblUrlInformation.setForeground(Color.WHITE);
		lblURL = new JLabel("URL"); // construct Label
		lblURL.setForeground(Color.WHITE);
		lblNewURL = new JLabel("New URL"); // construct Label
		lblNewURL.setForeground(Color.WHITE);
		lblListInstructions = new JLabel("<html>By default all three parts are removed from <br>"
				+ "the youtube url as otherwise full screen embeding would not work. <br>"
				+ "Having said that, feel free to change it by selecting any other <br>"
				+ "option from the list to the right.");
		lblListInstructions.setForeground(Color.WHITE);
		lblListInformation = new JLabel("Advanced settings, may give out unwanted results, proceed with caution");
		lblListInformation.setForeground(Color.WHITE);
		lblSavedExplanation = new JLabel("<html> <strong>Your Saved URLs</strong> <br>" + "Select the link you wish<br>"
				+ "to open and then click <br>" + "the Open button to view <br>" + "it inside your browser");
		lblSavedExplanation.setForeground(Color.WHITE);
		lblRegexRuleName = new JLabel("Rule Name: ");
		lblRegexRuleName.setForeground(Color.WHITE);
		lblRegexRule = new JLabel("Rule Pattern: ");
		lblRegexRule.setForeground(Color.WHITE);
		lblStatus = new JLabel("Application events will be displayed here");
		lblStatus.setForeground(Color.YELLOW);

		// Initialise an array of JComponents used in the JOptionPane
		ruleInputs = new JComponent[] { lblRegexRuleName, tfNewRuleName, lblRegexRule, tfNewRule };

		// Initialise JLists and their corresponding scrollers
		regexChoices = new JList<String>(data);
		// regexChoices.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		regexChoices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		regexChoices.setLayoutOrientation(JList.VERTICAL);
		regexChoices.setVisibleRowCount(-1);

		System.out.println(fetchSaved().size());
		String[] arr = new String[fetchSaved().size()];
		populateArrayFromList(arr, fetchSaved());
		savedURLs = new JList<String>(arr);
		savedURLs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		savedURLs.setLayoutOrientation(JList.VERTICAL);
		savedURLs.setVisibleRowCount(-1);
		savedURLs.setModel(savedListModel);

		/*
		 * int start = 0; int regexChoicesEnd = regexChoices.getModel().getSize() - 1;
		 * if (regexChoicesEnd >= 0) { regexChoices.setSelectionInterval(start,
		 * regexChoicesEnd); }
		 */

		selectedRegex = 0;
		regexChoices.setSelectedIndex(selectedRegex);
		regexChoices.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					selectedRegex = regexChoices.getSelectedIndex();
					System.out.println("Selected regex" + regexChoices.getSelectedIndex() + " | "
							+ regexChoices.getSelectedValue().toString());
				}
			}
		});

		regexChoicesScroller = new JScrollPane(regexChoices);
		regexChoicesScroller.setPreferredSize(new Dimension(250, 80));

		savedURLsScroller = new JScrollPane(savedURLs);
		savedURLsScroller.setPreferredSize(new Dimension(325, 120));

		// initialize buttons
		btnConvert = new JButton("Convert"); // construct Button
		btnConvert.setForeground(Color.WHITE);
		btnConvert.setBackground(Color.DARK_GRAY);
		btnConvert.addActionListener(this); // Clicking Button (source object) fires an ActionEvent.
		btnAddRule = new JButton("Add Rule");
		btnAddRule.setForeground(Color.WHITE);
		btnAddRule.setBackground(Color.DARK_GRAY);
		btnAddRule.addActionListener(this);
		btnSave = new JButton("Save");
		btnSave.setForeground(Color.WHITE);
		btnSave.setBackground(Color.DARK_GRAY);
		btnSave.addActionListener(this);
		btnOpenUrl = new JButton("Open Selected Url");
		btnOpenUrl.setForeground(Color.WHITE);
		btnOpenUrl.setBackground(Color.DARK_GRAY);
		btnOpenUrl.addActionListener(this);
		btnRemoveUrl = new JButton("Remove Selected Url");
		btnRemoveUrl.setForeground(Color.WHITE);
		btnRemoveUrl.setBackground(Color.DARK_GRAY);
		btnRemoveUrl.addActionListener(this);

		/*
		 * btnSaveRule.setForeground(Color.WHITE);
		 * btnSaveRule.setBackground(Color.DARK_GRAY);
		 * btnSaveRule.addActionListener(this);
		 */

		lblOperationMode.setFont(new Font("Abel", Font.PLAIN, 16));
		lblAddRuleExplanation.setFont(new Font("Abel", Font.PLAIN, 16));
		lblUrlInformation.setFont(new Font("Abel", Font.PLAIN, 16));
		lblURL.setFont(new Font("Abel", Font.PLAIN, 16));
		lblNewURL.setFont(new Font("Abel", Font.PLAIN, 16));
		lblListInstructions.setFont(new Font("Abel", Font.PLAIN, 16));
		lblListInformation.setFont(new Font("Abel", Font.PLAIN, 16));
		lblSavedExplanation.setFont(new Font("Abel", Font.PLAIN, 16));
		lblStatus.setFont(new Font("Abel", Font.PLAIN, 16));
		lblRegexRuleName.setFont(new Font("Abel", Font.PLAIN, 16));
		lblRegexRule.setFont(new Font("Abel", Font.PLAIN, 16));
		btnConvert.setFont(new Font("Abel", Font.PLAIN, 16));
		btnAddRule.setFont(new Font("Abel", Font.PLAIN, 16));
		btnSave.setFont(new Font("Abel", Font.PLAIN, 16));
		btnOpenUrl.setFont(new Font("Abel", Font.PLAIN, 16));
		btnRemoveUrl.setFont(new Font("Abel", Font.PLAIN, 16));
		regexChoices.setFont(new Font("Abel", Font.PLAIN, 13));
		savedURLs.setFont(new Font("Abel", Font.PLAIN, 13));
		// btnSaveRule.setFont(new Font("Abel", Font.PLAIN, 16));

		// listed based on the row number

		addItem(panel, lblUrlInformation, 0, 0, 4, 1, GridBagConstraints.SOUTH);

		addItem(panel, lblURL, 0, 1, 1, 1, GridBagConstraints.EAST);
		addItem(panel, tfURL, 1, 1, 1, 1, GridBagConstraints.CENTER);
		addItem(panel, btnConvert, 2, 1, 1, 1, GridBagConstraints.WEST);

		addItem(panel, lblNewURL, 0, 2, 1, 1, GridBagConstraints.EAST);
		addItem(panel, tfNewURL, 1, 2, 1, 1, GridBagConstraints.CENTER);
		addItem(panel, btnSave, 2, 2, 1, 1, GridBagConstraints.WEST);

		addItem(panel, lblListInstructions, 1, 3, 1, 1, GridBagConstraints.WEST);
		addItem(panel, regexChoicesScroller, 1, 3, 1, 1, GridBagConstraints.EAST);

		addItem(panel, lblOperationMode, 1, 4, 1, 1, GridBagConstraints.WEST);
		addItem(panel, operationModeBox, 1, 4, 1, 1, GridBagConstraints.EAST);

		addItem(panel, lblAddRuleExplanation, 1, 5, 1, 1, GridBagConstraints.WEST);
		addItem(panel, btnAddRule, 1, 5, 1, 1, GridBagConstraints.EAST);

		addItem(panel, lblSavedExplanation, 1, 6, 1, 1, GridBagConstraints.WEST);
		addItem(panel, savedURLsScroller, 1, 6, 1, 1, GridBagConstraints.CENTER);
		addItem(panel1, btnOpenUrl, 0, 0, 1, 1, GridBagConstraints.SOUTH);
		addItem(panel1, btnRemoveUrl, 0, 1, 1, 1, GridBagConstraints.NORTH);
		addItem(panel, panel1, 1, 6, 1, 1, GridBagConstraints.EAST);

		addItem(panel, lblStatus, 1, 7, 4, 1, GridBagConstraints.WEST);
		addWindowListener(this);

		// Disable parent logger and set the desired level.
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.ALL);

		// Add our custom formatter to a console handler.
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.WARNING);
		logger.addHandler(handler);

		/** Try and set the look and feel of the application */
		try {
			System.out.println("setting look and feel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Unable to set LookAndFeel");
		}
		if (SystemTray.isSupported()) {
			System.out.println("system tray supported");
			tray = SystemTray.getSystemTray();

			// Image image =
			// Toolkit.getDefaultToolkit().getImage("/resources/images/Pinwheel-48.png");
			Image image = ResourceLoader.getImage("Pinwheel-48.png");
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
		/** Frame adds "this" object as a WindowStateEvent listener. */
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

		/** Set the icon for the application */
		// setIconImage(Toolkit.getDefaultToolkit().getImage("/resources/images/Pinwheel-48.png"));
		setIconImage(ResourceLoader.getImage("Pinwheel-48.png"));
		/** "super" Frame sets its title */
		setTitle("YouTube URL Converter");

		panel.setBackground(Color.DARK_GRAY);
		panel1.setBackground(Color.DARK_GRAY);
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

	/**
	 * Adds the item to the panel specified.
	 *
	 * @param p
	 *            the p
	 * @param c
	 *            the c
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param align
	 *            the align
	 */
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

	/**
	 * Populate array from list.
	 *
	 * @param <T>
	 *            the generic type
	 * @param arr
	 *            the arr
	 * @param arrayList
	 *            the array list
	 */
	private <T> void populateArrayFromList(T[] arr, ArrayList<T> arrayList) {
		System.out.println("Array size " + arr.length);
		System.out.println("ArrayList size " + arrayList.size());
		for (int i = 0; i < arrayList.size(); i++) {
			savedListModel.addElement(arrayList.get(i).toString());
			arr[i] = arrayList.get(i);
		}
	}

	/**
	 * Fetch saved URLs from a text file.
	 *
	 * @return the array list
	 */
	private ArrayList<String> fetchSaved() {
		String path = System.getProperty("user.home") + File.separator + "Documents";
		path += File.separator + "UrlConverter";
		File customDir = new File(path);
		
		File file = new File(customDir, "saved.txt");
		if (customDir.exists() || customDir.mkdirs()) {
			// Path either exists or was created
			
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		Scanner s;
		int count = 0;
		ArrayList<String> savedList = new ArrayList<String>();
		try {
			s = new Scanner(file);
			while (s.hasNext()) {
				savedList.add(s.next());
				count += 1;
			}
			System.out.println("Number of converted urls fetched: " + count);
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return savedList;
	}

	/**
	 * Fetch history of converted URLs stored in a text file.
	 *
	 * @return the array list
	 */
	private ArrayList<String> fetchHistory() {
		String path = System.getProperty("user.home") + File.separator + "Documents";
		path += File.separator + "UrlConverter";
		File customDir = new File(path);
		
		File file = new File(customDir, "history.txt");
		if (customDir.exists() || customDir.mkdirs()) {
			// Path either exists or was created
			
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		Scanner s;
		int count = 0;
		ArrayList<String> historyList = new ArrayList<String>();
		try {
			s = new Scanner(file);
			while (s.hasNext()) {
				historyList.add(s.next());
				count += 1;
			}
			System.out.println("Number of converted urls fetched: " + count);
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return historyList;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
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

	/**
	 * Return REGEX method used to for choosing REGEX pattern to be used for URL
	 * conversion.
	 *
	 * @return the string
	 */
	public String returnRegex() {
		String chosenRegex;
		System.out.println("selectedRegex value is: " + selectedRegex);
		switch (selectedRegex) {
		case 1:
			chosenRegex = "(\\&?\\??t=\\d*?[h]?\\d*[m]?\\d*[s]|\\&t=\\d*)";
			break;
		case 2:
			chosenRegex = "(\\&list=[a-zA-Z 0-9 -]+\\&?index=?\\d+?)";
			break;
		case 3:
			chosenRegex = "(\\&feature=youtu.be)";
			break;
		default:
			chosenRegex = "\\?t.*|\\&t.*|\\&l.*|\\&f.*"; // alternative:
															// (\\&?\\??t=\\d*?[h]?\\d*[m]?\\d*[s]|\\&t=\\d*|\\&list=[a-zA-Z
															// 0-9 -]*)
			break;
		}
		System.out.println(chosenRegex);
		return chosenRegex;
	}

	/**
	 * Modify URL method for converting captured YouTube URLs.
	 *
	 * @param Regex
	 *            the regex
	 * @param convertedString
	 *            the converted string
	 * @return the string
	 */
	public String modifyURL(String Regex, String convertedString) {
		Pattern p = Pattern.compile(Regex);
		Matcher m = p.matcher(convertedString); // get a matcher object
		convertedUrl = "";
		int count = 0;

		while (m.find()) {
			count++;
			if (count == 1) {
				convertedUrl = convertedString.substring(0, m.start()) + convertedString.substring(m.end());
				tfNewURL.setText(convertedUrl);
				fileWriter(convertedUrl, "history.txt");
				//appendHistoryLog(convertedUrl);
			} else {
				lblStatus.setText("Can't remove as the requested tag was not found in the url");
			}
		}
		return convertedUrl;
	}

	/**
	 * Append saved method used for storing the specified URL in a text file.
	 *
	 * @param convertedURL
	 *            the converted URL
	 */
	public void appendSaved(String convertedURL) {

		String path = System.getProperty("user.home") + File.separator + "Documents";
		path += File.separator + "UrlConverter";
		File customDir = new File(path);
		
		//InputStream file = ResourceLoader.getFile("saved.txt");
		File file = new File(customDir, "saved.txt");
		Scanner scanner;
		int lineNum = 0;
		try {
			scanner = new Scanner(file);
			if (customDir.exists() || customDir.mkdirs()) {
				// Path either exists or was created
				
				try {
					file.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (!scanner.hasNextLine()) {
				try (FileWriter fw = new FileWriter(file, true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw)) {
					lblStatus.setText("Blank file, adding your first link");
					System.out.println("inside of negative");
					out.println(convertedURL);
				} catch (IOException e) {
					lblStatus.setText(e.toString());
				}
			} else {
				while (scanner.hasNextLine()) {
					System.out.println("inside of while loop");
					String line = scanner.nextLine();
					if (line.length() > 1) {
						lineNum++;
					}
					System.out.println("in" + lineNum);
					System.out.println("line" + line.toString());
					System.out.println("converted" + convertedURL);
					System.out.println("line length" + line.length());
					System.out.println("converted length" + convertedURL.length());
					if (line.equals(convertedURL)) {
						System.out.println("inside of if");
						lblStatus.setText("Link already saved at line: " + lineNum);
						break;
					} else if (!scanner.hasNextLine()) {
						try (FileWriter fw = new FileWriter(file, true);
								BufferedWriter bw = new BufferedWriter(fw);
								PrintWriter out = new PrintWriter(bw)) {
							System.out.println("inside of else");
							System.out.println(ResourceLoader.getFile("saved.txt").toString());
							out.println(convertedURL);
							lblStatus.setText("Url successfully saved!");
							savedListModel.addElement(convertedURL);
						} catch (IOException e) {
							lblStatus.setText(e.toString());
						}
					}
				}
			}
			} else {
				// The path could not be created for some reason
			}
			scanner.close();
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	/**
	 * Removes the specified saved URL from the text file.
	 *
	 * @param urlToRemove
	 *            the url to remove
	 */
	public void removeSaved(String urlToRemove) {
		
		String path = System.getProperty("user.home") + File.separator + "Documents";
		path += File.separator + "UrlConverter";
		File customDir = new File(path);
		
		//InputStream file = ResourceLoader.getFile("saved.txt");
		File file = new File(customDir, "saved.txt");
		if (!customDir.exists()) {}
		
		File temp = null;
		try {
			temp = File.createTempFile(path, "file.txt", file.getParentFile());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String charset = "UTF-8";
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));

			for (String line; (line = reader.readLine()) != null;) {
				line = line.replace(urlToRemove, "");
				// String adjusted = line.replaceAll("(?m)^[ \t]*\r?\n", "");
				// writer.println(line);
				if (!line.isEmpty()) {
					writer.println(line);
					// writer.write("\n");
				}
			}
		} catch (UnsupportedEncodingException | FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.close();
			savedListModel.removeElement(urlToRemove);
		}

		file.delete();
		temp.renameTo(file);
	}

	/**
	 * Append history log with the successfully converted URL.
	 *
	 * @param convertedURL
	 *            the converted URL
	 */
	public void appendHistoryLog(String convertedURL) {
		String url_to_save = convertedURL + "\n";

		String path = System.getProperty("user.home") + File.separator + "Documents";
		path += File.separator + "UrlConverter";
		File customDir = new File(path);
		if (customDir.exists() || customDir.mkdirs()) {
			// Path either exists or was created
			File file = new File(customDir, "history.txt");
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try (FileWriter fw = new FileWriter(file, true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.println(url_to_save);
			} catch (IOException e) {
				lblStatus.setText(e.toString());
			}
		} else {
			// The path could not be created for some reason
		}
		/*
		 * System.out.println("appendHistory: " + url_to_save); PrintWriter writer =
		 * ResourceLoader.getFileForWrite("history.txt"); writer.println(url_to_save);
		 * lblStatus.setText("Url successfully saved!");
		 */

		/*
		 * try { Files.write(Paths.get("history.txt"), url_to_save.getBytes(),
		 * StandardOpenOption.APPEND); } catch (IOException e) { // tfNewURL
		 * e.printStackTrace(); }
		 */
	}

	/**
	 * Save rule to file.
	 *
	 * @param ruleName
	 *            the rule name
	 * @param ruleContents
	 *            the rule contents
	 */

	public void fileWriter(String valueToWrite, String fileName) {
		String path = System.getProperty("user.home") + File.separator + "Documents";
		path += File.separator + "UrlConverter";
		File customDir = new File(path);
		if (customDir.exists() || customDir.mkdirs()) {
			// Path either exists or was created
			File file = new File(customDir, fileName);
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try (FileWriter fw = new FileWriter(file, true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.println(valueToWrite);
				if (fileName == "saved.txt") {
					lblStatus.setText("Url saved successfully saved!");
				} else if (fileName == "history.txt") {
					System.out.println("Url added to history");
				} else if (fileName == "regex.txt") {
					lblStatus.setText("Rule successfully saved!");
				} else {
					// shouldn't get here
					lblStatus.setText("Something went wrong while saving");
				}

			} catch (IOException e) {
				lblStatus.setText(e.toString());
			}
		} else {
			// The path could not be created for some reason
		}
	}

	public void saveRuleToFile(String ruleName, String ruleContents) {
		String ruleToSave = ruleName + "," + ruleContents + "\n";

		String path = System.getProperty("user.home") + File.separator + "Documents";
		path += File.separator + "UrlConverter";
		File customDir = new File(path);
		if (customDir.exists() || customDir.mkdirs()) {
			// Path either exists or was created
			File file = new File(customDir, "regex.txt");
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try (FileWriter fw = new FileWriter(file, true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.println(ruleToSave);
				lblStatus.setText("Rule successfully saved!");
			} catch (IOException e) {
				lblStatus.setText(e.toString());
			}
		} else {
			// The path could not be created for some reason
		}

		/*
		 * try (FileWriter fw = new FileWriter(
		 * UrlConverter.class.getClassLoader().getResource(
		 * "/resources/textfiles/regex.txt").toString(), true); BufferedWriter bw = new
		 * BufferedWriter(fw); PrintWriter out = new PrintWriter(bw)) {
		 * out.println(ruleToSave);
		 * 
		 * } catch (IOException e) { lblStatus.setText(e.toString()); }
		 */
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

	/**
	 * Convert parts of the URL that are always the same to some other that is
	 * required.
	 *
	 * @return the string
	 */
	public String convertString() {
		String urlToConvert = tfURL.getText();
		String is_youtube_link_s = "youtu.be";
		if (urlToConvert.contains("watch?v=")) {
			new_URL = urlToConvert.replace("watch?v=", "embed/");
		} else if (urlToConvert.contains(is_youtube_link_s)) {
			new_URL = urlToConvert.replace("youtu.be", "youtube.com/embed");
		}
		tfNewURL.setText(new_URL);
		System.out.println(new_URL);
		return new_URL;
	}

	/**
	 * Check clipboard contents while minimised and maximised.
	 */
	public void checkClipboardContents() {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		String is_youtube_link_l = "youtube.com"; // youtube url long version
		String is_youtube_link_s = "youtu.be";
		try {
			String paste = (String) c.getContents(null).getTransferData(DataFlavor.stringFlavor);
			if (this.isActive()) {
				if (paste.toLowerCase().contains(is_youtube_link_l.toLowerCase())
						|| paste.toLowerCase().contains(is_youtube_link_s.toLowerCase())) {
					System.out.println("Youtube url detected, placing it in the textbox");
					lblStatus.setText("Youtube url detected, placing it in the textbox");
					tfURL.setText(paste);
				} else {
					lblStatus.setText("NON Youtube text detected in the clipboard, ignoring it");
					System.out.println("NON Youtube text detected in the clipboard, ignoring it");
				}
			} else {
				if (paste.toLowerCase().contains(is_youtube_link_l.toLowerCase())
						|| paste.toLowerCase().contains(is_youtube_link_s.toLowerCase()) && autoMode == false) {
					System.out.println("Youtube url  detected, open the application window");
					trayIcon.displayMessage("Youtube", "Youtube link has been detected!", TrayIcon.MessageType.INFO);
				} else if (paste.toLowerCase().contains(is_youtube_link_l.toLowerCase())
						|| paste.toLowerCase().contains(is_youtube_link_s.toLowerCase()) && autoMode == true) {
					setClipboardContents(modifyURL(returnRegex(), convertString()));// add the converted url to the
																					// clipboard
					System.out.println("Youtube url  detected, converted url placed in your clipboard");
					trayIcon.displayMessage("Youtube", "Youtube link detected! Converted link in your clipboard",
							TrayIcon.MessageType.INFO);
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
	 *
	 * @param aString
	 *            the new clipboard contents
	 */
	public void setClipboardContents(String aString) {
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.
	 * Clipboard, java.awt.datatransfer.Transferable)
	 */
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub

	}

	/**
	 * ActionEvent handler - Called back upon button-click.
	 *
	 * @param evt
	 *            the evt
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {

		if (evt.getSource() == btnConvert) {
			// removeAll(convertString());
			modifyURL(returnRegex(), convertString());
			if (this.isActive() && autoMode == true) {
				setClipboardContents(tfNewURL.getText());// add the converted url to the clipboard
				lblStatus.setText("Conversion Complete. Converted link has been placed in your clipboard");
			} else {
				lblStatus.setText("Conversion Complete. Converted link has been placed in the appropriate text box");
			}
			// Display the counter value on the TextField tfCount
			// tfNew_URL.setText(new_URL);
		} else if (evt.getSource() == btnAddRule) {

			int result = JOptionPane.showConfirmDialog(null, ruleInputs, "New Regex Rule", JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				System.out.println("You entered " + tfNewRuleName.getText() + ", " + tfNewRule.getText());
				if (tfNewRuleName.getText().trim().length() > 1 && tfNewRule.getText().trim().length() > 1) {
					String ruleToSave = tfNewRuleName.getText() + "," + tfNewRule.getText() + "\n";
					//saveRuleToFile(tfNewRuleName.getText(), tfNewRuleName.getText());
					fileWriter(ruleToSave,"regex.txt");
					lblStatus.setText("Your rule has been saved successfully");
				} else {
					lblStatus.setText("Something went wrong while attempting to save your rule");
				}

			} else {
				System.out.println("User canceled / closed the dialog, result = " + result);
			}
		} else if (evt.getSource() == btnSave) {
			if (tfNewURL.getText().length() > 0) {
				appendSaved(tfNewURL.getText());
				//fileWriter(tfNewURL.getText(), "saved.txt");
			} else {
				lblStatus.setText("Nothing to save!");
			}
		} else if (evt.getSource() == btnOpenUrl) {
			if (Desktop.isDesktopSupported() && !savedURLs.isSelectionEmpty()) {
				try {
					Desktop.getDesktop().browse(new URI(savedURLs.getSelectedValue()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				lblStatus.setText("Please select an URL from the saved urls list");
			}
		} else if (evt.getSource() == btnRemoveUrl) {
			if (!savedURLs.isSelectionEmpty()) {
				removeSaved(savedURLs.getSelectedValue());
				// fetchSaved();
			}
		} else if (evt.getSource() == auto) {
			if (auto.isSelected()) {
				autoMode = true;
				lblStatus.setText("Automatic mode activated successfully");
				auto.setIcon(rBtnIconOn);
				manual.setIcon(rBtnIconOff);
				auto.repaint();
				manual.repaint();
			}
		} else if (evt.getSource() == manual) {
			if (manual.isSelected()) {
				autoMode = false;
				lblStatus.setText("Manual mode activated successfully");
				auto.setIcon(rBtnIconOff);
				auto.repaint();
				manual.setIcon(rBtnIconOn);
				manual.repaint();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		checkClipboardContents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		System.exit(0); // Terminate the program
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	/**
	 * Display all.
	 *
	 * @param col
	 *            the col
	 */
	static void displayAll(Collection<Character> col) {
		Iterator<Character> itr = col.iterator();
		while (itr.hasNext()) {
			String str = (String) itr.next().toString();
			System.out.print(str + " ");
		}
		System.out.println();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jnativehook.keyboard.NativeKeyListener#nativeKeyPressed(org.jnativehook.
	 * keyboard.NativeKeyEvent)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jnativehook.keyboard.NativeKeyListener#nativeKeyReleased(org.jnativehook.
	 * keyboard.NativeKeyEvent)
	 */
	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		keysPressed.remove(NativeKeyEvent.getKeyText(e.getKeyCode()));
		System.out.print(keysPressed.size() + " released \n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jnativehook.keyboard.NativeKeyListener#nativeKeyTyped(org.jnativehook.
	 * keyboard.NativeKeyEvent)
	 */
	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Key Typed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}
}
