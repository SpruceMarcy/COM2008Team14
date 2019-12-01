import java.sql.*;
import java.util.*;


public class DatabaseHandler {
	public static void main(String[] args) throws Exception{

		showAllWorks();
		showUsers();
		DatabaseHandler.signUp("test1","pw","mr","first name","last","und");
		DatabaseHandler.setAuthor("test1");
		DatabaseHandler.signUp("test2","pw","ms","first name2","last2","post");
		DatabaseHandler.setAuthor("test2");

		DatabaseHandler.setEditor("test1");
		DatabaseHandler.setReviewer("test1");
		Integer[] b = new Integer[] {};
		DatabaseHandler.createJounral(1,"name",1,Arrays.asList(b));
		Integer[] a = new Integer[] {2};
		DatabaseHandler.addWork(1,1,Arrays.asList(a));
		
		System.out.println(DatabaseHandler.addVerdict(1, 1, 1, 1));

		System.out.println(getAuthors(1));
	}
	public static void showUsers() throws Exception {
		try(Connection con = connect()){
			 Statement stmt = con.createStatement();
			 ResultSet res = stmt.executeQuery("Select * FROM author");
			 while(res.next()) {
				 int index = res.getInt(1);
				 String email = res.getString(2);
				 System.out.println("users:"+index+","+email);
			 }
			 res.close();
		}
	}
	
	public static Connection connect() throws Exception {
		String url = "jdbc:mysql://stusql.dcs.shef.ac.uk/team014";
		String user = "team014";
		String password = "80019d16";
		return DriverManager.getConnection(url, user, password);
	}

