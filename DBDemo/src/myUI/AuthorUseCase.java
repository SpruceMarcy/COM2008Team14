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

public class AuthorUseCase extends MainFrame  {
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
}
