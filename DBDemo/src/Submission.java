import java.util.ArrayList;

public class Submission {
	
	public static void main(String[] args) {

	}
	// not sure how to handle the pdf at the moment
	public Submission(String title, String abstract_) {
		this.title = title;
		this.abstract_ = abstract_;
		
	}
	public int workID;
	public String title;
	public String abstract_;
	
	public ArrayList<Author> getAuthor() throws Exception {
		return DatabaseHandler.getAuthors(workID); //type mismatch should be fixed - getAuthor should return a list of authors
	}
}
