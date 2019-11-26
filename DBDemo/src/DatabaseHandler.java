import java.sql.Connection;
import java.sql.*;

import java.util.ArrayList;

public class DatabaseHandler {
	public static void main(String[] args) throws Exception{
		/*
		System.out.println(logIn("happy","hi"));
		signUp("happy","hi");
		System.out.println(logIn("happy","hi"));
		*/
		System.out.println(createWork(new UserInfo()));
		//uploadSubmision(new Submission("test title","test abstract"));
		
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
	public static int createWork(UserInfo correspondingAuthor, ArrayList<UserInfo> otherAuthor) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO work () VALUES ()");
			pstmt.addBatch("SELECT LAST_INSERT_ID()");
			ResultSet res = pstmt.executeQuery();
			// pstmt.execute();
			System.out.println("a new submission is created successfully");
			return 1;
		}
		catch(Exception e) {
			System.out.println("create submission fail");
			return -1;
		}
	}
	
	
	public static boolean uploadSubmision(Submission submission) {
		try(Connection con = connect()){
			PreparedStatement pstmt = con.prepareStatement(
					 "INSERT INTO submission (workID, submissionID, title, abstract) VALUES (?,?)");
			pstmt.setInt(1, submission.workID);
			pstmt.setInt(2, 2);
			pstmt.setString(3, submission.title);
			pstmt.setString(4, submission.abstract_);
			pstmt.execute();
			System.out.println("a new submission is created successfully");
			return true;
		}
		catch(Exception e) {
			System.out.println("create submission fail");
			return false;
		}
	}

}