	public static boolean logIn(String email, String password) throws Exception {
		try(Connection con = connect()){
			//System.out.println("SELECT * FROM account WHERE email=? AND password=?");
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT * FROM account WHERE email=? AND password=?");
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			ResultSet res = pstmt.executeQuery();
			System.out.println(res.getFetchSize());
			while(res.next()) {
				res.close();
				return true;
			}
			res.close();
		}
		return false;
	}
	public static boolean signUp(String email, String password, String title, String firstName, String lastName, String affiliation) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO account (email, password, title, forename, surname, affiliation) VALUES (?,?,?,?,?,?)");
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.setString(3, title);
			pstmt.setString(4, firstName);
			pstmt.setString(5, lastName);
			pstmt.setString(6, affiliation);
			pstmt.execute();
			System.out.println("a new account is created successfully");
			return true;
		}
		catch(Exception e) {
			System.out.println("create account fail, maybe duplicate email");
			return false;
		}
	}
	public static boolean changePassword(String email, String newPassword) throws Exception {
		/**
		 * I tried to add a parameter called old password, and only update when old password match
		 * however, it would not return whether the old password is matched so it could not return if the password
		 * is changed. therefore, need to first check the password is matched first using signin method.
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "UPDATE account SET password=? WHERE email=?");
			pstmt.setString(1, newPassword);
			pstmt.setString(2, email);

			pstmt.execute();
			System.out.println("password is changed");
			return true;
		}
		catch(Exception e) {
			System.out.println("change password fail");
			return false;
		}
	}
	public static void testCreatWork(int issn) throws SQLException, Exception {
		try(Connection con = connect()){
			con.setAutoCommit(false); 
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO work (issn) VALUES (?)");
			pstmt.setInt(1, issn);
			pstmt.execute();
			con.commit();
		}
	}

	public static boolean createJounral(int issn, String name, Integer chiefEditor, List<Integer> otherEditor) {
		try(Connection con = connect()){
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO journal (issn, name) VALUES (?,?)");
			pstmt.setInt(1, issn);
			pstmt.setString(2, name);
			pstmt.execute();
			PreparedStatement pstmt2 = con.prepareStatement(
					 "INSERT INTO editing (editorID, issn, become_chief_editor) VALUES (?,?,?)");

			for(int authorID : otherEditor) {
				pstmt2.setInt(1, authorID);
				pstmt2.setInt(2, issn);
				pstmt2.setInt(3, 0);
				pstmt2.addBatch();
			}			
			pstmt2.setInt(1, chiefEditor);
			pstmt2.setInt(2, issn);
			pstmt2.setInt(3, 1);
			pstmt2.addBatch();
			pstmt2.executeBatch();
			
			
			con.commit();
			System.out.println("a new jounal is created successfully");
			return true;
		}
		catch(Exception e) {
			System.out.println("create journal fail");
			return false;
		}
	}
	public static int addWork(int issn, Integer correspondingAuthor, List<Integer> otherAuthor) throws Exception {
		/**
		 * return workID
		 */
		try(Connection con = connect()){
			con.setAutoCommit(false); 
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO work (issn) VALUES (?)");
			pstmt.setInt(1, issn);
			pstmt.execute();
		    Statement stmt = con.createStatement();
		    ResultSet res = stmt.executeQuery("SELECT LAST_INSERT_ID()");
		    int index = -1;
			while(res.next()) {
				index = res.getInt(1);
			}
			PreparedStatement pstmt2 = con.prepareStatement(
					 "INSERT INTO authoring (authorID, workID, is_corresponding_author) VALUES (?,?,?)");
			for(int authorID : otherAuthor) {
				pstmt2.setInt(1, authorID);
				pstmt2.setInt(2, index);
				pstmt2.setInt(3, 0);
				pstmt2.addBatch();
			}			
			pstmt2.setInt(1, correspondingAuthor);
			pstmt2.setInt(2, index);
			pstmt2.setInt(3, 1);
			pstmt2.addBatch();
			pstmt2.executeBatch();
			con.commit(); 
			System.out.println("a new work is created with authors");
			return index;
		}
		catch(Exception e) {
			System.out.println("create work fail");
			throw e;
		}
	}
	public static void addAuthoring(int workID, int newAuthor) throws Exception {

		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO authoring (authorID, workID, is_corresponding_author) VALUES (?,?,?)");
			pstmt.setInt(1, newAuthor);
			pstmt.setInt(2, workID);
			pstmt.setInt(3, 0);
			pstmt.execute();
			System.out.println("a new author is added");
		}
		catch(Exception e) {
			System.out.println("add author to work"+workID+"fail");
			throw e;
		}
	}
	public static void removeAuthoring(int workID, int author) throws Exception {

		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "DELETE FROM authoring WHERE authorID=? AND workID=? AND is_corresponding_author=0 ");
			pstmt.setInt(1, author);
			pstmt.setInt(2, workID);
			pstmt.execute();
			System.out.println("an author is removed");
		}
		catch(Exception e) {
			System.out.println("remove authoring fail, maybe because "
					+ "given author is the corresponding author, or the "
					+ "given author is not the author of the work");
			throw e;
		}
	}

	public static void addEditing(int issn, int newEditor) throws Exception {

		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO editing (editorID, issn, become_chief_editor) VALUES (?,?,?)");
			pstmt.setInt(1, newEditor);
			pstmt.setInt(2, issn);
			pstmt.setInt(3, 0);
			pstmt.execute();
			System.out.println("a new editor is added");
		}
		catch(Exception e) {
			System.out.println("add editor to work"+issn+"fail");
			throw e;
		}
	}
	public static void removeEditing(int issn, int editor) throws Exception {

		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "DELETE FROM editing WHERE editorID=? AND issn=? AND become_chief_editor=0 ");
			pstmt.setInt(1, issn);
			pstmt.setInt(2, editor);
			pstmt.execute();
			System.out.println("an editor is removed");
		}
		catch(Exception e) {
			System.out.println("remove editing fail, maybe because "
					+ "given editor is the chief author, or the "
					+ "given editor is not the editor of the jounral");
			throw e;
		}
	}
	public static void addVolumne(int issn, int newVolume) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO volume (issn,volume) VALUES (?,?)");
			pstmt.setInt(1, issn);
			pstmt.setInt(2, newVolume);
			pstmt.execute();
			System.out.println("new volume"+newVolume+" is added to "+issn);
		}
		catch(Exception e) {
			System.out.println("newVolume added fail");
			throw e;
		}
	}
	public static void addEdition(int issn, int volume, int newEdition) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO edition (issn,volume,number) VALUES (?,?,?)");
			pstmt.setInt(1, issn);
			pstmt.setInt(2, volume);
			pstmt.setInt(3, newEdition);
			pstmt.execute();
			System.out.println("new edition"+newEdition+" is added to "+issn+"/"+volume);
		}
		catch(Exception e) {
			System.out.println("new edition added fail");
			throw e;
		}
	}
	public static void addArticle(int issn, int volume, int edition,int page_start, int page_end, int workID) throws Exception {
		/**
		 * workID is the article that is not there
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO article (issn,volume,number,page_start,page_end,workID) "
					 + "VALUES (?,?,?,?,?,?)");
			pstmt.setInt(1, issn);
			pstmt.setInt(2, volume);
			pstmt.setInt(3, edition);
			pstmt.setInt(4, page_start);
			pstmt.setInt(5, page_end);
			pstmt.setInt(6, workID);
			pstmt.execute();
			System.out.println("new work"+workID+" is added to "+issn+"/"+volume+"/"+edition+" "
					+ "at page"+page_start+" to "+page_end);
		}
		catch(Exception e) {
			System.out.println("new article add fail");
			throw e;
		}
	}
	public static List<Integer> getJournals() throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT * FROM journal");
			ResultSet res = pstmt.executeQuery();
			List<Integer> issns = new ArrayList<Integer>();
			while(res.next()) {
				int issn = res.getInt(1);
				String name = res.getString(2);
				issns.add(issn);
			}
			System.out.println("returning a list of jounrals");
			return issns;
		}
		catch(Exception e) {
			System.out.println("");
			throw e;
		}
	}
	public static List<Integer> getVolumes(int issn) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT volume FROM volume WHERE volume.issn=?");
			pstmt.setInt(1, issn);
			ResultSet res = pstmt.executeQuery();
			List<Integer> volumes = new ArrayList<Integer>();
			while(res.next()) {
				int volume = res.getInt(1);
				volumes.add(volume);
			}
			System.out.println("returning list of volumes");
			return volumes;
		}
		catch(Exception e) {
			throw e;
		}
	}
	public static List<Integer> getEditions(int issn, int volume) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT number FROM edition WHERE edition.issn=? AND edition.volume=?");
			pstmt.setInt(1, issn);
			pstmt.setInt(2, volume);
			ResultSet res = pstmt.executeQuery();
			List<Integer> editions = new ArrayList<Integer>();
			while(res.next()) {
				int edition = res.getInt(1);
				editions.add(edition);
			}
			System.out.println("returning list of editions");
			return editions;
		}
		catch(Exception e) {
			throw e;
		}
	}
	public static List<Integer> getArticles(int issn, int volume, int number) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT page_start,page_end,workID FROM article "
					 + "WHERE article.issn=? AND article.volume=? AND article.number=?");
			pstmt.setInt(1, issn);
			pstmt.setInt(2, volume);
			pstmt.setInt(3, number);
			ResultSet res = pstmt.executeQuery();
			List<Integer> articles = new ArrayList<Integer>();
			while(res.next()) {
				int pageStart = res.getInt(1);
				int pageEnd = res.getInt(2);
				int workID = res.getInt(3);
				articles.add(workID);
			}
			System.out.println("returning list of articles");
			return articles;
		}
		catch(Exception e) {
			throw e;
		}
	}
	public static boolean isAuthor(String email) throws Exception {
		return isRole(email, "author");
	}
	private static boolean isRole(String email, String role) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT COUNT(*) FROM "+role+" WHERE email=?");
			pstmt.setString(1, email);
			ResultSet res = pstmt.executeQuery();
			int count = 0;
			while(res.next()) {
				count = res.getInt(1);
			}
			res.close();
			System.out.println("checking "+email+" is "+role);
			return count > 0;
			
		}
		catch(Exception e) {
			System.out.println("check "+email+" is "+role+" fail");
			throw e;
		}
	}
	
	public static boolean isEditor(String email) throws Exception {
		return isRole(email, "editor");
	}
	public static boolean isReviewer(String email) throws Exception {
		return isRole(email, "reviewer");
	}
	private static void setRole(String email, String role) throws Exception{
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO "+role+" (email) VALUES (?)");
			pstmt.setString(1, email);
			pstmt.execute();
			System.out.println(email+" is set to "+role);
		}
		catch(Exception e) {
			System.out.println("email "+ email+" not exist? ");
			throw e;
		}
	}
	public static void setAuthor(String email) throws Exception {
		setRole(email,"author");
	}
	public static void setEditor(String email) throws Exception {
		setRole(email,"editor");
	}
	public static void setReviewer(String email) throws Exception {
		setRole(email,"reviewer");
	}
	public static ArrayList<Author> getAuthors(int workID) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(""
					+ "SELECT account.email,title, forename, surname,affiliation,author.authorID FROM account "
					+ "JOIN author ON account.email=author.email "
					+ "JOIN authoring ON author.authorID = authoring.authorID "
					+ "WHERE authoring.workID=?"
					+ "");
			pstmt.setInt(1, workID);
			ResultSet res = pstmt.executeQuery();
			ArrayList<Author> authors = new ArrayList<Author>();
			while(res.next()) {
				String email = res.getString(1);
				String title = res.getString(2);
				String forename = res.getString(3);
				String surname = res.getString(4);
				String affiliation = res.getString(5);
				int index = res.getInt(6);
				System.out.println("users:"+index+",workID:"+workID);
				authors.add(new Author(email,forename,index));
			}
			res.close();
			return authors;
		}
	}
	public static Author getCorrespondingAuthor(int workID) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(""
					+ "SELECT account.email,title, forename, surname,affiliation,author.authorID FROM account "
					+ "JOIN author ON account.email=author.email "
					+ "JOIN authoring ON author.authorID = authoring.authorID "
					+ "WHERE authoring.workID=? "
					+ "AND is_corresponding_author=1"
					+ "");
			pstmt.setInt(1, workID);
			ResultSet res = pstmt.executeQuery();
			Author correspondingAuthor = null;
			while(res.next()) {
				String email = res.getString(1);
				String title = res.getString(2);
				String forename = res.getString(3);
				String surname = res.getString(4);
				String affiliation = res.getString(5);
				int index = res.getInt(6);
				System.out.println("user:"+index+", workID:"+workID);
				correspondingAuthor = new Author(email,forename,index);
			}
			res.close();
			return correspondingAuthor;
		}
	}
	public static void changeCorrespondingAuthor(int workID, int newAuthor) throws Exception {

		try(Connection con = connect()){
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement(
					 "UPDATE authoring SET is_corresponding_author=0 "
					 + "WHERE workID=? AND is_corresponding_author=1");
			pstmt.setInt(1, workID);
			pstmt.execute();

			PreparedStatement pstmt2 = con.prepareStatement(
					 "UPDATE authoring SET is_corresponding_author=1 "
					 + "WHERE workID=? AND authorID=?");
			pstmt2.setInt(1, workID);
			pstmt2.setInt(2, newAuthor);
			pstmt2.execute();
			con.commit();
		}
	}
	
	
	public static void showAllWorks() {
		try(Connection con = connect()){
		    Statement stmt = con.createStatement();
		    ResultSet res = stmt.executeQuery("SELECT * FROM work");
			while(res.next()) {
				int index = res.getInt(1);
				System.out.println("work:"+index);
			}
		}
		catch(Exception e) {
		}
	}

	public static boolean addSubmision(int workID, String title, String abstract_, boolean isFirstSubmission) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO submission (workID, submissionID, title, abstract) VALUES (?,?,?,?)");
			pstmt.setInt(1, workID);
			int submissionID = isFirstSubmission?1:2;
			pstmt.setInt(2, submissionID);
			pstmt.setString(3, title);
			pstmt.setString(4, abstract_);
			pstmt.execute();
			System.out.println("a new submission is created successfully, workID"+workID+", submissionID"+submissionID);
			return true;
		}
		catch(Exception e) {
			System.out.println("create submission fail");
			throw e;
		}
	}
	
	public static boolean hasEnoughReviews(int workID, int submissionID) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT COUNT(reviewerID) FROM verdict"
					 + " WHERE workID=? AND submissionID=? ");
			pstmt.setInt(1, workID);
			pstmt.setInt(2, submissionID);
			ResultSet res = pstmt.executeQuery();
			int count = 0;
			while(res.next()) {

				count = res.getInt(1);
			}
			System.out.println("there are "+count+" reviews");
			return count>=3;
		}
		catch(Exception e) {
			System.out.println("get review count fails on work:"+workID+", subm:"+submissionID);
			return false;
		}
	}
	public static boolean hasEnoughResponse(int workID, int submissionID) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT COUNT(response) FROM response "
					 + "JOIN review ON response.reviewerID = review.reviewerID "
					 + "AND response.workID=review.workID AND response.submissionID=review.submissionID"
					 + " WHERE response.workID=? AND response.submissionID=? ");
			pstmt.setInt(1, workID);
			pstmt.setInt(2, submissionID);
			ResultSet res = pstmt.executeQuery();
			int count = 0;
			while(res.next()) {
				count = res.getInt(1);
			}
			System.out.println("there are "+count+" response");
			return count>=3;
		}
		catch(Exception e) {
			System.out.println("get response count fails on work:"+workID+", subm:"+submissionID);
			throw e;
			// return false;
		}
	}
	
	public static boolean addVerdict(int workID, int submissionID, int reviewerID, int verdictID) throws Exception {
		/**
		 * only call this for second submission
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO verdict (reviewerID, workID, submissionID, verdictID) VALUES (?,?,?,?)"
					 + " ON DUPLICATE KEY UPDATE verdictID=?");
			pstmt.setInt(1, reviewerID);
			pstmt.setInt(2, workID);
			pstmt.setInt(3, submissionID);
			pstmt.setInt(4, verdictID);
			pstmt.setInt(5, verdictID);
			pstmt.execute();
			System.out.println("a new verdict is created successfully");
			return true;
		}
		catch(Exception e) {
			System.out.println("create verdict fail");
			return false;
		}
	}

	public static boolean addReview(int workID, int submissionID, int reviewerID, String review, int verdictID) throws Exception {
		/**
		 * call this at the first submission
		 */
		try(Connection con = connect()){
			
			con.setAutoCommit(false);
			
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO review (reviewerID, workID, submissionID, review) VALUES (?,?,?,?)"
					 + " ON DUPLICATE KEY UPDATE review=?");
			pstmt.setInt(1, reviewerID);
			pstmt.setInt(2, workID);
			pstmt.setInt(3, submissionID);
			pstmt.setString(4, review);
			pstmt.setString(5, review);
			pstmt.execute();
			

			PreparedStatement pstmt2 = con.prepareStatement(
					 "INSERT INTO verdict (reviewerID, workID, submissionID, verdictID) VALUES (?,?,?,?)"
					 + " ON DUPLICATE KEY UPDATE verdictID=?");
			pstmt2.setInt(1, reviewerID);
			pstmt2.setInt(2, workID);
			pstmt2.setInt(3, submissionID);
			pstmt2.setInt(4, verdictID);
			pstmt2.setInt(5, verdictID);
			pstmt2.execute();
			System.out.println("a new verdict is created successfully");
			
			System.out.println("a new review is created successfully");
			con.commit();
			
			return true;
		}
		catch(Exception e) {
			System.out.println("create review fail");
			return false;
		}
	}
	public static boolean addResponse(int workID, int submissionID, int reviewerID, String response) throws Exception {
		/**
		 * reviewerID is the authorID who review this submission
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO response (reviewerID, workID, submissionID, response) VALUES (?,?,?,?)"
					 + " ON DUPLICATE KEY UPDATE response=?");
			pstmt.setInt(1, reviewerID);
			pstmt.setInt(2, workID);
			pstmt.setInt(3, submissionID);
			pstmt.setString(4, response);
			pstmt.setString(5, response);
			pstmt.execute();																																																																																																																																																																																																																																																																																																																																																																							
			System.out.println("a new response is created successfully");
			return true;
		}
		catch(Exception e) {
			System.out.println("create response fail");
			return false;
		}
	}

	public static ArrayList<Integer> getEditors(int issn) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(""
					+ "SELECT account.email, title, forename, surname, affiliation, editor.editorID FROM account "
					+ "JOIN editor ON account.email=editor.email "
					+ "JOIN editing ON editor.editorID = editing.editorID "
					+ "WHERE editing.issn=?"
					+ "");
			pstmt.setInt(1, issn);
			ResultSet res = pstmt.executeQuery();
			ArrayList<Integer> editors = new ArrayList<Integer>();
			while(res.next()) {
				String email = res.getString(1);
				String title = res.getString(2);
				String forename = res.getString(3);
				String surname = res.getString(4);
				String affiliation = res.getString(5);
				int index = res.getInt(6);
				System.out.println("editors:"+index+",issn:"+issn);
				editors.add(index);
			}
			res.close();
			return editors;
		}
	}
	public static int getChiefEditor(int issn) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(""
					+ "SELECT account.email, title, forename, surname, affiliation, editor.editorID FROM account "
					+ "JOIN editor ON account.email=editor.email "
					+ "JOIN editing ON editor.editorID = editing.editorID "
					+ "WHERE editing.issn=? "
					+ "AND editing.become_chief_editor=1"
					+ "");
			pstmt.setInt(1, issn);
			ResultSet res = pstmt.executeQuery();
			int chiefEditor = -1;
			while(res.next()) {
				String email = res.getString(1);
				String title = res.getString(2);
				String forename = res.getString(3);
				String surname = res.getString(4);
				String affiliation = res.getString(5);
				int index = res.getInt(6);
				System.out.println("editors:"+index+",issn:"+issn);
				chiefEditor = index;
			}
			res.close();
			return chiefEditor;
		}
	}
	public static void changeChiefEditor(int issn, int newEditor) throws Exception {

		try(Connection con = connect()){
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement(
					 "UPDATE editing SET become_chief_editor=0 "
					 + "WHERE issn=? AND become_chief_editor=1");
			pstmt.setInt(1, issn);
			pstmt.execute();

			PreparedStatement pstmt2 = con.prepareStatement(
					 "UPDATE editing SET become_chief_editor=1 "
					 + "WHERE issn=? AND editorID=?");
			pstmt2.setInt(1, issn);
			pstmt2.setInt(2, newEditor);
			pstmt2.execute();
			con.commit();
		}
	}
}
