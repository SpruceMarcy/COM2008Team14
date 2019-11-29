import java.awt.*;
import javax.swing.*;

public class ViewerFrame extends JFrame{
	public static final long serialVersionUID = 1L;
	
	private InterfaceMenuBar menuBar;
	private JList editions;
	private JList articles;
	private JLabel abstractText;
	
	public ViewerFrame(int width, int height, Runnable onLogout) {
		super("Journal Viewer");
		setSize(width,height);
		Font font = new Font("Sans-Serif",0,18);
		
		menuBar=new InterfaceMenuBar(font, onLogout);
		editions=new JList();
		articles=new JList();
		abstractText=new JLabel();
		
		editions.setFont(font);
		articles.setFont(font);
		abstractText.setFont(font);
		
		editions.setListData(new String[]{"Test","test","test","test","test","test","test","test","test","test","test","test","test"});
		editions.setLayoutOrientation(JList.VERTICAL);
		
		articles.setListData(new String[]{"Test","test","test","test","test","test","test","test","test","test","test","test","test"});
		articles.setLayoutOrientation(JList.VERTICAL);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		JPanel containerPanel = new JPanel();
		JPanel leftPanel = new JPanel();
		JPanel centrePanel = new JPanel();
		JPanel rightPanel = new JPanel();
		containerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.gridy=0;
		gbc.weighty = 1.0;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.CENTER;
		
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(UserInterface.makeLabel("Select an Edition:",Component.CENTER_ALIGNMENT,font),BorderLayout.NORTH);
		leftPanel.add(editions,BorderLayout.CENTER);
		
		centrePanel.setLayout(new BorderLayout());
		centrePanel.add(UserInterface.makeLabel("Select an Article:",Component.CENTER_ALIGNMENT,font),BorderLayout.NORTH);
		centrePanel.add(articles,BorderLayout.CENTER);
		
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(UserInterface.makeLabel("Abstract:",Component.CENTER_ALIGNMENT,font),BorderLayout.NORTH);
		rightPanel.add(abstractText,BorderLayout.CENTER);
		
		gbc.weightx = 0.25;
		gbc.gridx = 0;
		containerPanel.add(leftPanel,gbc);
		gbc.weightx = 0.25;
		gbc.gridx = 1;
		containerPanel.add(centrePanel,gbc);
		gbc.weightx = 1.0;
		gbc.gridx = 2;
		containerPanel.add(rightPanel,gbc);
		
		//contentPane.add(Box.createVerticalGlue());
		setJMenuBar(menuBar);
		contentPane.add(containerPanel);
		//contentPane.add(Box.createVerticalGlue());
		//UserInterface.forceSize(loginPanel,new Dimension(256,300));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}
