
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
	
	public ArrayList<> getAuthor() throws Exception {
		return DatabaseHandler.getAuthor(workID);
	}
	
	public String getTitle() {
		DatabaseHandler.get
	}
}
