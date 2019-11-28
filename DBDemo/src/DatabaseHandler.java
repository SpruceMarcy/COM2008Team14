import java.sql.*;
import java.util.*;


public class DatabaseHandler {
	public static void main(String[] args) throws Exception{

		showAllWorks();
		showUsers();
		signUp("test1","pw");
		setAuthor("test1");
		signUp("test2","pw");
		setAuthor("test2");
		setEditor("test1");
		Integer[] b = new Integer[] {};
		DatabaseHandler.createJounral(1,"name",1,Arrays.asList(b));
		Integer[] a = new Integer[] {2};
		DatabaseHandler.createWork(1,1,Arrays.asList(a));
	}
	public static void Test() {
		/**
		 * assume the database has just been initialized, unless this won't work
		 */
		
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
	public static boolean signUp(String email, String password) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO account (email, password) VALUES (?,?)");
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.execute();
			System.out.println("a new account is created successfully");
			return true;
		}
		catch(Exception e) {
			System.out.println("create account fail, maybe duplicate email");
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
	public static int createWork(int issn, Integer correspondingAuthor, List<Integer> otherAuthor) throws Exception {
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
	public static void setAuthor(String email) throws Exception {
		/**
		 * given an account email, add author function to it
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO author (email) VALUES (?)");
			pstmt.setString(1, email);
			pstmt.execute();
			System.out.println(email+" is set to author");
		}
		catch(Exception e) {
			System.out.println("email "+ email+" not exist? ");
			throw e;
		}
	}
	public static void setEditor(String email) throws Exception {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO editor (email) VALUES (?)");
			pstmt.setString(1, email);
			pstmt.execute();
			System.out.println(email+" is set to editor");
		}
		catch(Exception e) {
			System.out.println("editor "+ email+" not exist? ");
			throw e;
		}
	}
	public static ArrayList<Integer> getAuthor(int workID) throws Exception {
		/**
		 * return all authors related to a work
		 * (currently not returning anything as there are no author class)
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT authorID FROM authoring "
					 + "WHERE workID=? ");
			pstmt.setInt(1, workID);
			ResultSet res = pstmt.executeQuery();
			ArrayList<Integer> authors = new ArrayList<Integer>();
			while(res.next()) {
				int index = res.getInt(1);
				System.out.println("users:"+index+",workID:"+workID);
				authors.add(index);
			}
			res.close();
			return authors;
		}
	}
	public static Integer getCorrespondingAuthor(int workID) throws Exception {
		/**
		 * return all authors related to a work
		 * (currently not returning anything as there are no author class)
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "SELECT authorID FROM authoring "
					 + "WHERE workID=? "
					 + "AND is_corresponding_author=1");
			pstmt.setInt(1, workID);
			ResultSet res = pstmt.executeQuery();
			Integer correspondingAuthor = -1;
			while(res.next()) {
				int index = res.getInt(1);
				System.out.println("user:"+index+", workID:"+workID);
				correspondingAuthor = index;
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

	public static boolean createSubmision(int workID, String title, String abstract_, boolean isFirstSubmission) throws Exception {
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
	
		
	public static boolean uploadVerdict(int workID, int submissionID, int reviewerID, int verdictID) throws Exception {
		/**
		 * reviewerID is the authorID who review this submission
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO verdict (authorID, workID, submissionID, verdictID) VALUES (?,?,?,?)"
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

	public static boolean uploadReview(int workID, int submissionID, int reviewerID, String review, int verdictID) throws Exception {
		/**
		 * reviewerID is the authorID who review this submission
		 */
		try(Connection con = connect()){
			
			con.setAutoCommit(false);
			
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO review (authorID, workID, submissionID, review) VALUES (?,?,?,?)"
					 + " ON DUPLICATE KEY UPDATE review=?");
			pstmt.setInt(1, reviewerID);
			pstmt.setInt(2, workID);
			pstmt.setInt(3, submissionID);
			pstmt.setString(4, review);
			pstmt.setString(5, review);
			pstmt.execute();
			

			PreparedStatement pstmt2 = con.prepareStatement(
					 "INSERT INTO verdict (authorID, workID, submissionID, verdictID) VALUES (?,?,?,?)"
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
	public static boolean uploadResponse(int workID, int submissionID, int reviewerID, String response) throws Exception {
		/**
		 * reviewerID is the authorID who review this submission
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO response (authorID, workID, submissionID, response) VALUES (?,?,?,?)"
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
}
