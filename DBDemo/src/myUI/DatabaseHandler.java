package myUI;
import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;


public class DatabaseHandler {
	public static void main(String[] args) throws Exception{

		showAllWorks();
		showUsers();
		DatabaseHandler.signUp("test1","pw","mr","first name","last","und");
//		DatabaseHandler.setAuthor("test1");
		DatabaseHandler.signUp("test2","pw","ms","first name2","last2","post");
//		DatabaseHandler.setAuthor("test2");

//		DatabaseHandler.setEditor("test1");
		DatabaseHandler.setReviewer("test1");
		String[] b = new String[] {};
		DatabaseHandler.createJounral(1,"name","test1",Arrays.asList(b));
		String[] a = new String[] {"test2"};
		DatabaseHandler.addWork(1,"test1",Arrays.asList(a));
		
		
		System.out.println(DatabaseHandler.addVerdict(1, 1, 1, 1));

		System.out.println(getAuthors(1));
	}
	public static void showUsers() throws Exception {
		try(Connection con = connect()){
			 Statement stmt = con.createStatement();
			 ResultSet res = stmt.executeQuery("Select * FROM account");
			 while(res.next()) {
//				 int index = res.getInt(1);
				 String email = res.getString(1);
				 System.out.println("users:"+email);
			 }
			 res.close();
		}
	}
	
	public static Connection connect() throws Exception {
		DriverManager.setLoginTimeout(5);
		String url = "jdbc:mysql://stusql.dcs.shef.ac.uk/team014";
		String user = "team014";
		String password = "80019d16";
		return DriverManager.getConnection(url, user, password);
	}

