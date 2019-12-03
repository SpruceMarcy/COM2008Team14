package myUI;

public class Author extends User {
	
	private int authorId;
	
	public Author(String name, String email, int id) {
		super(name, email);
		authorId = id;
	}
	
	public int getId() {
		return authorId;
	}
}