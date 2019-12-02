import java.awt.*;
import javax.swing.*;

public class EditorPanel extends JPanel{
	public static final long serialVersionUID = 1L;
	
	private JList pendingArticles;
	private JLabel abstractText;
	private JComboBox verdict;
	private JButton verdictSubmit;
	
	public EditorPanel() {
		Font font = new Font("Sans-Serif",0,18);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		pendingArticles = new JList();
		abstractText = new JLabel();
		verdict = new JComboBox<String>(new String[] {"Strong Reject","Weak Reject","Weak Accept","Strong Accept"});
		verdictSubmit = new JButton("Submit Verdict");
		
		pendingArticles.setFont(font);
		pendingArticles.setAlignmentX(Component.CENTER_ALIGNMENT);
		abstractText.setFont(font);
		abstractText.setAlignmentX(Component.CENTER_ALIGNMENT);
		verdict.setFont(font);
		verdict.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(verdict,new Dimension(256,32));
		verdictSubmit.setFont(font);
		verdictSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		pendingArticles.setListData(new String[]{"Todo: list pending articles here"});
		
		add(pendingArticles);
		add(Box.createVerticalStrut(32)); 
		add(UserInterface.makeLabel("Abstract:",Component.CENTER_ALIGNMENT,font));
		add(Box.createVerticalStrut(4)); 
		add(abstractText);
		add(Box.createVerticalGlue());
		add(verdict);
		add(verdictSubmit);
	}
}