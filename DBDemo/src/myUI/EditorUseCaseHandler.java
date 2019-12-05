package myUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * this class store jpanels used by editor
 * @author Asus
 *
 */
public class EditorUseCaseHandler extends MainFrame {

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
				changePanel(EditorUseCaseHandler.createEditorPanel(emailTF.getText(), issn));
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
		boolean isChiefEditor = DatabaseHandler.getChiefEditor(issn).toLowerCase().equals(user.toLowerCase());
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
			if(editor.toLowerCase().equals(user.toLowerCase())) {
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
