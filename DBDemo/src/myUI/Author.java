package myUI;

public class Author {
	
	private int authorID;
	public String title;
	public String forename;
	public String surname;
	public String affiliation;
	public String email;
	public String password;
	
	public Author(String name, String email, int id) {
		this.forename = name;
		this.email = email;
		authorID = id;
	}
	public Author(String title, String forename, String surname, String affliation,
			String email, String password) {
		this.title = title;
		this.forename = forename;
		this.surname = surname;
		this.affiliation = affliation;
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}
	public boolean signup() {
		boolean newAccount= DatabaseHandler.signUp(email, password, title, forename, surname, affiliation);
		try {
			DatabaseHandler.setReviewer(email);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newAccount;
	}
	public String getName() {
		return title+" "+forename+" "+surname;
	}

}