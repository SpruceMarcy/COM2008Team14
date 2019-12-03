package myUI;

public class Review {
	public int reviewID;
	public String review;
	public int verdict;
	public int reviewerID;
	public Review(int reviewID,String review,int verdict, int reviewerID) {
		this.reviewID = reviewID;
		this.review = review;
		this.verdict = verdict;
		this.reviewerID = reviewerID;
	}
}
