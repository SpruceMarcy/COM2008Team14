import java.awt.*;
import javax.swing.*;

public class ChiefEditorFrame extends JFrame{
	public static final long serialVersionUID = 1L;
	
	private InterfaceMenuBar menuBar;
	private JTextField registerFirstName;
	public String getRegistrationFirstName() {return registerFirstName.getText();}
	private JTextField registerLastName;
	public String getRegistrationLastName() {return registerLastName.getText();}
	private JTextField registerEmail;
	public String getRegistrationEmail() {return registerEmail.getText();}
	private JTextField registerPassword;
	public String getRegistrationPassword() {return registerPassword.getText();}
	private JButton registerSubmit;

	private JList passList;
	private JButton passSubmit;
	private JButton passSubmitAuto;
	
	public ChiefEditorFrame(int width, int height, Runnable onLogout) {
		super("Chief Editor");
		setSize(width,height);
		Font font = new Font("Sans-Serif",0,18);
		
		menuBar=new InterfaceMenuBar(font, onLogout);
		registerFirstName=new JTextField("placeholder first name");
		registerLastName=new JTextField("placeholder last name");
		registerEmail=new JTextField("placeholder@example.com");
		registerPassword=new JPasswordField("Password"); 
		registerSubmit=new JButton("Register");
		passList=new JList();
		passSubmit=new JButton("Pass Chief Editor Role");
		passSubmitAuto=new JButton("Automatically choose new Chief Editor");
		
		registerFirstName.setFont(font);
		registerFirstName.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(registerFirstName,new Dimension(256,32));
		registerLastName.setFont(font);
		registerLastName.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(registerLastName,new Dimension(256,32));
		registerEmail.setFont(font);
		registerEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(registerEmail,new Dimension(256,32));
		registerPassword.setFont(font);
		registerPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
		UserInterface.forceSize(registerPassword,new Dimension(256,32));
		registerSubmit.setFont(font);
		registerSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		passList.setFont(font);
		passList.setListData(new String[]{"Todo: list other editors here"});
		passList.setLayoutOrientation(JList.VERTICAL);
		UserInterface.forceSize(passList,new Dimension(256,100));
		passSubmit.setFont(font);
		passSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		passSubmitAuto.setFont(font);
		passSubmitAuto.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		JPanel containerPanel = new JPanel();
		JPanel registerPanel = new JPanel();
		JPanel retirePanel = new JPanel();
		EditorPanel editorPanel = new EditorPanel();
		containerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.CENTER;
		
		registerPanel.setLayout(new BoxLayout(registerPanel,BoxLayout.Y_AXIS));
		registerPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Register"));
		retirePanel.setLayout(new BoxLayout(retirePanel,BoxLayout.Y_AXIS));
		retirePanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Pass Role"));
		editorPanel.setLayout(new BoxLayout(editorPanel,BoxLayout.Y_AXIS));
		editorPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Articles Under Consideration"));
		
		registerPanel.add(Box.createVerticalGlue());
		registerPanel.add(UserInterface.makeLabel("Register other academic as an editor.",Component.CENTER_ALIGNMENT,font));
		registerPanel.add(Box.createVerticalStrut(32)); 
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
		registerPanel.add(registerEmail);
		registerPanel.add(Box.createVerticalStrut(8)); 
		registerPanel.add(UserInterface.makeLabel("Password:",Component.CENTER_ALIGNMENT,font));
		registerPanel.add(Box.createVerticalStrut(4)); ;
		registerPanel.add(registerPassword);
		registerPanel.add(Box.createVerticalStrut(8)); 
		registerPanel.add(registerSubmit);
		registerPanel.add(Box.createVerticalGlue());
		
		retirePanel.add(Box.createVerticalGlue());
		retirePanel.add(UserInterface.makeLabel("Pass role as chief editor onto another editor",Component.CENTER_ALIGNMENT,font));
		retirePanel.add(Box.createVerticalStrut(32)); 
		retirePanel.add(UserInterface.makeLabel("Select Editor:",Component.CENTER_ALIGNMENT,font));
		retirePanel.add(Box.createVerticalStrut(4)); 
		retirePanel.add(passList);
		retirePanel.add(Box.createVerticalStrut(8)); 
		retirePanel.add(passSubmit);
		retirePanel.add(Box.createVerticalStrut(8)); 
		retirePanel.add(UserInterface.makeLabel("Or",Component.CENTER_ALIGNMENT,font));
		retirePanel.add(Box.createVerticalStrut(8)); 
		retirePanel.add(passSubmitAuto);
		retirePanel.add(Box.createVerticalGlue());
		
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//registerPanel.setBackground(Color.green);
		containerPanel.add(registerPanel,gbc);
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 1;
		//retirePanel.setBackground(Color.red);
		containerPanel.add(retirePanel,gbc);
		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		//editorPanel.setBackground(Color.blue);
		containerPanel.add(editorPanel,gbc);

		setJMenuBar(menuBar);
		contentPane.add(containerPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}
