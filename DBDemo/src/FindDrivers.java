import java.sql.*;
import java.util.*;
public class FindDrivers {
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
			System.out.print("connected?");
			Statement stmt = con.createStatement();
			ResultSet res = stmt.executeQuery("SHOW TABLES");
			System.out.println(res);
			while(res.next()) {
				String txt = res.getString(1);
				System.out.println(txt);
			}
			System.out.println("finsih");
			res.close();
		}
	
	}
}