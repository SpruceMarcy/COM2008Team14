package myUI;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ReaderUseCaseHandler extends MainFrame {
	/**
	 * 
	 * @return a panel that show all existing journal
	 */
	public static JPanel createReadJournalPanel() {
		List<Journal> journals = DatabaseHandler.getJournals();
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		largePanel.add(new JLabel("select a journal below"));
		JPanel journalPanel = new JPanel();
		journalPanel.setLayout(new GridLayout(0,1));
		largePanel.add(journalPanel);
		for(Journal journal : journals) {
			JButton button = new JButton(journal.name);
			button.addActionListener((event)->{
				changePanel(createReadVolumePanel(journal));
			});
			journalPanel.add(button);
		}
		return largePanel;
	}
	/**
	 * 
	 * @param journal
	 * @return a panel that show all existing volume in that journal
	 */
	public static JPanel createReadVolumePanel(Journal journal) {
		List<Integer> volumes = DatabaseHandler.getVolumes(journal.issn);
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		largePanel.add(new JLabel("select a volume below"));
		JPanel volumePanel = new JPanel();
		volumePanel.setLayout(new GridLayout(0,1));
		largePanel.add(volumePanel);
		for(Integer volume : volumes) {
			JButton button = new JButton(volume.toString());
			button.addActionListener((event)->{
				changePanel(createReadEditionPanel(journal, volume));
			});
			volumePanel.add(button);
		}
		return largePanel;
	}

	/**
	 * 
	 * @param journal
	 * @return a panel that show all existing edition in that volume of the journal
	 */
	public static JPanel createReadEditionPanel(Journal journal, int volume) {
		List<Integer> editions = DatabaseHandler.getEditions(journal.issn, volume);
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		largePanel.add(new JLabel("select an edition below"));
		JPanel editionPanel = new JPanel();
		editionPanel.setLayout(new GridLayout(0,1));
		largePanel.add(editionPanel);
		for(Integer edition : editions) {
			JButton button = new JButton(edition.toString());
			button.addActionListener((event)->{
				changePanel(createReadArticlePanel(journal, volume, edition));
			});
			editionPanel.add(button);
		}
		return largePanel;
	}
	/**
	 * 
	 * @param journal
	 * @param volume
	 * @param edition
	 * @return  a panel that show all existing article in that edition of that volume of the journal
	 */
	public static JPanel createReadArticlePanel(Journal journal, int volume, int edition) {
		List<Article> articles = DatabaseHandler.getArticles(journal.issn, volume, edition);
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		largePanel.add(new JLabel("select an article below"));
		JPanel articlePanel = new JPanel();
		articlePanel.setLayout(new GridLayout(0,1));
		largePanel.add(articlePanel);
		for(Article article : articles) {
			JButton button = new JButton(article.work.title);
			button.addActionListener((event)->{
				changePanel(createArticlePanel(article));
			});
			articlePanel.add(button);
		}
		return largePanel;
	}
	/**
	 * 
	 * @param article
	 * @return show all infos about the article, including the title, abstract and author, 
	 * and a button that allow user to download the pdf 
	 */
	public static JPanel createArticlePanel(Article article) {
		List<Author> authors = DatabaseHandler.getAuthors(article.work.workID);
		Author corrAuthor = DatabaseHandler.getCorrespondingAuthor(article.work.workID);
		JPanel largePanel = new JPanel();
		largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
		JPanel articlePanel = new JPanel();
		articlePanel.setLayout(new GridLayout(0,2));
		largePanel.add(articlePanel);
		
		articlePanel.add(new JLabel("title :"));
		articlePanel.add(new JLabel(article.work.title));
		articlePanel.add(new JLabel("abstract :"));
		articlePanel.add(new JLabel(article.work._abstract));
		articlePanel.add(new JLabel("pdf :"));
		articlePanel.add(downloadButton(article.work));
		articlePanel.add(new JLabel("at page :"));
		articlePanel.add(new JLabel(""+article.pageNum));
		articlePanel.add(new JLabel("corresponding author:"));
		articlePanel.add(new JLabel(corrAuthor.getName()));
		String otherAuthors = "";
		for(Author auth : authors) {
			if(corrAuthor.getName().equals(auth.getName())) {
				continue;
			}
			otherAuthors+=auth.getName()+", ";
		}
		articlePanel.add(new JLabel("other author(s):"));
		articlePanel.add(new JLabel(otherAuthors));
		return largePanel;
	}

}
