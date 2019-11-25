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
/*
			Statement stmt2 = con.createStatement();
			stmt2.addBatch("DROP TABLE *");
			stmt2.executeBatch();*/
			// DropAllTables(con);
			/*
			Statement stmt2 = con.createStatement();
			stmt2.addBatch("CREATE TABLE Test(testID int)");
			stmt2.executeBatch();*/
			/*
			Statement stmt = con.createStatement();
			ResultSet res = stmt.executeQuery("SHOW TABLES");
			System.out.println(res);*/
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
					"CREATE TABLE Account ("
					+ "email char(255) NOT NULL PRIMARY KEY"
					+ ")");
			stmt.addBatch(
					"CREATE TABLE Editor ("
					+ "editorID int NOT NULL PRIMARY KEY,"
					+ "email char(255) NOT NULL,"
					+ "FOREIGN KEY Editor(email) REFERENCES Account(email)"
					+ ")");
			stmt.addBatch(
					"CREATE TABLE Journal ("
					+ "issn int NOT NULL PRIMARY KEY,"
					+ "name char(255) NOT NULL"
					+ ")");
			stmt.addBatch(
					"CREATE TABLE Editing ("
					+ "editorID int NOT NULL,"
					+ "issn int NOT NULL,"
					+ "become_chief_editor BIT DEFAULT 0,"
					+ "PRIMARY KEY (editorID,issn),"
					+ "FOREIGN KEY Editing(editorID) REFERENCES Editor(editorID),"
					+ "FOREIGN KEY Editing(issn) REFERENCES Journal(issn)"
					+ ")");
			stmt.addBatch(
					"CREATE TABLE Volumn ("
					+ "issn int NOT NULL,"
					+ "volumn int NOT NULL,"
					+ "PRIMARY KEY (issn,volumn),"
					+ "FOREIGN KEY Volumn(issn) REFERENCES Journal(issn)"
					+ ")");			
			stmt.addBatch(
					"CREATE TABLE Edition ("
					+ "issn int NOT NULL,"
					+ "volumn int NOT NULL,"
					+ "number int NOT NULL,"
					+ "PRIMARY KEY (issn,volumn,number),"
					+ "FOREIGN KEY Edition (issn,volumn) REFERENCES Volumn (issn,volumn)"
					+ ")");	
			stmt.addBatch(
					"CREATE TABLE Work ("
					+ "workID int NOT NULL,"
					+ "PRIMARY KEY (workID)"
					+ ")");			
			stmt.addBatch(
					"CREATE TABLE Article ("
					+ "issn int NOT NULL,"
					+ "volumn int NOT NULL,"
					+ "number int NOT NULL,"
					+ "page_start int NOT NULL,"
					+ "page_end int NOT NULL /*inclusive*/,"
					+ "workID int NOT NULL,"
					+ "PRIMARY KEY (issn,volumn,number,page_start),"
					+ "FOREIGN KEY Article (issn,volumn,number) REFERENCES Edition (issn,volumn,number),"
					+ "FOREIGN KEY Article (workID) REFERENCES Work (workID)"
					+ ")");
			stmt.addBatch(
					"CREATE TABLE Author ("
					+ "authorID int NOT NULL,"
					+ "email char(255) NOT NULL,"
					+ "PRIMARY KEY (authorID),"
					+ "FOREIGN KEY Editor(email) REFERENCES Account(email)"
					+ ")");	
			stmt.addBatch(
					"CREATE TABLE Authoring ("
					+ "authorID int NOT NULL,"
					+ "workID int NOT NULL,"
					+ "PRIMARY KEY (authorID,workID),"
					+ "FOREIGN KEY Authoring(authorID) REFERENCES Author(authorID),"
					+ "FOREIGN KEY Authoring(workID) REFERENCES Work(workID)"
					+ ")");	

			stmt.addBatch(
					"CREATE TABLE Submission ("
					+ "workID int NOT NULL,"
					+ "submissionID int NOT NULL,"
					+ "PRIMARY KEY (workID,submissionID),"
					+ "FOREIGN KEY Submission (workID) REFERENCES Work(workID)"
					+ ")");

			stmt.addBatch(
					"CREATE TABLE Verdict_Choice ("
					+ "verdictID int NOT NULL,"
					+ "verdict char NOT NULL,"
					+ "PRIMARY KEY (verdictID)"
					+ ")");	
			// something is preventing me to use the uncommented version -Duplicate key name 'Verdict'
			stmt.addBatch(
					"CREATE TABLE Verdict ("
					+ "authorID int NOT NULL,"
					+ "workID int NOT NULL,"
					+ "submissionID int NOT NULL,"
					+ "verdictID int NOT NULL REFERENCES Verdict_Choice (verdictID),"
					//+ "verdictID int NOT NULL,"
					+ "PRIMARY KEY (authorID,workID,submissionID),"
					+ "FOREIGN KEY Verdict (authorID) REFERENCES Author (authorID),"
					//+ "FOREIGN KEY Verdict (verdictID) REFERENCES Verdict_Choice (verdictID),"
					+ "FOREIGN KEY Verdict (workID,submissionID) REFERENCES Submission(workID,submissionID)"
					+ ")");	
			stmt.addBatch(
					"CREATE TABLE Review ("
					+ "authorID int NOT NULL,"
					+ "workID int NOT NULL,"
					+ "submissionID int NOT NULL,"
					+ "review char NOT NULL,"
					+ "PRIMARY KEY (authorID,workID,submissionID),"
					+ "FOREIGN KEY Review (authorID) REFERENCES Author (authorID),"
					+ "FOREIGN KEY Review (workID,submissionID) REFERENCES Submission(workID,submissionID)"
					+ ")");	

			stmt.addBatch(
					"CREATE TABLE Response ("
					+ "authorID int NOT NULL,"
					+ "workID int NOT NULL,"
					+ "submissionID int NOT NULL,"
					+ "response char NOT NULL,"
					+ "PRIMARY KEY (authorID,workID,submissionID),"
					+ "FOREIGN KEY Response (authorID,workID,submissionID) REFERENCES Review (authorID,workID,submissionID)"
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
				//stmt.addBatch("ALTER TABLE "+table+" NOCHECK CONSTRANT all");
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
			System.out.println("  //");
		}
		catch(Exception e){
			throw e;
		}
	}
}
