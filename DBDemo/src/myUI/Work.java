package myUI;

import java.util.ArrayList;

public class Work {
	public int workID;
	public String title;
	public String _abstract;
	public String pdf;
	public int state;
	public int issn;
	public Work(int workID, String title, String _abstract, String pdf, int state) {
		this.workID = workID;
		this.title = title;
		this._abstract = _abstract;
		this.pdf = pdf;
		this.state = state;
	}

	public Work(String title, String _abstract, String pdf, int issn) {
		this.workID = -1;
		this.title = title;
		this._abstract = _abstract;
		this.pdf = pdf;
		this.issn = issn;
		this.state = 0;
	}
	public Author mainAuthor;
	public ArrayList<Author> authors = new ArrayList<Author>();
	
}
