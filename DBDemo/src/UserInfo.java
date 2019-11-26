import java.sql.Connection;

public class UserInfo {
	public int userIndex;
	public UserInfo() {
		userIndex = -1;
	}
	public static boolean logIn(String email,String password) {
		try {
			return DatabaseHandler.logIn(email, password);
		}
		catch(Exception e){
			return false;
		}
	}
}
