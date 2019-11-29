import java.awt.*;
import javax.swing.*;

public class UserInterface{
	private static UserLoginFrame userLogin;
	private static JFrame mainFrame;
	
	public static void main(String[] args) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		userLogin=new UserLoginFrame(screenSize.width/2,3*screenSize.height/4,UserInterface::onLoginAttempt,
																			  UserInterface::onSetupAttempt,
																			  UserInterface::onViewArticles);
		userLogin.setLocation(screenSize.width/4,screenSize.height/8);

	}
	private static void onLoginAttempt(){
		if(true || UserInfo.logIn(userLogin.getEmailAddress(), userLogin.getPassword())) {
			startProgram(userLogin.getRole());
		}
		else {
			//TODO: userLogin.showLoginFailure()
		}
	}
	private static void onSetupAttempt(){
		try {
			//TODO: basic data validation
			//DatabaseHandler.createJounral(DatabaseHandler.getJournals().size(), 
			//							  userLogin.getRegistrationJournalName(), 
			//							  null,//TODO: I have no idea how the setup in DatabaseHandler.java was intended to be done
			//							  null //This parameter is not needed at this stage.
			//							  );
			startProgram("Chief Editor");
		}
		catch(Exception e){
			//TODO: userSetup.showSetupFailure()
		}
		finally{
			
		}
	}
	private static void onViewArticles() {
		startProgram("Viewer");
	}
	private static void startProgram(String Role) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		switch(Role) {
		case "Viewer":
			mainFrame=new ViewerFrame(screenSize.width/2,3*screenSize.height/4,UserInterface::onLogout);
			break;
		case "Chief Editor":
			mainFrame=new ChiefEditorFrame(screenSize.width/2,3*screenSize.height/4,UserInterface::onLogout);
			break;
		case "Editor":
			mainFrame=new EditorFrame(screenSize.width/2,3*screenSize.height/4,UserInterface::onLogout);
			break;
		case "Author":
			mainFrame=new AuthorFrame(screenSize.width/2,3*screenSize.height/4,UserInterface::onLogout);
			break;
		case "Reviewer":
			mainFrame=new ReviewerFrame(screenSize.width/2,3*screenSize.height/4,UserInterface::onLogout);
			break;
		}
		userLogin.setVisible(false);
		
		mainFrame.setLocation(screenSize.width/4,screenSize.height/8);
	}
	private static void onLogout() {
		userLogin.setVisible(true);
		mainFrame.dispose();
	}
	
	
	protected static JLabel makeLabel(String text, float xAlignment, Font font) {
		JLabel label = new JLabel(text);
		label.setAlignmentX(xAlignment);
		label.setFont(font);
		return label;
	}
	protected static void forceSize(Component comp, Dimension dim) {
		comp.setMinimumSize(dim);
		comp.setMaximumSize(dim);
		comp.setPreferredSize(dim);
	}
}
