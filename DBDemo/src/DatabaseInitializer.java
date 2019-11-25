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
			System.out.println("connected to team14 database?");
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
			stmt.addBatch("CREATE TABLE Test(testID int)");
			stmt.executeBatch();
			
		}
		catch(Exception e){
			throw e;
		}
	}
	public static void DropAllTables(Connection con) throws Exception {
		try {
			Statement stmt = con.createStatement();

			ArrayList<String> tables = FindAllTables(con);
			for(String table : tables) {
				System.out.println("dropping table "+table);
				stmt.addBatch("DROP TABLE "+table);
			}
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
