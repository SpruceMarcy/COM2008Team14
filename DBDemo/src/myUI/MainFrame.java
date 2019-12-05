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

public class MainFrame extends JFrame {
// Needed for serialisation
	private static final long serialVersionUID = 1L;
	private static Container contentPane;
	private static JPanel extraPanel;
	public static MainFrame instance;
	public static void main(String[] args) {
		instance = new MainFrame("My Application");
	}
	public MainFrame() {
		this("My Application");
		setMessage("WELCOME");
	}
	public MainFrame(String title) {
		this(title, mainPanel());
		setMessage("WELCOME");
	}
	public MainFrame(String title, JPanel defaultPanel) {
		super(title);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		setSize(screenSize.width/2, screenSize.height/2);
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
	public static void changePanel(JPanel panel) {
		contentPane.removeAll();
		clearMessage();
		contentPane.add(panel);
		contentPane.invalidate(); contentPane.validate(); contentPane.repaint();
	}
	public static void setMessage(String message) {
		lastSetTime = System.nanoTime();
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
	private static float lastSetTime = 0;
	public static void clearMessage() {
		if(System.nanoTime() > lastSetTime) {
			extraPanel.removeAll();
			JButton backToMenuButton = new JButton("home");
			backToMenuButton.addActionListener((event)->{
				changePanel(mainPanel());
			});
			extraPanel.add(backToMenuButton, BorderLayout.EAST);
		}
		extraPanel.invalidate(); extraPanel.validate(); extraPanel.repaint();
	}
	public static JPanel mainPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(5, 1));
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
		return buttonPanel;
	}
	public static File pdf = null;
	
	
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
			System.out.println(userName);
			System.out.println(password);
			System.out.println(role);
			boolean loginSuccess = DatabaseHandler.logIn(userName,password);
			if(userName.equals("x")) {
				loginSuccess = true;
				switch(role) {
				case "Editor":
					userName = "EdGordon";
					break;
				case "Author":
					userName = "AuMary";
					break;
				case "Reviewer":
					userName = "AuPeter";
					break;
				}
			}
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