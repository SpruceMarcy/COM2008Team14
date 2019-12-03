import java.awt.*;
import javax.swing.*;

public class UserLoginFrame extends JFrame{
	public static final long serialVersionUID = 1L;
	
	private JTextField loginEmailAddress;
	public String getEmailAddress() {return loginEmailAddress.getText();}
	private JTextField loginPassword;
	public String getPassword() {return loginPassword.getText();}
	private JComboBox<String> loginRole;
	public String getRole() {return (String) loginRole.getSelectedItem();}
	private JComboBox<String> loginJournal;
	public String getLoginJournal() {return (String) loginJournal.getSelectedItem();}
	private JButton loginButton;
	
	private JTextField registerJournalName;
	public String getRegistrationJournalName() {return registerJournalName.getText();}
	private JTextField registerISSN;
	public int getRegistrationISSN() {return Integer.valueOf(registerISSN.getText());}
	private JTextField registerFirstName;
	public String getRegistrationFirstName() {return registerFirstName.getText();}
	private JTextField registerLastName;
	public String getRegistrationLastName() {return registerLastName.getText();}
	private JTextField registerEmailAddress;
	public String getRegistrationEmailAddress() {return registerEmailAddress.getText();}
	private JTextField registerPassword;
	public String getRegistrationPassword() {return registerPassword.getText();}
	private JButton registerButton;
	
	private JButton viewButton;
	
