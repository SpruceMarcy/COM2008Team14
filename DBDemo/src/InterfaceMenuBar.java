import java.awt.Font;
import javax.swing.*;

public class InterfaceMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;

	public InterfaceMenuBar(Font font,Runnable onLogout) {
		super();
		
		JMenu menu=new JMenu("Account");
		JMenuItem logoutButton=new JMenuItem("Log out");
		add(menu);
		add(new JSeparator(JSeparator.VERTICAL));
		menu.add(logoutButton);
		setFont(font);
		menu.setFont(font);
		logoutButton.setFont(font);
		
		logoutButton.addActionListener(e -> {
			try {
				onLogout.run();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

}
