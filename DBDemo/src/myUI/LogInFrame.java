package myUI;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
public class LogInFrame extends JFrame {
	public LogInFrame(String title) {
		super(title);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		setSize(screenSize.width/2, screenSize.height/2);
		setLocation(screenSize.width/4, screenSize.height/4);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
}
