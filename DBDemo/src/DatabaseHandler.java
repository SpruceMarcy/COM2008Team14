import java.sql.*;

import java.util.ArrayList;
import java.util.*;


public class DatabaseHandler {
	public static void main(String[] args) throws Exception{
		//System.out.println(createWork(new UserInfo(), new ArrayList<UserInfo>()));
		showAllWorks();
		showUsers();
		//uploadSubmision(new Submission("test title","test abstract"));
		signUp("test1","pw");
		setAuthor("test1");
		signUp("test2","pw");
		setAuthor("test2");
		//createWork(1,new ArrayList<Integer>());
		Integer[] a = new Integer[] {2};
		createWork(1,Arrays.asList(a));
		getCorrespondingAuthor(1);
		ChangeCorrespondingAuthor(1, 2);
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
	public static int createWork(Integer correspondingAuthor, List<Integer> otherAuthor) throws Exception {
		try(Connection con = connect()){
			con.setAutoCommit(false); 
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO work () VALUES ()");
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
			//return -1;
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
	public static void ChangeCorrespondingAuthor(int workID, int newAuthor) throws Exception {

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

	public static boolean uploadSubmision(int workID, String title, String abstract_, boolean isFirstSubmission) throws Exception {
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
	
	public static boolean uploadSubmision(Submission submission) throws Exception {
		return uploadSubmision(submission.workID,submission.title,submission.abstract_,true);
	}

	public static boolean uploadVerdict(int workID, int submissionID, int reviewerID, int verdictID) throws Exception {
		/**
		 * reviewerID is the authorID who review this submission
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO verdict (authorID, workID, submissionID, verdictID) VALUES (?,?,?,?)");
			pstmt.setInt(1, reviewerID);
			pstmt.setInt(2, workID);
			pstmt.setInt(3, submissionID);
			pstmt.setInt(4, verdictID);
			pstmt.execute();
			System.out.println("a new verdict is created successfully");
			return true;
		}
		catch(Exception e) {
			System.out.println("create verdict fail");
			return false;
		}
	}

	public static boolean uploadReview(int workID, int submissionID, int reviewerID, String review) throws Exception {
		/**
		 * reviewerID is the authorID who review this submission
		 */
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO review (authorID, workID, submissionID, review) VALUES (?,?,?,?)");
			pstmt.setInt(1, reviewerID);
			pstmt.setInt(2, workID);
			pstmt.setInt(3, submissionID);
			pstmt.setString(4, review);
			pstmt.execute();
			System.out.println("a new review is created successfully");
			return true;
		}
		catch(Exception e) {
			System.out.println("create review fail");
			return false;
		}
	}
}
