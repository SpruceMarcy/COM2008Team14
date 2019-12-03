import java.awt.*;
import javax.swing.*;

public class EditorFrame extends JFrame{
	public static final long serialVersionUID = 1L;
	
	private InterfaceMenuBar menuBar;
	
	private JButton retireButton;
	
	public EditorFrame(int width, int height, Runnable onLogout) {
		super("Editor");
		setSize(width,height);
		Font font = new Font("Sans-Serif",0,18);
		
		menuBar=new InterfaceMenuBar(font, onLogout);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		JPanel containerPanel = new JPanel();
		EditorPanel editorPanel = new EditorPanel();
		containerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridheight=2;
		gbc.gridwidth=1;
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.weightx=gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		
		editorPanel.setLayout(new BoxLayout(editorPanel,BoxLayout.Y_AXIS));
		editorPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Articles Under Consideration"));
		
		retireButton = new JButton("Retire as an editor for this journal");
		retireButton.setFont(font);
		
		containerPanel.add(editorPanel,gbc);
		JPanel subContainerPanel = new JPanel();
		subContainerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		subContainerPanel.add(retireButton);
		gbc.gridy=2;
		gbc.weighty=0;
		containerPanel.add(subContainerPanel,gbc);
		
		setJMenuBar(menuBar);
		contentPane.add(containerPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}
