package myUI;

public class Article {

	public Work work;
	
	public int pageNum;
	public int pageNumEnd;
	public Article(Work work, int pageStart, int pageEnd) {
		this.work = work;
		this.pageNum = pageStart;
		this.pageNumEnd = pageEnd;
	}
}
