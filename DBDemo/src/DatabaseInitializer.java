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

			DropAllTables(con);
			
			InitializeDatabase(con);
			DisplayAllTables(con);
			System.out.println("finsih2");
		}
	
	}

	public static void InitializeDatabase(Connection con) throws Exception {
		try {
			Statement stmt = con.createStatement();
			stmt.addBatch(
					"CREATE TABLE account ("
					+ "email char(255) NOT NULL PRIMARY KEY"
					+ ")");
			stmt.addBatch(
					"CREATE TABLE editor ("
					+ "editorID int NOT NULL PRIMARY KEY,"
					+ "email char(255) NOT NULL,"
					+ "FOREIGN KEY editor(email) REFERENCES account(email)"
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
					+ "FOREIGN KEY editing(editorID) REFERENCES editor(editorID),"
					+ "FOREIGN KEY editing(issn) REFERENCES journal(issn)"
					+ ")");
			stmt.addBatch(
					"CREATE TABLE volumn ("
					+ "issn int NOT NULL,"
					+ "volumn int NOT NULL,"
					+ "PRIMARY KEY (issn,volumn),"
					+ "FOREIGN KEY volumn(issn) REFERENCES journal(issn)"
					+ ")");			
			stmt.addBatch(
					"CREATE TABLE edition ("
					+ "issn int NOT NULL,"
					+ "volumn int NOT NULL,"
					+ "number int NOT NULL,"
					+ "PRIMARY KEY (issn,volumn,number),"
					+ "FOREIGN KEY edition (issn,volumn) REFERENCES volumn (issn,volumn)"
					+ ")");	
			stmt.addBatch(
					"CREATE TABLE work ("
					+ "workID int NOT NULL,"
					+ "PRIMARY KEY (workID)"
					+ ")");			
			stmt.addBatch(
					"CREATE TABLE article ("
					+ "issn int NOT NULL,"
					+ "volumn int NOT NULL,"
					+ "number int NOT NULL,"
					+ "page_start int NOT NULL,"
					+ "page_end int NOT NULL /*inclusive*/,"
					+ "workID int NOT NULL,"
					+ "PRIMARY KEY (issn,volumn,number,page_start),"
					+ "FOREIGN KEY article (issn,volumn,number) REFERENCES edition (issn,volumn,number),"
					+ "FOREIGN KEY article (workID) REFERENCES work (workID)"
					+ ")");
			stmt.addBatch(
					"CREATE TABLE author ("
					+ "authorID int NOT NULL,"
					+ "email char(255) NOT NULL,"
					+ "PRIMARY KEY (authorID),"
					+ "FOREIGN KEY editor(email) REFERENCES account(email)"
					+ ")");	
			stmt.addBatch(
					"CREATE TABLE authoring ("
					+ "authorID int NOT NULL,"
					+ "workID int NOT NULL,"
					+ "PRIMARY KEY (authorID,workID),"
					+ "FOREIGN KEY authoring(authorID) REFERENCES author(authorID),"
					+ "FOREIGN KEY authoring(workID) REFERENCES work(workID)"
					+ ")");	

			stmt.addBatch(
					"CREATE TABLE submission ("
					+ "workID int NOT NULL,"
					+ "submissionID int NOT NULL,"
					+ "PRIMARY KEY (workID,submissionID),"
					+ "FOREIGN KEY submission (workID) REFERENCES work(workID)"
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
			// something is preventing me to use the uncommented version -Duplicate key name 'Verdict'
			stmt.addBatch(
					"CREATE TABLE verdict ("
					+ "authorID int NOT NULL,"
					+ "workID int NOT NULL,"
					+ "submissionID int NOT NULL,"
					+ "verdictID int NOT NULL REFERENCES verdict_choice (verdictID),"
					//+ "verdictID int NOT NULL,"
					+ "PRIMARY KEY (authorID,workID,submissionID),"
					+ "FOREIGN KEY verdict (authorID) REFERENCES author (authorID),"
					//+ "FOREIGN KEY Verdict (verdictID) REFERENCES Verdict_Choice (verdictID),"
					+ "FOREIGN KEY verdict (workID,submissionID) REFERENCES submission(workID,submissionID)"
					+ ")");	
			stmt.addBatch(
					"CREATE TABLE review ("
					+ "authorID int NOT NULL,"
					+ "workID int NOT NULL,"
					+ "submissionID int NOT NULL,"
					+ "review char NOT NULL,"
					+ "PRIMARY KEY (authorID,workID,submissionID),"
					+ "FOREIGN KEY review (authorID) REFERENCES author (authorID),"
					+ "FOREIGN KEY review (workID,submissionID) REFERENCES submission(workID,submissionID)"
					+ ")");	

			stmt.addBatch(
					"CREATE TABLE response ("
					+ "authorID int NOT NULL,"
					+ "workID int NOT NULL,"
					+ "submissionID int NOT NULL,"
					+ "response char NOT NULL,"
					+ "PRIMARY KEY (authorID,workID,submissionID),"
					+ "FOREIGN KEY response (authorID,workID,submissionID) REFERENCES review (authorID,workID,submissionID)"
					+ ")");	
			System.out.println("executeBatch");
			stmt.executeBatch();
			System.out.println("finish initilizing");
			
		}
		catch(Exception e){
			throw e;
		}
	}
	public static void DropAllTables(Connection con) throws Exception {
		try {
			Statement stmt = con.createStatement();

			stmt.addBatch("SET FOREIGN_KEY_CHECKS = 0 ");
			ArrayList<String> tables = FindAllTables(con);
			for(String table : tables) {
				System.out.println("dropping table "+table);
				stmt.addBatch("DROP TABLE "+table);
			}
			stmt.addBatch("SET FOREIGN_KEY_CHECKS = 1 ");
			stmt.executeBatch();
			
		}
		catch(Exception e){
			throw e;
		}
	}

	public static ArrayList<String> FindAllTables(Connection con) throws Exception {
		try {
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
		catch(Exception e){
			throw e;
		}
	}
	public static void DisplayAllTables(Connection con) throws Exception {
		try {
			System.out.println("here are table names:");
			ArrayList<String> tableNames = FindAllTables(con);
			for(String table : tableNames) {
				System.out.println(table);
			}
			System.out.println("//");
		}
		catch(Exception e){
			throw e;
		}
	}
}
