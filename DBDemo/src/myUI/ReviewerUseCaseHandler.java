package myUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ReviewerUseCaseHandler extends MainFrame {
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
}
