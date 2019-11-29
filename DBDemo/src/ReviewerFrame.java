import java.awt.*;
import javax.swing.*;

public class ReviewerFrame extends JFrame{
	public static final long serialVersionUID = 1L;
	
	private InterfaceMenuBar menuBar;
	//private JTextField loginEmailAddress;
	//public String getEmailAddress() {return loginEmailAddress.getText();}
	//private JTextField loginPassword;
	//public String getPassword() {return loginPassword.getText();}
	//private JComboBox<String> loginRole;
	//public String getRole() {return (String) loginRole.getSelectedItem();}
	//private JButton loginButton;

	
	public ReviewerFrame(int width, int height, Runnable onLogout) {
		super("Reviewer");
		setSize(width,height);
		Font font = new Font("Sans-Serif",0,18);
		
		menuBar=new InterfaceMenuBar(font, onLogout);
		//loginEmailAddress=new JTextField("placeholder@example.com");
		//loginPassword=new JPasswordField("Password");
		//loginRole=new JComboBox<String>(new String[] {"Chief Editor","Editor","Author","Reviewer"});
		//loginButton=new JButton("Log In");
		
		//loginEmailAddress.setFont(font);
		//loginEmailAddress.setAlignmentX(Component.CENTER_ALIGNMENT);
		//UserInterface.forceSize(loginEmailAddress,new Dimension(256,32));
		//loginPassword.setFont(font);
		//loginPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
		//UserInterface.forceSize(loginPassword,new Dimension(256,32));
		//loginButton.setFont(font);
		//loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		JPanel containerPanel = new JPanel();
		JPanel loginPanel = new JPanel();
		containerPanel.setLayout(new BoxLayout(containerPanel,BoxLayout.X_AXIS));
		loginPanel.setLayout(new BoxLayout(loginPanel,BoxLayout.Y_AXIS));
		
		containerPanel.add(loginPanel);

		
		//contentPane.add(Box.createVerticalGlue());
		setJMenuBar(menuBar);
		contentPane.add(containerPanel);
		//contentPane.add(Box.createVerticalGlue());
		//UserInterface.forceSize(loginPanel,new Dimension(256,300));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}