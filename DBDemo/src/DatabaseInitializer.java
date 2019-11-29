import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;

import java.util.*;

import java.sql.*;
public class DatabaseInitializer {

	public static void main(String[] args) throws Exception {
		System.out.println("\nDrivers loaded as properties:");
		System.out.println(System.getProperty("jdbc.drivers"));
		System.out.println("\nDrivers loaded by DriverManager:");
		Enumeration<Driver> list = DriverManager.getDrivers();
		while (list.hasMoreElements())
			System.out.println(list.nextElement());
		String url = "jdbc:mysql://stusql.dcs.shef.ac.uk/team014";
		String user = "team014";
		String password = "80019d16";
		try(Connection con = DriverManager.getConnection(url, user, password)){
			System.out.println("connected to team14 database");

			dropAllTables(con);
			
			initializeDatabase(con);
			displayAllTables(con);
			System.out.println("finsih");
		}
	
	}

	public static void initializeDatabase(Connection con) throws Exception {
		Statement stmt = con.createStatement();
		stmt.addBatch(
				"CREATE TABLE account ("
				+ "email char(255) NOT NULL PRIMARY KEY,"
				+ "password char(255) NOT NULL,"
				+ "title TEXT NOT NULL,"
				+ "forename TEXT NOT NULL,"
				+ "surname TEXT NOT NULL,"
				+ "affiliation TEXT NOT NULL"
				+ ")");
		stmt.addBatch(
				"INSERT INTO account (email, password, title, forename, surname, affiliation) "
				+ "VALUES ('llai2@shef','pw','mr.','Ling','Lai','uos')");
		stmt.addBatch(
				"CREATE TABLE editor ("
				+ "editorID int NOT NULL PRIMARY KEY AUTO_INCREMENT,"
				+ "email char(255) NOT NULL,"
				+ "FOREIGN KEY (email) REFERENCES account(email)"
				+ ")");
		stmt.addBatch(
				"CREATE TABLE journal ("
				+ "issn int NOT NULL PRIMARY KEY,"
				+ "name char(255) NOT NULL"
				+ ")");
		stmt.addBatch(
				"CREATE TABLE editing ("
				+ "editorID int NOT NULL,"
				+ "issn int NOT NULL,"
				+ "become_chief_editor BIT DEFAULT 0,"
				+ "PRIMARY KEY (editorID,issn),"
				+ "FOREIGN KEY (editorID) REFERENCES editor(editorID),"
				+ "FOREIGN KEY (issn) REFERENCES journal(issn)"
				+ ")");
		stmt.addBatch(
				"CREATE TABLE volume ("
				+ "issn int NOT NULL,"
				+ "volume int NOT NULL,"
				+ "PRIMARY KEY (issn,volume),"
				+ "FOREIGN KEY (issn) REFERENCES journal(issn)"
				+ ")");			
		stmt.addBatch(
				"CREATE TABLE edition ("
				+ "issn int NOT NULL,"
				+ "volume int NOT NULL,"
				+ "number int NOT NULL,"
				+ "PRIMARY KEY (issn,volume,number),"
				+ "FOREIGN KEY (issn,volume) REFERENCES volume (issn,volume)"
				+ ")");
		
		stmt.addBatch(
				"CREATE TABLE work ("
				+ "workID int NOT NULL AUTO_INCREMENT,"
				+ "issn int NOT NULL,"
				+ "PRIMARY KEY (workID),"
				+ "FOREIGN KEY (issn) REFERENCES journal(issn)"
				+ ")");			
		stmt.addBatch(
				"CREATE TABLE article ("
				+ "issn int NOT NULL,"
				+ "volume int NOT NULL,"
				+ "number int NOT NULL,"
				+ "page_start int NOT NULL,"
				+ "page_end int NOT NULL /*inclusive*/,"
				+ "workID int NOT NULL,"
				+ "PRIMARY KEY (issn,volume,number,page_start),"
				+ "FOREIGN KEY (issn,volume,number) REFERENCES edition (issn,volume,number),"
				+ "FOREIGN KEY (workID) REFERENCES work (workID)"
				+ ")");
		stmt.addBatch(
				"CREATE TABLE author ("
				+ "authorID int NOT NULL AUTO_INCREMENT,"
				+ "email char(255) NOT NULL,"
				+ "PRIMARY KEY (authorID),"
				+ "FOREIGN KEY (email) REFERENCES account(email)"
				+ ")");	
		stmt.addBatch(
				"CREATE TABLE authoring ("
				+ "authorID int NOT NULL,"
				+ "workID int NOT NULL,"
				+ "is_corresponding_author BIT DEFAULT 0,"
				+ "PRIMARY KEY (authorID,workID),"
				+ "FOREIGN KEY (authorID) REFERENCES author(authorID),"
				+ "FOREIGN KEY (workID) REFERENCES work(workID)"
				+ ")");	

		stmt.addBatch(
				"CREATE TABLE submission ("
				+ "workID int NOT NULL,"
				+ "submissionID int NOT NULL ,"
				+ "title char(255) NOT NULL,"
				+ "abstract text NOT NULL,"
				+ "pdf BLOB /*NOT NULL*/,"
				+ "PRIMARY KEY (workID,submissionID),"
				+ "FOREIGN KEY (workID) REFERENCES work(workID)"
				+ ")");

		stmt.addBatch(
				"CREATE TABLE reviewer ("
				+ "reviewerID int NOT NULL AUTO_INCREMENT,"
				+ "email char(255) NOT NULL,"
				+ "PRIMARY KEY (reviewerID),"
				+ "FOREIGN KEY (email) REFERENCES account(email)"
				+ ")");
		stmt.addBatch(
				"CREATE TABLE verdict_choice ("
				+ "verdictID int NOT NULL,"
				+ "verdict text NOT NULL,"
				+ "PRIMARY KEY (verdictID)"
				+ ")");	
		stmt.addBatch(
				"INSERT INTO verdict_choice (verdictID,verdict) "
				+ "VALUES (1, 'Strong Accept'),"
				+ " (2, 'Weak Accept'),"
				+ " (3, 'Weak Reject'),"
				+ " (4, 'Strong Reject')");	
		stmt.addBatch(
				"CREATE TABLE verdict ("
				+ "reviewerID int NOT NULL,"
				+ "workID int NOT NULL,"
				+ "submissionID int NOT NULL,"
				+ "verdictID int NOT NULL,"
				+ "PRIMARY KEY (reviewerID,workID,submissionID),"
				+ "FOREIGN KEY (reviewerID) REFERENCES reviewer (reviewerID),"
				+ "FOREIGN KEY (verdictID) REFERENCES verdict_choice (verdictID),"
				+ "FOREIGN KEY (workID,submissionID) REFERENCES submission(workID,submissionID)"
				+ ")");	
		stmt.addBatch(
				"CREATE TABLE review ("
				+ "reviewerID int NOT NULL,"
				+ "workID int NOT NULL,"
				+ "submissionID int NOT NULL,"
				+ "review text NOT NULL,"
				+ "PRIMARY KEY (reviewerID,workID,submissionID),"
				+ "FOREIGN KEY (reviewerID) REFERENCES reviewer (reviewerID),"
				+ "FOREIGN KEY (workID,submissionID) REFERENCES submission(workID,submissionID)"
				+ ")");	

		stmt.addBatch(
				"CREATE TABLE response ("
				+ "reviewerID int NOT NULL,"
				+ "workID int NOT NULL,"
				+ "submissionID int NOT NULL,"
				+ "response text NOT NULL,"
				+ "PRIMARY KEY (reviewerID,workID,submissionID),"
				+ "FOREIGN KEY (reviewerID,workID,submissionID) REFERENCES review (reviewerID,workID,submissionID)"
				+ ")");	
		System.out.println("executeBatch");
		stmt.executeBatch();
		System.out.println("finish initilizing");
	}
	public static void dropAllTables(Connection con) throws Exception {
		Statement stmt = con.createStatement();

		stmt.addBatch("SET FOREIGN_KEY_CHECKS = 0 ");
		ArrayList<String> tables = findAllTables(con);
		for(String table : tables) {
			System.out.println("dropping table "+table);
			stmt.addBatch("DROP TABLE "+table);
		}
		stmt.addBatch("SET FOREIGN_KEY_CHECKS = 1 ");
		stmt.executeBatch();
	}

	public static ArrayList<String> findAllTables(Connection con) throws Exception {
		Statement stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SHOW TABLES");
		ArrayList<String> tableNames = new ArrayList<String>();
		while(res.next()) {
			String txt = res.getString(1);
			tableNames.add(txt);
		}
		res.close();
		return tableNames;
	}
	public static void displayAllTables(Connection con) throws Exception {
		System.out.println("here are table names:");
		ArrayList<String> tableNames = findAllTables(con);
		for(String table : tableNames) {
			System.out.println(table);
		}
		System.out.println("//");
	}
}
