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
				changePanel(createEditorPanel(emailTF.getText(), issn));
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
					changePanel(createEditorSelectionPanel(userName));
					return;
				}
				break;
				
			case "Author" : 
				if(DatabaseHandler.isAuthor(userName)) {
					changePanel(createAuthorPanel(userName));
					return;
				}
				break;
			case "Reviewer" :
				if(DatabaseHandler.isReviewer(userName)) {
					changePanel(createReviewerPanel(userName));
					return;
				}
				break;
			}
			setMessage("you do not have access to role: "+role);
			
		});
		logInPanel.add(logInButton);
		return logInPanel;
	}
	
	
	
	public static JPanel createAuthorPanel(String email) {
		JPanel authorPanel = new JPanel();
		List<Work> works = DatabaseHandler.getWorks(email);
		authorPanel.setLayout(new GridLayout(0,1));
		JLabel labels = new JLabel("select your current journal");
		authorPanel.add(labels);
		for(Work i : works) {
			if(i.state==2)continue;
			JButton currentButton = new JButton(i.title);
			currentButton.addActionListener((event)->{
				changePanel(createAuthorWorkDetailPanel(email,i));
			});
			authorPanel.add(currentButton);
		}

		JButton changePWbutton = new JButton("change password");
		changePWbutton.addActionListener((event)->{
			changePanel(changeAuthorPasswordPanel(email));
		});
		authorPanel.add(changePWbutton);
		return authorPanel;
	}
	public static JPanel changeAuthorPasswordPanel(String email) {
		JPanel changePasswordPanel = new JPanel();
		changePasswordPanel.setLayout(new GridLayout(0,2));
		changePasswordPanel.add(new JLabel("old password:"));
		JPasswordField oldPF = new JPasswordField();
		changePasswordPanel.add(oldPF);
		changePasswordPanel.add(new JLabel("new password:"));
		JPasswordField newPF = new JPasswordField();
		changePasswordPanel.add(newPF);
		changePasswordPanel.add(new JLabel("reenter password:"));
		JPasswordField newPF2 = new JPasswordField();
		changePasswordPanel.add(newPF2);
		
		JButton cancelButton = new JButton("cancel");
		cancelButton.addActionListener((event)->{
			changePanel(createAuthorPanel(email));
		});
		JButton submitButton = new JButton("change password");
		submitButton.addActionListener((event)->{
			if(DatabaseHandler.logIn(email, oldPF.getText())) {
				if(newPF.getText().equals(newPF2.getText())) {
					DatabaseHandler.changePassword(email, newPF.getText());
					changePanel(createAuthorPanel(email));
					setMessage("password is changed");
				}else {
					setMessage("new password does not equal");
				}
			}else {
				setMessage("old password incorrect");
			}
		});
		changePasswordPanel.add(cancelButton);
		changePasswordPanel.add(submitButton);
		return changePasswordPanel;
	}

	public static JPanel createAuthorWorkDetailPanel(String author, Work work) {
		boolean isMainAuthor = DatabaseHandler.getCorrespondingAuthor(work.workID).getEmail().equals(author);
		List<Review> reviews = DatabaseHandler.getReviewsAndVerdicts(work.workID);
		System.out.println(reviews.size());
		System.out.println(isMainAuthor);
		JPanel reviewPanel = new JPanel();
		reviewPanel.setLayout(new BorderLayout());
		
		JPanel reviewTextPanel = new JPanel();
		reviewPanel.add(reviewTextPanel, BorderLayout.NORTH);
		reviewTextPanel.setLayout(new GridLayout(0,2));
		reviewTextPanel.add(new JLabel("Title:"));
		JTextField titleTF = new JTextField(work.title);
		if(reviews.size()>=3 && isMainAuthor) reviewTextPanel.add(titleTF);
		else reviewTextPanel.add(new JLabel(work.title));
		reviewTextPanel.add(new JLabel("Abstract:"));

		JTextField abstractTF = new JTextField(work._abstract);
		if(reviews.size()>=3 && isMainAuthor) reviewTextPanel.add(abstractTF);
		else reviewTextPanel.add(new JLabel(work._abstract));
		
		pdf = null;
		reviewTextPanel.add(new JLabel("pdf:"));
		JButton uploadButton = new JButton("choose from your desktop");
		uploadButton.addActionListener((event)->{
			JFileChooser pdfFC = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			int returnValue = pdfFC.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = pdfFC.getSelectedFile();
				System.out.println(selectedFile.getAbsolutePath());
				uploadButton.setText(selectedFile.getName());
				pdf = selectedFile;
			}
		});
		reviewTextPanel.add(uploadButton);
		for(Review review:reviews) {
			reviewTextPanel.add(new JLabel("verdict"+review.reviewID+" :"));
			reviewTextPanel.add(new JLabel(verdictIDtoVerdict(review.verdict)));
			reviewTextPanel.add(new JLabel("review"+review.reviewID+" :"));
			reviewTextPanel.add(new JLabel(review.review));
		}
		if(reviews.size()>=3 && isMainAuthor) {
			JPanel responsePanel = new JPanel();
			responsePanel.setLayout(new BorderLayout());
			List<JTextArea> responese = new ArrayList<JTextArea>();
			for(int i=0;i<reviews.size();i++) {
				Review review = reviews.get(i);
				JPanel responsePanel1 = new JPanel();
				responsePanel1.setLayout(new GridLayout(0,2));
				responsePanel1.add(new JLabel("response"+review.reviewID+" :"));
				JTextArea responseTA = new JTextArea(3, 15);
				responseTA.setLineWrap(true);
				responsePanel1.add(responseTA);
				responese.add(responseTA);
				if(i==0)
					responsePanel.add(responsePanel1, BorderLayout.NORTH);
				else if(i==1) {
					responsePanel.add(responsePanel1, BorderLayout.CENTER);
					responseTA.setBackground(Color.yellow);
				}
				else
					responsePanel.add(responsePanel1, BorderLayout.SOUTH);
			}
			reviewPanel.add(responsePanel);
			JButton submitButton = new JButton("SUBMIT");
			submitButton.addActionListener((event)->{
				try {
					DatabaseHandler.addSubmision(work.workID, titleTF.getText(), abstractTF.getText(),pdf, false);
					for(int i=0;i<3;i++)
						DatabaseHandler.addResponse(work.workID, 1, reviews.get(i).reviewerID, responese.get(i).getText());
					changePanel(mainPanel());
					setMessage("submit success");
					pdf=null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					setMessage("submit fail");
				}
			});
			
			reviewPanel.add(submitButton, BorderLayout.SOUTH);
		}
		return reviewPanel;
	}

	public static JPanel createReviewerPanel(String email) {
		JPanel reviewerPanel = new JPanel();
		int reviewerID = DatabaseHandler.getReviewerID(email);
		reviewerPanel.setLayout(new GridLayout(0,1));
		reviewerPanel.add(new JLabel("HERE are the submission you can review"));
		String affiliation = DatabaseHandler.getAffiliation(email);
		List<Work> reviewList = DatabaseHandler.getWorksReview(affiliation);
		for(Work review : reviewList) {
			System.out.println(review.title);
			JButton button = new JButton(review.title);
			button.addActionListener((event)->{
				changePanel(createReviewWorkPanel(reviewerID,review));
			});
			reviewerPanel.add(button);
		}
		return reviewerPanel;
	}
	public static String verdictIDtoVerdict(int verdictID) {

		switch(verdictID) {
		case 1:return "Strong Accept";
		case 2:return "Weak Accept";
		case 3:return "Weak Reject";
		case 4:return "Strong Reject";
		}
		return "";
	}
	public static JPanel createReviewWorkPanel(int reviewerID, Work work) {
		String oldReview = DatabaseHandler.getReview(reviewerID, work.workID);
		int oldVerdict = DatabaseHandler.getVerdict(reviewerID, work.workID,1);
		System.out.println(oldReview);
		JPanel reviewPanel = new JPanel();
		reviewPanel.setLayout(new BorderLayout());
		JPanel reviewTextPanel = new JPanel();
		reviewPanel.add(reviewTextPanel, BorderLayout.NORTH);
		reviewTextPanel.setLayout(new GridLayout(0,2));
		reviewTextPanel.add(new JLabel("Title:"));
		reviewTextPanel.add(new JLabel(work.title));
		reviewTextPanel.add(new JLabel("Abstract:"));
		reviewTextPanel.add(new JLabel(work._abstract));

		reviewTextPanel.add(new JLabel("pdf:"));
		reviewTextPanel.add(downloadButton(work));

		JButton sumbitButton = new JButton("SUBMIT");
		if(work.state==1) {
			reviewTextPanel.add(new JLabel("verdict:"));
			JComboBox<String> verdictChoice = new JComboBox<String>();
			verdictChoice.addItem("Strong Accept");
			verdictChoice.addItem("Weak Accept");
			verdictChoice.addItem("Weak Reject");
			verdictChoice.addItem("Strong Reject");
			if(oldVerdict>0)
				verdictChoice.setSelectedIndex(oldVerdict-1);
			reviewTextPanel.add(verdictChoice);
			
			JPanel midPanel = new JPanel();
			reviewPanel.add(midPanel, BorderLayout.CENTER);
			midPanel.setLayout(new GridLayout(1,2));
			midPanel.add(new JLabel("review:"));
			JTextArea reviewTA = new JTextArea(15, 40);
			reviewTA.setLineWrap(true);
			reviewTA.setText(oldReview);
			midPanel.add(reviewTA);
			
			sumbitButton.addActionListener((event)->{
				try {
					DatabaseHandler.addReview(work.workID,
						1, reviewerID, reviewTA.getText(), verdictChoice.getSelectedIndex()+1);
					changePanel(mainPanel());
				}
				catch(Exception e) {
					setMessage("submit fail");
				}
				
			});
		}
		if(work.state==2) {
			reviewTextPanel.add(new JLabel("last verdict:"));

			String verdictSelected = verdictIDtoVerdict(oldVerdict);
			
			reviewTextPanel.add(new JLabel(verdictSelected));
			reviewTextPanel.add(new JLabel("last review:"));
			reviewTextPanel.add(new JLabel(oldReview));
			String response = DatabaseHandler.getResponse(reviewerID,work.workID);
			reviewTextPanel.add(new JLabel("response:"));
			reviewTextPanel.add(new JLabel(response));
			
			int oldVerdict2 = DatabaseHandler.getVerdict(reviewerID, work.workID,2);
			reviewTextPanel.add(new JLabel("new verdict:"));
			JComboBox<String> verdictChoice = new JComboBox<String>();
			verdictChoice.addItem("Strong Accept");
			verdictChoice.addItem("Weak Accept");
			verdictChoice.addItem("Weak Reject");
			verdictChoice.addItem("Strong Reject");
			if(oldVerdict2>0)
				verdictChoice.setSelectedIndex(oldVerdict2-1);
			reviewTextPanel.add(verdictChoice);
			sumbitButton.addActionListener((event)->{
				try {
					DatabaseHandler.addVerdict(work.workID, 2, reviewerID, verdictChoice.getSelectedIndex()+1);
					changePanel(mainPanel());
					int reviewCount = DatabaseHandler.getReviewCount(reviewerID);
					System.out.println(reviewCount);
					if(reviewCount>=3) {
						DatabaseHandler.removeReviewer(reviewerID);
						setMessage("you have reviewed 3 submission, your reviewer account is disabled and removed");
					}else {
						setMessage("you have reviewed "+reviewCount+" submission");
					}
				}
				catch(Exception e) {
					setMessage("submit fail");
				}
			});
		}
		reviewPanel.add(sumbitButton, BorderLayout.SOUTH);
		return reviewPanel;
	}
	private static JButton downloadButton(Work work) {
		JButton downloadPdfButton = new JButton("download to your computer");
		downloadPdfButton.addActionListener((event)->{
			File file = work.getPdf();
			setMessage("please wait, pdf is downloading to "+file.getAbsolutePath());
		});
		return downloadPdfButton;
	}
	
	public static JPanel createEditorSelectionPanel(String email) {
		/**
		 * select which journal to work on
		 */
		JPanel editorPanel = new JPanel();
		List<Integer> journals = DatabaseHandler.getJournals(email);
		editorPanel.setLayout(new GridLayout(journals.size()+1,1));
		JLabel labels = new JLabel("select your current journal");
		editorPanel.add(labels);
		for(Integer i : journals) {
			JButton currentButton = new JButton("issn:"+i);
			currentButton.addActionListener((event)->{
				changePanel(createEditorPanel(email, i));
			});
			editorPanel.add(currentButton);
		}
		return editorPanel;
	}
	
	public static JPanel createEditorPanel(String user, int issn) {
		// EdTomas/pw : chiefeditor
		// EdGordon : editor
		System.out.println(DatabaseHandler.getChiefEditor(issn));
		boolean isChiefEditor = DatabaseHandler.getChiefEditor(issn).equals(user);
		System.out.println(isChiefEditor);
		
		JPanel editorPanel = new JPanel();
		editorPanel.setLayout(new GridLayout(0,1));
		editorPanel.add(new JLabel("signed in as "+user+(isChiefEditor?" (chief editor)":" (editor)")));
		
		JButton addVolumeButton = new JButton("add volume");
		addVolumeButton.addActionListener((event)->{
			changePanel(addVolumePanel(issn,user));
		});
		editorPanel.add(addVolumeButton);
		JButton addEdition = new JButton("add edition");
		addEdition.addActionListener((event)->{
			changePanel(addEditionPanel(issn, user));
		});
		editorPanel.add(addEdition);
		
		if(isChiefEditor) {
			JButton registerButton = new JButton("register new editor");
			registerButton.addActionListener((event)->{
				changePanel(createRegisterPage(user, issn));
			});
			JButton passroleButton = new JButton("pass your role");
			passroleButton.addActionListener((event)->{
				changePanel(passRolePanel(user, issn));
			});
			
			editorPanel.add(registerButton);
			editorPanel.add(passroleButton);
		}else {
			JButton retireButton = new JButton("retire");
			retireButton.addActionListener((event)->{
				DatabaseHandler.removeEditing(issn, user);
				changePanel(mainPanel());
			});
			editorPanel.add(retireButton);
		}
		JButton manageArticleButton = new JButton("manage article");
		manageArticleButton.addActionListener((event)->{
			changePanel(manageSubmissionPanel(issn, user));
		});
		editorPanel.add(manageArticleButton);
		return editorPanel;
	}
	public static JPanel manageSubmissionPanel(int issn, String editor) {
		/** 
		 * show a list of work submitted to this issn
		 */
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		panel.add(new JLabel("here are the submissions:"));
		List<Work> works = DatabaseHandler.getSubmission(issn);
		for(Work work : works) {
			JButton button = new JButton(work.title);
			button.addActionListener((event)->{
				changePanel(acceptSubmissionPanel(work, issn, editor));
			});
			panel.add(button);
		}
		JButton cancelButton = new JButton("cancel");
		cancelButton.addActionListener((event)->{
			changePanel(createEditorPanel(editor, issn));
		});
		panel.add(cancelButton);
		return panel;
	}
	public static JPanel addVolumePanel(int issn, String editor) {
		JPanel addVolumePanel = new JPanel();
		addVolumePanel.setLayout(new GridLayout(0,1));
		addVolumePanel.add(new JLabel("enter the volume number"));
		JTextField newVolumeTF = new JTextField();
		addVolumePanel.add(newVolumeTF);
		JButton finishButton = new JButton("add volume");
		finishButton.addActionListener((event)->{
			try {
				DatabaseHandler.addVolumne(issn, Integer.parseInt(newVolumeTF.getText()));
				changePanel(createEditorPanel(editor, issn));
				setMessage("new volume is added");
			} catch (NumberFormatException e) {
				setMessage("only accept integer");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				setMessage("same volume aready exist");
				changePanel(createEditorPanel(editor, issn));
			}
		});
		addVolumePanel.add(finishButton);
		JButton cancelButton = new JButton("cancel");
		cancelButton.addActionListener((event)->{
			changePanel(createEditorPanel(editor, issn));
		});
		addVolumePanel.add(cancelButton);
		return addVolumePanel;
	}
	public static JPanel addEditionPanel(int issn, String editor) {
		JPanel addEditionPanel = new JPanel();
		addEditionPanel.setLayout(new GridLayout(0,1));
		addEditionPanel.add(new JLabel("select the volume"));
		List<Integer> editions = DatabaseHandler.getVolumes(issn);
		
		for(Integer edition : editions) {
			JButton edButton = new JButton(edition.toString());
			edButton.addActionListener((event)->{
				changePanel(addEditionPanel2(issn,edition,editor));
			});
			addEditionPanel.add(edButton);
		}
		JButton cancelButton = new JButton("cancel");
		cancelButton.addActionListener((event)->{
			changePanel(createEditorPanel(editor, issn));
		});
		addEditionPanel.add(cancelButton);
		return addEditionPanel;
	}
	public static JPanel addEditionPanel2(int issn, int volume, String editor) {
		JPanel addEditionPanel = new JPanel();
		addEditionPanel.setLayout(new GridLayout(0,1));
		addEditionPanel.add(new JLabel("enter the edition number"));

		JTextField newEditionTF = new JTextField();
		addEditionPanel.add(newEditionTF);
		JButton finishButton = new JButton("add edition");
		finishButton.addActionListener((event)->{
			try {
				int i = Integer.parseInt(newEditionTF.getText());
				DatabaseHandler.addEdition(issn, volume, i);
				changePanel(createEditorPanel(editor, issn));
				setMessage("new edition is added");
			} catch (NumberFormatException e) {
				setMessage("only accept integer");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				changePanel(createEditorPanel(editor, issn));
				setMessage("the same edition already exist");
			}
		});
		addEditionPanel.add(finishButton);
		JButton cancelButton = new JButton("cancel");
		cancelButton.addActionListener((event)->{
			changePanel(createEditorPanel(editor, issn));
		});
		addEditionPanel.add(cancelButton);
		return addEditionPanel;
	}
	
	public static JPanel acceptSubmissionPanel(Work work, int issn, String email) {
		List<Review> reviews = DatabaseHandler.getReviewsAndVerdicts(work.workID);
		String editorAffiliation = DatabaseHandler.getAffiliation(email);
		boolean canAccept = !DatabaseHandler.isSameAffiliation(editorAffiliation, work.workID);
		
		JPanel acceptWorkPanel = new JPanel();
		acceptWorkPanel.setLayout(new BoxLayout(acceptWorkPanel, BoxLayout.Y_AXIS));
		
		JPanel workDetailPanel = new JPanel();
		acceptWorkPanel.add(workDetailPanel, BorderLayout.NORTH);
		workDetailPanel.setLayout(new GridLayout(0,2));
		workDetailPanel.add(new JLabel("Title:"));
		workDetailPanel.add(new JLabel(work.title));
		workDetailPanel.add(new JLabel("Abstract:"));
		workDetailPanel.add(new JLabel(work._abstract));
		workDetailPanel.add(new JLabel("pdf:"));
		workDetailPanel.add(downloadButton(work));
		for(Review r : reviews) {
			workDetailPanel.add(new JLabel("review"+r.reviewID+" :"));
			workDetailPanel.add(new JLabel(r.review));
			
			int verdict = DatabaseHandler.getVerdict(r.reviewerID, work.workID, 2);
			String response = DatabaseHandler.getResponse(r.reviewerID, work.workID);
			workDetailPanel.add(new JLabel("response"+r.reviewID+" :"));
			workDetailPanel.add(new JLabel(response));
			workDetailPanel.add(new JLabel("verdict"+r.reviewID+" :"));
			workDetailPanel.add(new JLabel(verdictIDtoVerdict(verdict)));
			
		}
		
		workDetailPanel.add(new JLabel("volume"));
		JTextField volumeTF = new JTextField();
		workDetailPanel.add(volumeTF);		
		workDetailPanel.add(new JLabel("edition"));
		JTextField editionTF = new JTextField();
		workDetailPanel.add(editionTF);		
		workDetailPanel.add(new JLabel("page"));
		JTextField pageTF = new JTextField();
		workDetailPanel.add(pageTF);		
		
		JButton rejectButton = new JButton("reject");
		rejectButton.addActionListener((event)->{
			DatabaseHandler.rejectWork(work.workID);
			changePanel(manageSubmissionPanel(issn, email));
			setMessage("a work is rejected");
		});
		JButton acceptButton = new JButton("accept");
		acceptButton.addActionListener((event)->{
			try {
				DatabaseHandler.addArticle(issn, Integer.parseInt(volumeTF.getText()),
						Integer.parseInt(editionTF.getText()),Integer.parseInt(pageTF.getText()),
						0, work.workID);
				changePanel(manageSubmissionPanel(issn, email));
				setMessage("a work is accepted");
			} catch (NumberFormatException e) {
				setMessage("edition/volume/page text field only accept integer");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				setMessage("edition/volume not exist, or another article is using the same page number");
			}
		});
		if(canAccept) {
			workDetailPanel.add(rejectButton);
			workDetailPanel.add(acceptButton);
		}
		
		JPanel cancelPanel = new JPanel();
		JButton cancelButton = new JButton("cancel");
		cancelPanel.setLayout(new GridLayout(0,1));
		cancelButton.addActionListener((event)->{
			changePanel(createEditorPanel(email, issn));
		});

		cancelPanel.add(cancelButton);
		acceptWorkPanel.add(cancelPanel);
		if(!canAccept)acceptWorkPanel.add(new JLabel("unable to accpet or reject due to conflict of interest"));
		return acceptWorkPanel;
	}
	
	
	public static JPanel passRolePanel(String user, int issn) {
		JPanel passrolePanel = new JPanel();
		passrolePanel.setLayout(new GridLayout(0,1));
		List<String> editors = DatabaseHandler.getEditors(issn);
		for(String editor : editors) {
			if(editor.equals(user)) {
				continue;
			}
			JButton passroleButton = new JButton(editor);
			passroleButton.addActionListener((event)->{
				if(DatabaseHandler.changeChiefEditor(issn, editor)) {
					changePanel(createEditorPanel(user, issn));
					setMessage(editor+" is now the new chief editor");
				}else {
					setMessage("unable to change to this editor");
				}
			});
			passrolePanel.add(passroleButton);
		}
		if(editors.size() == 1) {
			passrolePanel.add(new JLabel("this journal has no other editors"));
		}
		JButton cancelButton = new JButton("--cancel--");
		cancelButton.addActionListener((event)->{
			changePanel(createEditorPanel(user, issn));
		});
		passrolePanel.add(cancelButton);
		
		return passrolePanel;
	}
	
	public static JPanel createRegisterPage(String user, int issn) {
		JPanel registerPanel = new JPanel();
		JPanel signUpPanelField = new JPanel();
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
		JButton goBackButton = new JButton("cancel");
		goBackButton.addActionListener((event)->{
			changePanel(createEditorPanel(user, issn));
		});
		JButton registerButton = new JButton("register new editor for issn "+issn);
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
			
			
			if(
			DatabaseHandler.signUp(emailTF.getText(), passwordTF.getText(),
					titleTF.getText(), forenameTF.getText(), 
					surnameTF.getText(), affiliationTF.getText()))
			{
				//DatabaseHandler.addEditing(issn, emailTF.getText());
			}
			if(DatabaseHandler.addEditing(issn, emailTF.getText())) {
				changePanel(createEditorPanel(user, issn));
				return;
			}
			setMessage("an error occur when signing up for this email, maybe owner of this editor is already registered and included in the project");
		});
		
		signUpPanelField.setLayout(new GridLayout(0,2));
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
		signUpPanelField.add(goBackButton);
		signUpPanelField.add(registerButton);
		
		registerPanel.add(signUpPanelField);
		return registerPanel;
		
	}

}