	public static boolean logIn(String email, String password) {
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
		catch(Exception e) {
			
		}
		return false;
	}
	public static boolean signUp(String email, String password, String title, String firstName, String lastName, String affiliation) {
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
	public static boolean changePassword(String email, String newPassword) {
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

	public static boolean createJounral(int issn, String name, String chiefEditor, List<String> otherEditor) {
		try(Connection con = connect()){
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO journal (issn, name) VALUES (?,?)");
			pstmt.setInt(1, issn);
			pstmt.setString(2, name);
			pstmt.execute();
			PreparedStatement pstmt2 = con.prepareStatement(
					 "INSERT INTO editing (email, issn, become_chief_editor) VALUES (?,?,?)");

			for(String editor : otherEditor) {
				pstmt2.setString(1, editor);
				pstmt2.setInt(2, issn);
				pstmt2.setInt(3, 0);
				pstmt2.addBatch();
			}			
			pstmt2.setString(1, chiefEditor);
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
	public static int addWork(int issn, Author correspondingAuthor, List<Author> authors) throws Exception {
		List<String> otherAuthors = new ArrayList<String>();
		for(Author au : authors) {
			otherAuthors.add(au.email);
		}
		
		return addWork(issn,correspondingAuthor.email,otherAuthors);
	}
	
	public static int addWork(int issn, String correspondingAuthor, List<String> otherAuthor) throws Exception {
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
			System.out.println(index);
			PreparedStatement pstmt2 = con.prepareStatement(
					 "INSERT INTO authoring (email, workID, is_corresponding_author) VALUES (?,?,?)");
			for(String email : otherAuthor) {
				pstmt2.setString(1, email);
				pstmt2.setInt(2, index);
				pstmt2.setInt(3, 0);
				pstmt2.addBatch();
			}			
			pstmt2.setString(1, correspondingAuthor);
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
	public static void addAuthoring(int workID, String newAuthor) throws Exception {

		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO authoring (email, workID, is_corresponding_author) VALUES (?,?,?)");
			pstmt.setString(1, newAuthor);
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
	public static void removeAuthoring(int workID, String author) throws Exception {

		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "DELETE FROM authoring WHERE email=? AND workID=? AND is_corresponding_author=0 ");
			pstmt.setString(1, author);
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
	public static void removeReviewer(int reviewerID) {

		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "DELETE FROM reviewer WHERE reviewerID=?");
			pstmt.setInt(1, reviewerID);
			pstmt.execute();
			System.out.println("an reviewer is removed");
		}
		catch(Exception e) {
			System.out.println("remove editor fail");
		}
	}

	public static boolean addEditing(int issn, String newEditor){

		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO editing (email, issn, become_chief_editor) VALUES (?,?,?)");
			pstmt.setString(1, newEditor);
			pstmt.setInt(2, issn);
			pstmt.setInt(3, 0);
			pstmt.execute();
			System.out.println("a new editor is added");
			return true;
		}
		catch(Exception e) {
			System.out.println("add editor to work"+issn+"fail");
			return false;
		}
	}
	public static void removeEditing(int issn, String editor) {

		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "DELETE FROM editing WHERE email=? AND issn=? AND become_chief_editor=0 ");
			pstmt.setString(1, editor);
			pstmt.setInt(2, issn);
			pstmt.execute();
			System.out.println("an editor is removed");
		}
		catch(Exception e) {
			System.out.println("remove editing fail, maybe because "
					+ "given editor is the chief author, or the "
					+ "given editor is not the editor of the jounral");
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
	public static List<Journal> getJournals() {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT journal.issn,journal.name FROM journal");
			ResultSet res = pstmt.executeQuery();
			List<Journal> issns = new ArrayList<Journal>();
			while(res.next()) {
				int issn = res.getInt(1);
				String name = res.getString(2);
				issns.add(new Journal(issn, name));
			}
			res.close();
			System.out.println("returning a list of jounrals");
			return issns;
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}
	public static List<Integer> getVolumes(int issn) {
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
			res.close();
			System.out.println("returning list of volumes");
			return volumes;
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}
	public static List<Integer> getEditions(int issn, int volume){
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
			res.close();
			System.out.println("returning list of editions");
			return editions;
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}
	public static List<Article> getArticles(int issn, int volume, int number){
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT page_start,page_end,work.workID,submission.title,"
					 + "submission.abstract,submission.pdf FROM article,work,submission "
					 + "WHERE article.issn=? AND article.volume=? AND article.number=? "
					 + "AND article.workID=work.workID AND submission.workID=work.workID");
			pstmt.setInt(1, issn);
			pstmt.setInt(2, volume);
			pstmt.setInt(3, number);
			ResultSet res = pstmt.executeQuery();
			List<Article> articles = new ArrayList<Article>();
			while(res.next()) {
				int pageStart = res.getInt(1);
				int pageEnd = res.getInt(2);
				int workID = res.getInt(3);
				String title = res.getString(4);
				String _abstract = res.getString(5);
				byte[] fileBytes = res.getBytes(6);
				//int pdf = res.getInt(3);
				Work work = new Work(title,_abstract,fileBytes,issn);
				work.workID = workID;
				articles.add(new Article(work,pageStart,pageEnd));
			}
			res.close();
			System.out.println("returning list of articles");
			return articles;
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}
	public static boolean isAuthor(String email) {
		return isRole(email, "authoring");
	}
	private static boolean isRole(String email, String role) {
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
			System.out.println(count);
			return count > 0;
			
		}
		catch(Exception e) {
			System.out.println("check "+email+" is "+role+" fail");
		}
		return false;
	}
	
	public static boolean isEditor(String email) {
		return isRole(email, "editing");
	}
	public static boolean isReviewer(String email) {
		return isRole(email, "reviewer");
	}
	private static void removeRole(String email, String role) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "DELETE FROM "+role+" WHERE email=?");
			pstmt.setString(1, email);
			pstmt.execute();
			
		}
		catch(Exception e) {
			System.out.println("check "+email+" is "+role+" fail");
		}
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
//	public static void setAuthor(String email) throws Exception {
//		setRole(email,"author");
//	}
//	public static void setEditor(String email) throws Exception {
//		setRole(email,"editor");
//	}
	public static void setReviewer(String email) throws Exception {
		setRole(email,"reviewer");
	}
	public static ArrayList<Author> getAuthors(int workID) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(""
					+ "SELECT account.email,title, forename, surname,affiliation FROM account "
					+ "JOIN authoring ON account.email = authoring.email "
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
				System.out.println("users:"+email+",workID:"+workID);
//				System.out.println("users:"+index+",workID:"+workID);;
				authors.add(new Author(title,forename,surname,affiliation,email,""));
			}
			res.close();
			return authors;
		}
		catch(Exception e) {
			
		}
		return new ArrayList<Author>();
	}
	public static Author getCorrespondingAuthor(int workID) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(""
					+ "SELECT account.email,title, forename, surname,affiliation FROM account "
					+ "JOIN authoring ON authoring.email = account.email "
					+ "WHERE authoring.workID=? "
					+ "AND authoring.is_corresponding_author=1"
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
				System.out.println("user:"+email+", workID:"+workID);
				correspondingAuthor = new Author(title,forename,surname,affiliation,email,"");
			}
			res.close();
			return correspondingAuthor;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void changeCorrespondingAuthor(int workID, String newAuthor) throws Exception {

		try(Connection con = connect()){
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement(
					 "UPDATE authoring SET is_corresponding_author=0 "
					 + "WHERE workID=? AND is_corresponding_author=1");
			pstmt.setInt(1, workID);
			pstmt.execute();

			PreparedStatement pstmt2 = con.prepareStatement(
					 "UPDATE authoring SET is_corresponding_author=1 "
					 + "WHERE workID=? AND email=?");
			pstmt2.setInt(1, workID);
			pstmt2.setString(2, newAuthor);
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
			res.close();
		}
		catch(Exception e) {
		}
	}

	public static boolean addSubmision(int workID, String title, String abstract_,File pdf, boolean isFirstSubmission) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO submission (workID, submissionID, title, abstract, pdf) VALUES (?,?,?,?,?)");
			pstmt.setInt(1, workID);
			int submissionID = isFirstSubmission?1:2;
			pstmt.setInt(2, submissionID);
			pstmt.setString(3, title);
			pstmt.setString(4, abstract_);
			FileInputStream fis = new FileInputStream(pdf);
			pstmt.setBinaryStream(5, fis);
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
			res.close();
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
			res.close();
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

	public static ArrayList<String> getEditors(int issn) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(""
					+ "SELECT account.email, title, forename, surname, affiliation FROM account "
					+ "JOIN editing ON account.email = editing.email "
					+ "WHERE editing.issn=?"
					+ "");
			pstmt.setInt(1, issn);
			ResultSet res = pstmt.executeQuery();
			ArrayList<String> editors = new ArrayList<String>();
			while(res.next()) {
				String email = res.getString(1);
				String title = res.getString(2);
				String forename = res.getString(3);
				String surname = res.getString(4);
				String affiliation = res.getString(5);
				System.out.println("editors:"+email+",issn:"+issn);
				editors.add(email);
			}
			res.close();
			return editors;
		}
		catch(Exception e) {
			
		}
		return new ArrayList<String>();
	}
	public static String getChiefEditor(int issn) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(""
					+ "SELECT account.email, title, forename, surname, affiliation FROM account "
					+ "JOIN editing ON account.email = editing.email "
					+ "WHERE editing.issn=? "
					+ "AND editing.become_chief_editor=1"
					+ "");
			pstmt.setInt(1, issn);
			ResultSet res = pstmt.executeQuery();
			String chiefEditor = null;
			while(res.next()) {
				String email = res.getString(1);
				String title = res.getString(2);
				String forename = res.getString(3);
				String surname = res.getString(4);
				String affiliation = res.getString(5);
				System.out.println("editors:"+email+",issn:"+issn);
				chiefEditor = email;
			}
			res.close();
			return chiefEditor;
		}catch(Exception e) {
			
		}
		return "";
	}
	public static boolean changeChiefEditor(int issn, String newEditor) {
		try(Connection con = connect()){
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement(
					 "UPDATE editing SET become_chief_editor=0 "
					 + "WHERE issn=? AND become_chief_editor=1");
			pstmt.setInt(1, issn);
			pstmt.execute();

			PreparedStatement pstmt2 = con.prepareStatement(
					 "UPDATE editing SET become_chief_editor=1 "
					 + "WHERE issn=? AND email=?");
			pstmt2.setInt(1, issn);
			pstmt2.setString(2, newEditor);
			pstmt2.execute();
			con.commit();
			return true;
		}
		catch(Exception e) {
			return false;
		}

	}
	public static List<Work> getWorks(String email) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT work.workID,issn,MAX(submission.submissionID),submission.title,"
					 + "submission.abstract,submission.pdf FROM work "
					 + ",submission,authoring WHERE work.workID=submission.workID"
					 + " AND authoring.workID=work.workID AND authoring.email=?"
					 + " GROUP BY work.workID");
			pstmt.setString(1, email);
			ResultSet res = pstmt.executeQuery();
			List<Work> works = new ArrayList<Work>();
			while(res.next()) {
				int workID = res.getInt(1);
				int issn = res.getInt(2);
				int submissionID = res.getInt(3);
				String title = res.getString(4);
				String _abstract = res.getString(5);
				byte[] fileBytes = res.getBytes(6);			
				works.add(new Work(workID, title, _abstract, fileBytes, submissionID));

			}
			res.close();
			return works;
		}
		catch(Exception e) {
		}
		return new ArrayList<Work>();
	}
	public static List<Work> getWorksReview(String email) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT work.workID,issn,MAX(submission.submissionID),submission.title,"
					 + "submission.abstract,submission.pdf FROM work "
					 + ",submission,authoring WHERE work.workID=submission.workID"
					 + " AND authoring.workID=work.workID AND authoring.email<>?"
					 + " GROUP BY work.workID");
			pstmt.setString(1, email);
			ResultSet res = pstmt.executeQuery();
			List<Work> works = new ArrayList<Work>();
			while(res.next()) {
				int workID = res.getInt(1);
				int issn = res.getInt(2);
				int submissionID = res.getInt(3);
				String title = res.getString(4);
				String _abstract = res.getString(5);
				byte[] fileBytes = res.getBytes(6);
				works.add(new Work(workID, title, _abstract, fileBytes, submissionID));

			}
			res.close();
			return works;
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return new ArrayList<Work>();
	}
	public static List<Integer> getJournals(String email) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT issn FROM editing WHERE email=?");
			pstmt.setString(1, email);
			ResultSet res = pstmt.executeQuery();
			List<Integer> issns = new ArrayList<Integer>();
			while(res.next()) issns.add(res.getInt(1));
			res.close();
			return issns;
		}
		catch(Exception e) {
		}
		return new ArrayList<Integer>();
	}
	public static int getReviewerID(String email) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT reviewerID FROM reviewer WHERE email=? ");
			pstmt.setString(1, email);
			ResultSet res = pstmt.executeQuery();
			
			while(res.next()) {
				int id = res.getInt(1);
				res.close();
				return id;
			}
			res.close();
		}
		catch(Exception e) {
		}
		return -1;
	}
	public static String getReview(int reviewerID, int workID) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT review FROM review WHERE reviewerID=? AND workID=?");
			pstmt.setInt(1, reviewerID);
			pstmt.setInt(2, workID);
			ResultSet res = pstmt.executeQuery();
			
			while(res.next()) {
				String review = res.getString(1);
				res.close();
				return review;
			}
			res.close();
		}
		catch(Exception e) {}
		return "";
	}
	public static List<Review> getReviewsAndVerdicts(int workID) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT review.review,verdict.verdictID,review.reviewerID,MAX(review.submissionID) FROM review,verdict WHERE "
					 + "verdict.reviewerID=review.reviewerID AND verdict.workID=review.workID"
					 + " AND review.workID=? GROUP BY review.reviewerID");
			pstmt.setInt(1, workID);
			
			ResultSet res = pstmt.executeQuery();
			List<Review> reviews = new ArrayList<Review>();
			int index = 1;
			while(res.next()) {
				String reviewstr = res.getString(1);
				int verdictID = res.getInt(2);
				int reviewerID = res.getInt(3);
				Review review = new Review(index,reviewstr,verdictID, reviewerID);
				reviews.add(review);
				index++;
			}
			res.close();
			return reviews;
		}
		catch(Exception e) {System.out.println(e);}
		return new ArrayList<Review>();
	}
	public static int getVerdict(int reviewerID, int workID, int submissionID) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT verdictID, MAX(submissionID) FROM verdict "
					 + "WHERE reviewerID=? AND workID=? AND submissionID=?");
			pstmt.setInt(1, reviewerID);
			pstmt.setInt(2, workID);
			pstmt.setInt(3, submissionID);
			ResultSet res = pstmt.executeQuery();
			
			while(res.next()) {
				int verdictID = res.getInt(1);
				res.close();
				return verdictID;
			}
			res.close();
		}
		catch(Exception e) {
		}
		return -1;
	}
	
	
	public static String getResponse(int reviewerID, int workID) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT response FROM response WHERE reviewerID=? AND workID=?");
			pstmt.setInt(1, reviewerID);
			pstmt.setInt(2, workID);
			ResultSet res = pstmt.executeQuery();
			
			while(res.next()) {
				String response = res.getString(1);
				res.close();
				return response;
			}
			res.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return "";
	}
	public static int getReviewCount(int reviewerID) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT COUNT(*) FROM verdict WHERE reviewerID=? AND submissionID=2");
			pstmt.setInt(1, reviewerID);
			ResultSet res = pstmt.executeQuery();
			
			while(res.next()) {
				int count = res.getInt(1);
				res.close();
				return count;
			}
			res.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return -1;
	}
	
	public static List<Work> getSubmission(int issn){
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT work.workID,MAX(submission.submissionID),submission.title,"
					 + "submission.abstract,submission.pdf FROM work LEFT JOIN article ON work.workID=article.workID"
					 + ",submission,authoring "
					 + "WHERE work.workID=submission.workID "
					 + "AND authoring.workID=work.workID AND work.issn=? "
					 + "AND article.workID IS NULL"
					 + " GROUP BY work.workID");
			pstmt.setInt(1, issn);
			ResultSet res = pstmt.executeQuery();
			List<Work> works = new ArrayList<Work>();
			while(res.next()) {
				int workID = res.getInt(1);
//				int issn = res.getInt(2);
				int submissionID = res.getInt(2);
				String title = res.getString(3);
				String _abstract = res.getString(4);
				byte[] fileBytes = res.getBytes(5);
				works.add(new Work(workID, title, _abstract, fileBytes, submissionID));

			}
			res.close();
			return works;
		} catch (Exception e) {
			System.out.println(e);
		}
		return new ArrayList<Work>();
	}
	public static void rejectWork(int workID) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					"DELETE FROM work WHERE workID=?");
			pstmt.setInt(1, workID);
			pstmt.execute();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
}
