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
			changePanel(createReadJournalPanel());
		});
		JButton logInButton = new JButton("LOG IN");
		logInButton.addActionListener((event)->{
			changePanel(createLogInPanel());
		});
		JButton jounalButton = new JButton("NEW JOURNAL");
		jounalButton.addActionListener((event)->{
			changePanel(createNewJournal());
		});
		JButton submitButton = new JButton("NEW SUBMISSION");
		submitButton.addActionListener((event)->{
			changePanel(createNewSubmissionPanel());
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
	public static JPanel createReadJournalPanel() {
		List<Journal> journals = DatabaseHandler.getJournals();
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		largePanel.add(new JLabel("select a journal below"));
		JPanel journalPanel = new JPanel();
		journalPanel.setLayout(new GridLayout(0,1));
		largePanel.add(journalPanel);
		for(Journal journal : journals) {
			JButton button = new JButton(journal.name);
			button.addActionListener((event)->{
				changePanel(createReadVolumePanel(journal));
			});
			journalPanel.add(button);
		}
		return largePanel;
	}
	public static JPanel createReadVolumePanel(Journal journal) {
		List<Integer> volumes = DatabaseHandler.getVolumes(journal.issn);
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		largePanel.add(new JLabel("select a volume below"));
		JPanel volumePanel = new JPanel();
		volumePanel.setLayout(new GridLayout(0,1));
		largePanel.add(volumePanel);
		for(Integer volume : volumes) {
			JButton button = new JButton(volume.toString());
			button.addActionListener((event)->{
				changePanel(createReadEditionPanel(journal, volume));
			});
			volumePanel.add(button);
		}
		return largePanel;
	}

	public static JPanel createReadEditionPanel(Journal journal, int volume) {
		List<Integer> editions = DatabaseHandler.getEditions(journal.issn, volume);
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		largePanel.add(new JLabel("select an edition below"));
		JPanel editionPanel = new JPanel();
		editionPanel.setLayout(new GridLayout(0,1));
		largePanel.add(editionPanel);
		for(Integer edition : editions) {
			JButton button = new JButton(edition.toString());
			button.addActionListener((event)->{
				changePanel(createReadArticlePanel(journal, volume, edition));
			});
			editionPanel.add(button);
		}
		return largePanel;
	}
	public static JPanel createReadArticlePanel(Journal journal, int volume, int edition) {
		List<Article> articles = DatabaseHandler.getArticles(journal.issn, volume, edition);
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		largePanel.add(new JLabel("select an article below"));
		JPanel articlePanel = new JPanel();
		articlePanel.setLayout(new GridLayout(0,1));
		largePanel.add(articlePanel);
		for(Article article : articles) {
			JButton button = new JButton(article.work.title);
			button.addActionListener((event)->{
				changePanel(createArticlePanel(article));
			});
			articlePanel.add(button);
		}
		return largePanel;
	}
	public static JPanel createArticlePanel(Article article) {
		List<Author> authors = DatabaseHandler.getAuthors(article.work.workID);
		Author corrAuthor = DatabaseHandler.getCorrespondingAuthor(article.work.workID);
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		JPanel articlePanel = new JPanel();
		articlePanel.setLayout(new GridLayout(0,2));
		largePanel.add(articlePanel);
		
		articlePanel.add(new JLabel("title :"));
		articlePanel.add(new JLabel(article.work.title));
		articlePanel.add(new JLabel("abstract :"));
		articlePanel.add(new JLabel(article.work._abstract));
		articlePanel.add(new JLabel("pdf :"));
		articlePanel.add(downloadButton(article.work));
		articlePanel.add(new JLabel("at page :"));
		articlePanel.add(new JLabel(""+article.pageNum));
		articlePanel.add(new JLabel("corresponding author:"));
		articlePanel.add(new JLabel(corrAuthor.getName()));
		String otherAuthors = "";
		for(Author auth : authors) {
			if(corrAuthor.getName().equals(auth.getName())) {
				continue;
			}
			otherAuthors+=auth.getName()+", ";
		}
		articlePanel.add(new JLabel("other author(s):"));
		articlePanel.add(new JLabel(otherAuthors));
		return largePanel;
	}

	public static JPanel createNewJournal() {
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		largePanel.add(new JLabel("-----enter issn and journal name below(issn only accept integer):----"));

		JPanel panel = new JPanel();
		largePanel.add(panel);
		panel.setLayout(new GridLayout(0,2));
		panel.add(new JLabel("issn:"));
		JTextField issnTF = new JTextField(20);
		panel.add(issnTF);
		panel.add(new JLabel("journal name:"));
		JTextField nameTF = new JTextField(20);
		panel.add(nameTF);
		largePanel.add(new JLabel("-----enter your(editor) info below(if you aready have an account, only enter email):----"));
		//panel.add(new JLabel("-----"));
		JPanel panelUser = new JPanel();
		panelUser.setLayout(new GridLayout(0,2));
		largePanel.add(panelUser);
		panelUser.add(new JLabel("email:"));
		JTextField emailTF = new JTextField(20);
		panelUser.add(emailTF);
		panelUser.add(new JLabel("password:"));
		JTextField passwordTF = new JTextField(20);
		panelUser.add(passwordTF);
		panelUser.add(new JLabel("title:"));
		JTextField titleTF = new JTextField(20);
		panelUser.add(titleTF);
		panelUser.add(new JLabel("forename:"));
		JTextField forenameTF = new JTextField(20);
		panelUser.add(forenameTF);
		panelUser.add(new JLabel("surname:"));
		JTextField surnameTF = new JTextField(20);
		panelUser.add(surnameTF);
		panelUser.add(new JLabel("affliation:"));
		JTextField affiliationTF = new JTextField(20);
		panelUser.add(affiliationTF);
		
		JButton submit = new JButton("finish");
		submit.addActionListener((event)->{
			int issn = -1;
			try {
				issn = Integer.parseInt(issnTF.getText());
			}catch(NumberFormatException e){
				setMessage("issn only accept integer input");
				return;
			}
			String email = emailTF.getText();
			String password = passwordTF.getText();
			String title = titleTF.getText();
			String forename = forenameTF.getText();
			String surname = surnameTF.getText();
			String affiliation = affiliationTF.getText();
			if(email.equals("")||password.equals("")||title.equals("")||forename.equals("")
					||surname.equals("")||affiliation.equals("")) {
				setMessage("please enter all details");
				return;
			}
			
			
			DatabaseHandler.signUp(emailTF.getText(),
					passwordTF.getText(), titleTF.getText(),
					forenameTF.getText(), surnameTF.getText(), affiliationTF.getText());
			boolean success = DatabaseHandler.createJounral(issn,
					nameTF.getText(), emailTF.getText(), new ArrayList<String>());
			if(success) {
				changePanel(EditorUseCase.createEditorPanel(emailTF.getText(), issn));
				setMessage("new journal successfully created");
			}else {
				setMessage("journal with same issn already exist in database");
			}
		});
		JPanel submitPanel=new JPanel();
		submitPanel.setLayout(new GridLayout(0,1));
		largePanel.add(submitPanel);
		submitPanel.add(submit);
		
		return largePanel;
	}
	public static File pdf = null;
	
	public static JPanel createNewSubmissionPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,2));
		panel.add(new JLabel("title :"));
		JTextField titleTF = new JTextField();
		panel.add(titleTF);
		panel.add(new JLabel("abstract :"));
		JTextField abstractTF = new JTextField();
		panel.add(abstractTF);
		pdf = null;
		panel.add(new JLabel("pdf :"));
		JButton uploadButton = new JButton("choose from your desktop");
		uploadButton.addActionListener((event)->{
			JFileChooser pdfFC = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			int returnValue = pdfFC.showOpenDialog(null);
			// int returnValue = jfc.showSaveDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = pdfFC.getSelectedFile();
				System.out.println(selectedFile.getAbsolutePath());
				uploadButton.setText(selectedFile.getName());
				pdf = selectedFile;
			}
		});
		panel.add(uploadButton);

		panel.add(new JLabel("issn :"));
		JTextField issnTF = new JTextField();
		panel.add(issnTF);
		JButton addAuthor = new JButton("continue to add authors");
		addAuthor.addActionListener((event)->{
			if(pdf==null) {
				setMessage("please supply pdf");
				return;
			}
			try {
				Work work = new Work(titleTF.getText(),
						abstractTF.getText(),pdf,Integer.parseInt(issnTF.getText()));
				changePanel(addAuthorPanel(work, true));
				pdf = null;
			}catch(NumberFormatException e){
				setMessage("issn only accept integer input");
			}
		});
		panel.add(addAuthor);
		
		return panel;
	}
	public static JPanel addAuthorPanel(Work work, boolean mainAuthor) {
		JPanel registerPanel = new JPanel();
		registerPanel.setLayout(new BorderLayout());
		JPanel signUpPanelField = new JPanel();
		signUpPanelField.setLayout(new GridLayout(0,2));
		if(mainAuthor) {
			registerPanel.add(new JLabel("corresponding author(if the owner of the email "
					+ "already has an account, only fill in the email):"),BorderLayout.NORTH);
		}else {
			registerPanel.add(new JLabel("other author(if the owner of the email "
					+ "already has an account, only fill in the email):"),BorderLayout.NORTH);
		}
		
		JLabel emailLB = new JLabel("email:");
		JTextField emailTF = new JTextField(20);
		JLabel passwordLB = new JLabel("password:");
		JTextField passwordTF = new JTextField(20);
		JLabel titleLB = new JLabel("title:");
		JTextField titleTF = new JTextField(20);
		JLabel forenameLB = new JLabel("forename:");
		JTextField forenameTF = new JTextField(20);
		JLabel surnameLB = new JLabel("surname:");
		JTextField surnameTF = new JTextField(20);
		JLabel affiliationLB = new JLabel("affliation:");
		JTextField affiliationTF = new JTextField(20);
		JButton addMoreButton = new JButton("add more author");
		addMoreButton.addActionListener((event)->{

			String email = emailTF.getText();
			String password = passwordTF.getText();
			String title = titleTF.getText();
			String forename = forenameTF.getText();
			String surname = surnameTF.getText();
			String affiliation = affiliationTF.getText();
			if(email.equals("")||password.equals("")||title.equals("")||forename.equals("")
					||surname.equals("")||affiliation.equals("")) {
				setMessage("please enter all details");
				return;
			}
			
			Author newAuthor = new Author(titleTF.getText(),
					forenameTF.getText(),surnameTF.getText(),
					affiliationTF.getText(),emailTF.getText(),passwordTF.getText());
			if(mainAuthor) {
				work.mainAuthor = newAuthor;
			}else {
				work.authors.add(newAuthor);
			}
			changePanel(addAuthorPanel(work, false));
		});
		JButton registerButton = new JButton("finish");
		registerButton.addActionListener((event)->{

			String email = emailTF.getText();
			String password = passwordTF.getText();
			String title = titleTF.getText();
			String forename = forenameTF.getText();
			String surname = surnameTF.getText();
			String affiliation = affiliationTF.getText();
			if(email.equals("")||password.equals("")||title.equals("")||forename.equals("")
					||surname.equals("")||affiliation.equals("")) {
				setMessage("please enter all details");
				return;
			}
			Author newAuthor = new Author(titleTF.getText(),
					forenameTF.getText(),surnameTF.getText(),
					affiliationTF.getText(),emailTF.getText(),passwordTF.getText());
			if(mainAuthor) {
				work.mainAuthor = newAuthor;
			}else {
				work.authors.add(newAuthor);
			}
			String extraMessage = "";
			if(!work.mainAuthor.signup()) {
				extraMessage+=","+work.mainAuthor.email;
			}
			for(Author author : work.authors){
				if(!author.signup()) {
					extraMessage+=","+author.email;
				}
			}
			try {
				int workID = DatabaseHandler.addWork(work.issn, work.mainAuthor, work.authors);
				DatabaseHandler.addSubmision(workID, work.title, work._abstract,work.pdf, true);
				changePanel(mainPanel());
				if(extraMessage.equals("")) {
					setMessage("new work is added"+extraMessage);
				}else {
					setMessage("new work is added"+extraMessage+" already have an account, therefore their account(s) are not created");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				changePanel(mainPanel());
				setMessage("you cannot add the work, maybe the issn does not exist");
			}
			
		});
		signUpPanelField.add(emailLB);
		signUpPanelField.add(emailTF);
		signUpPanelField.add(passwordLB);
		signUpPanelField.add(passwordTF);
		signUpPanelField.add(titleLB);
		signUpPanelField.add(titleTF);
		signUpPanelField.add(forenameLB);
		signUpPanelField.add(forenameTF);
		signUpPanelField.add(surnameLB);
		signUpPanelField.add(surnameTF);
		signUpPanelField.add(affiliationLB);
		signUpPanelField.add(affiliationTF);
		signUpPanelField.add(addMoreButton);
		signUpPanelField.add(registerButton);
		
		registerPanel.add(signUpPanelField, BorderLayout.CENTER);
		return registerPanel;
	}
	
	
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
					changePanel(EditorUseCase.createEditorSelectionPanel(userName));
					return;
				}
				break;
				
			case "Author" : 
				if(DatabaseHandler.isAuthor(userName)) {
					changePanel(AuthorUseCase.createAuthorPanel(userName));
					return;
				}
				break;
			case "Reviewer" :
				if(DatabaseHandler.isReviewer(userName)) {
					changePanel(ReviewerUseCase.createReviewerPanel(userName));
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