	public UserLoginFrame(int width, int height, Runnable onLogin, Runnable onRegistration, Runnable onView) {
		super("Log In or Register");
		setSize(width,height);
		Font font = new Font("Sans-Serif",0,18);
		
		loginEmailAddress=new JTextField("placeholder@example.com");
		loginPassword=new JPasswordField("Password");
		loginRole=new JComboBox<String>(new String[] {"Editor","Author","Reviewer"});
		loginJournal=new JComboBox<String>(new String[] {"Todo: list journals here"});
		loginButton=new JButton("Log In");
		registerJournalName=new JTextField("Untitled Journal");
		registerISSN=new JTextField("ISSN");
		registerFirstName=new JTextField("Bob");
		registerLastName=new JTextField("Bobson");
		registerEmailAddress=new JTextField("placeholder@example.com");
		registerPassword=new JPasswordField("Password"); 
		registerButton=new JButton("Register");
		viewButton=new JButton("View all Journals");
		
		loginEmailAddress.setFont(font);
		loginEmailAddress.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(loginEmailAddress,new Dimension(256,32));
		loginPassword.setFont(font);
		loginPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(loginPassword,new Dimension(256,32));
		loginRole.setFont(font);
		loginRole.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(loginRole,new Dimension(256,32));
		loginJournal.setFont(font);
		loginJournal.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(loginJournal,new Dimension(256,32));
		loginButton.setFont(font);
		loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		registerJournalName.setFont(font);
		registerJournalName.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(registerJournalName,new Dimension(256,32));
		registerISSN.setFont(font);
		registerISSN.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(registerISSN,new Dimension(256,32));
		registerFirstName.setFont(font);
		registerFirstName.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(registerFirstName,new Dimension(256,32));
		registerLastName.setFont(font);
		registerLastName.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(registerLastName,new Dimension(256,32));
		registerEmailAddress.setFont(font);
		registerEmailAddress.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(registerEmailAddress,new Dimension(256,32));
		registerPassword.setFont(font);
		registerPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(registerPassword,new Dimension(256,32));
		registerButton.setFont(font);
		registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		viewButton.setFont(font);
		viewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		JPanel containerPanel = new JPanel();
		JPanel loginPanel = new JPanel();
		JPanel registerPanel = new JPanel();
		containerPanel.setLayout(new BoxLayout(containerPanel,BoxLayout.X_AXIS));
		loginPanel.setLayout(new BoxLayout(loginPanel,BoxLayout.Y_AXIS));
		registerPanel.setLayout(new BoxLayout(registerPanel,BoxLayout.Y_AXIS));
		
		containerPanel.add(Box.createHorizontalGlue());
		containerPanel.add(loginPanel);
		containerPanel.add(Box.createHorizontalGlue()); //Strut(64));
		containerPanel.add(UserInterface.makeLabel("Or",Component.CENTER_ALIGNMENT,font));
		containerPanel.add(Box.createHorizontalGlue()); //Strut(64));
		containerPanel.add(registerPanel);
		containerPanel.add(Box.createHorizontalGlue());
		containerPanel.add(UserInterface.makeLabel("Or",Component.CENTER_ALIGNMENT,font));
		containerPanel.add(Box.createHorizontalGlue()); //Strut(64));
		containerPanel.add(viewButton);
		containerPanel.add(Box.createHorizontalGlue());
		
		contentPane.add(Box.createVerticalGlue());
		contentPane.add(containerPanel);
		contentPane.add(Box.createVerticalGlue());
		UserInterface.forceSize(loginPanel,new Dimension(256,600));
		UserInterface.forceSize(registerPanel,new Dimension(256,600));
		
		loginPanel.add(Box.createVerticalGlue());
		loginPanel.add(UserInterface.makeLabel("Login to an existing account",Component.CENTER_ALIGNMENT,font));
		loginPanel.add(Box.createVerticalStrut(32)); 
		loginPanel.add(UserInterface.makeLabel("Email Address:",Component.CENTER_ALIGNMENT,font));
		loginPanel.add(Box.createVerticalStrut(4)); 
		loginPanel.add(loginEmailAddress);
		loginPanel.add(Box.createVerticalStrut(8)); 
		loginPanel.add(UserInterface.makeLabel("Password:",Component.CENTER_ALIGNMENT,font));
		loginPanel.add(Box.createVerticalStrut(4));  
		loginPanel.add(loginPassword);
		loginPanel.add(Box.createVerticalStrut(8)); 
		loginPanel.add(UserInterface.makeLabel("Select Role:",Component.CENTER_ALIGNMENT,font));
		loginPanel.add(Box.createVerticalStrut(4));  
		loginPanel.add(loginRole);
		loginPanel.add(Box.createVerticalStrut(8));
		loginPanel.add(UserInterface.makeLabel("Select Journal:",Component.CENTER_ALIGNMENT,font));
		loginPanel.add(Box.createVerticalStrut(4));  
		loginPanel.add(loginJournal);
		loginPanel.add(Box.createVerticalStrut(8));
		loginPanel.add(loginButton);
		loginPanel.add(Box.createVerticalGlue());
		//loginPanel.setBackground(Color.RED);
		
		registerPanel.add(Box.createVerticalGlue());
		registerPanel.add(UserInterface.makeLabel("<html>Register as the Chief Editor for a new journal</html>",Component.CENTER_ALIGNMENT,font));
		registerPanel.add(Box.createVerticalStrut(32));
		registerPanel.add(UserInterface.makeLabel("Journal ISSN:",Component.CENTER_ALIGNMENT,font));
		registerPanel.add(Box.createVerticalStrut(4)); 
		registerPanel.add(registerISSN);
		registerPanel.add(Box.createVerticalStrut(8)); 
		registerPanel.add(UserInterface.makeLabel("Journal Name:",Component.CENTER_ALIGNMENT,font));
		registerPanel.add(Box.createVerticalStrut(4));  
		registerPanel.add(registerJournalName);
		registerPanel.add(Box.createVerticalStrut(8)); 
		registerPanel.add(UserInterface.makeLabel("First Name:",Component.CENTER_ALIGNMENT,font));
		registerPanel.add(Box.createVerticalStrut(4)); 
		registerPanel.add(registerFirstName);
		registerPanel.add(Box.createVerticalStrut(8)); 
		registerPanel.add(UserInterface.makeLabel("Last Name:",Component.CENTER_ALIGNMENT,font));
		registerPanel.add(Box.createVerticalStrut(4)); 
		registerPanel.add(registerLastName);
		registerPanel.add(Box.createVerticalStrut(8)); 
		registerPanel.add(UserInterface.makeLabel("Email Address:",Component.CENTER_ALIGNMENT,font));
		registerPanel.add(Box.createVerticalStrut(4)); 
		registerPanel.add(registerEmailAddress);
		registerPanel.add(Box.createVerticalStrut(8)); 
		registerPanel.add(UserInterface.makeLabel("Password:",Component.CENTER_ALIGNMENT,font));
		registerPanel.add(Box.createVerticalStrut(4));  
		registerPanel.add(registerPassword);
		registerPanel.add(Box.createVerticalStrut(8)); 
		registerPanel.add(registerButton);
		registerPanel.add(Box.createVerticalGlue());
		//registerPanel.setBackground(Color.BLUE);
		
		//pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		loginButton.addActionListener(e -> {
			try {
				onLogin.run();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		registerButton.addActionListener(e -> {
			try {
				onRegistration.run();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		viewButton.addActionListener(e -> {
			try {
				onView.run();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
}
