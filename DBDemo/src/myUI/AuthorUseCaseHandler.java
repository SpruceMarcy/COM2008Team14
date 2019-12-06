package myUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

public class AuthorUseCaseHandler extends MainFrame  {

	/**
	 * 
	 * @return a jpanel that contains field for user to input info for the new submission
	 *         after user click add author, the panel will be changed to add author panel
	 */
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
	/**
	 * 
	 * @param work the work that are going to be sent to database
	 * @param mainAuthor to know whether this form is for author or correspondingauthor
	 * @return a panel/form that allow user to add a new author, 
	 *         user can submit after adding a author or continue adding other author
	 */
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
				if(!DatabaseHandler.isAccountExist(email)) {
					setMessage("please enter all details");
					return;
				}
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
				if(!DatabaseHandler.isAccountExist(email)) {
					setMessage("please enter all details");
					return;
				}
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
	
	// below provide jpanel that author see after log in
	
	
	/**
	 * 
	 * @param email user who log in
	 * @return a jpanel that contain submission for user to choose, 
	 *         and a button that allow user to change password
	 */
	public static JPanel createAuthorPanel(String email) {
		JPanel authorPanel = new JPanel();
		List<Work> works = DatabaseHandler.getWorks(email);
		authorPanel.setLayout(new GridLayout(0,1));
		JLabel labels = new JLabel("select your current submission");
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
	/**
	 * 
	 * @param email user who want to change pw
	 * @return a panel/form that user use to change pw
	 */
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

	/**
	 * 
	 * @param author
	 * @param work
	 * @return a panel that show author details of their selected work,
	 *         if user is corresponding author and there are enough review
	 *         user can response and submit final submission
	 */
	public static JPanel createAuthorWorkDetailPanel(String author, Work work) {
		boolean isMainAuthor = DatabaseHandler.getCorrespondingAuthor(work.workID).getEmail().toLowerCase().equals(author.toLowerCase());
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
					setMessage("submit fail, probably final pdf is missing");
				}
			});
			
			reviewPanel.add(submitButton, BorderLayout.SOUTH);
		}
		return reviewPanel;
	}
}
