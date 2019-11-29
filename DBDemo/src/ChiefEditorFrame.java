import java.awt.*;
import javax.swing.*;

public class ChiefEditorFrame extends JFrame{
	public static final long serialVersionUID = 1L;
	
	private InterfaceMenuBar menuBar;

	public ChiefEditorFrame(int width, int height, Runnable onLogout) {
		super("Chief Editor");
		setSize(width,height);
		Font font = new Font("Sans-Serif",0,18);
		
		menuBar=new InterfaceMenuBar(font, onLogout);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		JPanel containerPanel = new JPanel();
		JPanel mainPanel = new JPanel();
		containerPanel.setLayout(new BoxLayout(containerPanel,BoxLayout.X_AXIS));
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		
		containerPanel.add(mainPanel);

		setJMenuBar(menuBar);
		contentPane.add(containerPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}
