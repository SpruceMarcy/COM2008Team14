package myUI;

public class Work {
	public int workID;
	public String title;
	public String _abstract;
	public String pdf;
	public int state;
	public Work(int workID, String title, String _abstract, String pdf, int state) {
		this.workID = workID;
		this.title = title;
		this._abstract = _abstract;
		this.pdf = pdf;
		this.state = state;
		
	}
}
