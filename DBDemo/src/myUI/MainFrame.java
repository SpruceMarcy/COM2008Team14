package myUI;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.NumberFormatter;

/**
 * Start the apps.
 * Contain the welcome panel, and the login panel.
 * Click button or log in to enter other panel, 
 *  which were stored in author/reader/editor/reviewer UseCaseHandler.
 * @author Asus
 * 
 */
public class MainFrame extends JFrame {
// Needed for serialisation
	private static final long serialVersionUID = 1L;
	private static final String version = "1.0";
	private static Container contentPane;
	private static JPanel extraPanel;
	public static MainFrame instance;
	public static void main(String[] args) {
		instance = new MainFrame("Academic Publicher System"+"_version"+version);
	}
	public MainFrame() {
		this("Academic Publicher System"+"_version"+version);
		setMessage("WELCOME");
	}
	public MainFrame(String title) {
		this(title, mainPanel());
		setMessage("WELCOME");
	}
	/**
	 * initialize the application, including setting the contentPane and extraPanel
	 * contentPane is the panel that consume most area and store the main panel
	 * extraPanel provide an extra area to store extra messages and home button
	 * 
	 * @param title name of application
	 * @param defaultPanel the initial panel when application start
	 *  
	 */
	public MainFrame(String title, JPanel defaultPanel) {
		super(title);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int width = screenSize.width/2;
		
		int height = screenSize.height/2;
		if(width<960) {
			width=960;
			height=540;
		}
		setSize(width, height);
		setLocation(screenSize.width/4, screenSize.height/4);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setLayout(new FlowLayout());
		extraPanel = new JPanel();
		Container mainContent = getContentPane();
		mainContent.setLayout(new BorderLayout());
		mainContent.add(contentPane, BorderLayout.CENTER);
		mainContent.add(extraPanel,BorderLayout.SOUTH);
		changePanel(defaultPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	/**
	 * change the current main panel to a new panel
	 * @param panel the panel to change to
	 */
	public static void changePanel(JPanel panel) {
		contentPane.removeAll();
		clearMessage();
		contentPane.add(panel);
		contentPane.invalidate(); contentPane.validate(); contentPane.repaint();
	}
	/** 
	 * 
	 * @param message: the message to be shown
	 */
	public static void setMessage(String message) {
		extraPanel.removeAll();
		extraPanel.setLayout(new BorderLayout());
		extraPanel.add(new JLabel(message), BorderLayout.CENTER);
		JButton backToMenuButton = new JButton("home");
		backToMenuButton.addActionListener((event)->{
			changePanel(mainPanel());
		});
		extraPanel.add(backToMenuButton, BorderLayout.EAST);
		extraPanel.invalidate(); extraPanel.validate(); extraPanel.repaint();
	}
	/**
	 * clear the message in extra panel
	 */
	public static void clearMessage() {
		extraPanel.removeAll();
		JButton backToMenuButton = new JButton("home");
		backToMenuButton.addActionListener((event)->{
			changePanel(mainPanel());
		});
		extraPanel.add(backToMenuButton, BorderLayout.EAST);
		extraPanel.invalidate(); extraPanel.validate(); extraPanel.repaint();
	}
	/**
	 * the welcome panel.
	 * contain 5 button:
	 *  read article,login,new journal
	 *  new submission,exit
	 * @return
	 */
	public static JPanel mainPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0, 1));
		JButton readerButton = new JButton("READ article");
		readerButton.addActionListener((event)->{
			changePanel(ReaderUseCaseHandler.createReadJournalPanel());
		});
		JButton logInButton = new JButton("LOG IN");
		logInButton.addActionListener((event)->{
			changePanel(createLogInPanel());
		});
		JButton jounalButton = new JButton("NEW JOURNAL");
		jounalButton.addActionListener((event)->{
			changePanel(EditorUseCaseHandler.createNewJournal());
		});
		JButton submitButton = new JButton("NEW SUBMISSION");
		submitButton.addActionListener((event)->{
			changePanel(AuthorUseCaseHandler.createNewSubmissionPanel());
		});
		JButton exitButton = new JButton("EXIT");
		exitButton.addActionListener((event)->{
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(buttonPanel);
			topFrame.dispatchEvent(new WindowEvent(topFrame, WindowEvent.WINDOW_CLOSING));
		});
		buttonPanel.add(readerButton);
		buttonPanel.add(logInButton);
		buttonPanel.add(jounalButton);
		buttonPanel.add(submitButton);
		buttonPanel.add(exitButton);
		buttonPanel.add(new JLabel("please resize the application if the screen is too small"));
		return buttonPanel;
	}
	
	public static File pdf = null;
	/**
	 * log in panel, contain username and password field
	 *  contain dropdown to select role and a login button
	 * @return
	 */
	public static JPanel createLogInPanel() {
		JPanel logInPanel = new JPanel();
		logInPanel.setLayout(new GridLayout(3,1));
		
		// first create user name and pw input
		JPanel logInPanelField = new JPanel();
		JLabel userNameLB = new JLabel("user name:");
		JTextField userNameTF = new JTextField(20);
		JLabel passwordLB = new JLabel("password:");
		JPasswordField passwordPF = new JPasswordField(20);
		logInPanelField.setLayout(new GridLayout(2,2));
		logInPanelField.add(userNameLB);
		logInPanelField.add(userNameTF);
		logInPanelField.add(passwordLB);
		logInPanelField.add(passwordPF);
		logInPanel.add(logInPanelField);
		// then combo box for role and button for log in
		JComboBox<String> roleCB = new JComboBox<String>();
		roleCB.addItem("Editor");
		roleCB.addItem("Author");
		roleCB.addItem("Reviewer");
		logInPanel.add(roleCB);

		JButton logInButton = new JButton("LOG IN");
		logInButton.addActionListener((event2)->{
			String userName = userNameTF.getText();
			String password = passwordPF.getText();
			String role = roleCB.getSelectedItem().toString();
			boolean loginSuccess = DatabaseHandler.logIn(userName,password);
			if(!loginSuccess) {
				setMessage("log in fail");
				return;
			}
			switch(role) {
			case "Editor" :
				if(DatabaseHandler.isEditor(userName)) {
					changePanel(EditorUseCaseHandler.createEditorSelectionPanel(userName));
					return;
				}
				break;
				
			case "Author" : 
				if(DatabaseHandler.isAuthor(userName)) {
					changePanel(AuthorUseCaseHandler.createAuthorPanel(userName));
					return;
				}
				break;
			case "Reviewer" :
				if(DatabaseHandler.isReviewer(userName)) {
					changePanel(ReviewerUseCaseHandler.createReviewerPanel(userName));
					return;
				}
				break;
			}
			setMessage("you do not have access to role: "+role);
			
		});
		logInPanel.add(logInButton);
		return logInPanel;
	}
	
	/**
	 * 
	 * @param work
	 * @return a download button to download the given work
	 */
	protected static JButton downloadButton(Work work) {
		JButton downloadPdfButton = new JButton("download to your computer");
		downloadPdfButton.addActionListener((event)->{
			File file = work.getPdf();
			setMessage("please wait, pdf is downloading to "+file.getAbsolutePath());
		});
		return downloadPdfButton;
	}

	protected static String verdictIDtoVerdict(int verdictID) {

		switch(verdictID) {
		case 1:return "Strong Accept";
		case 2:return "Weak Accept";
		case 3:return "Weak Reject";
		case 4:return "Strong Reject";
		}
		return "";
	}

}