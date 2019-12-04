package myUI;

import java.util.Arrays;

public class DatabaseExampleData {
	public static void main(String[] args) throws Exception {
		DatabaseInitializer.main(new String[0]);
		createEditor();
		createJournal();
		createWork();
		createReview();
	}
	public static void createEditor() throws Exception {

		DatabaseHandler.signUp("EdGordon","pw","mr","first name","last","uos");
		DatabaseHandler.signUp("EdTomas","pw","mr","first name2","last2","uos");
		DatabaseHandler.signUp("EdTom","pw","mr","first name2","last2","uos");	
		DatabaseHandler.signUp("EdSam","pw","mr","first name2","last2","uos");	
		

		DatabaseHandler.signUp("AuPeter","pw","mr","first name","last","uos");
		DatabaseHandler.signUp("AuMary","pw","mr","first name2","last2","uos");
		DatabaseHandler.signUp("AuJordon","pw","mr","first name2","last2","uos");
		DatabaseHandler.signUp("AuDog","pw","mr","dog","dogg","uos");
		DatabaseHandler.signUp("AuCat","pw","msr","cat","caatt","uos");
		DatabaseHandler.signUp("AuTurtle","pw","mr","first name2","last2","uos");
		DatabaseHandler.signUp("AuFish","pw","mr","first name2","last2","uos");

		DatabaseHandler.signUp("AuFire","pw","mr","first name2","last2","uos");
		DatabaseHandler.signUp("AuWater","pw","mr","first name2","last2","uos");
		DatabaseHandler.signUp("AuWind","pw","mr","first name2","last2","uos");
		
		DatabaseHandler.setReviewer("AuPeter");
		DatabaseHandler.setReviewer("AuMary");
		DatabaseHandler.setReviewer("AuJordon");
		
	}
	public static void createJournal() {
		/**
		 * 3 journal created by edgordon, edtom, edsam
		 * edtomas is also a editor for computer science journal and philosophy journal 
		 */
		String[] b = new String[] {"EdTomas"};
		DatabaseHandler.createJounral(1, "computer science journal", "EdGordon", Arrays.asList(b));

		DatabaseHandler.createJounral(15, "philosophy journal", "EdTom", Arrays.asList(b));

		DatabaseHandler.createJounral(24, "psy journal", "EdSam", Arrays.asList(new String[0]));
	}
	public static void createWork() throws Exception {
		DatabaseHandler.addWork(1, "AuPeter",  Arrays.asList( new String[] {}));
		DatabaseHandler.addSubmision(1, "Peter life", "peter is happy", true);
		DatabaseHandler.addWork(1, "AuMary",  Arrays.asList( new String[] {}));
		DatabaseHandler.addSubmision(2, "Mary life", "mary's abstract", true);
		DatabaseHandler.addWork(1, "AuDog",  Arrays.asList( new String[] {"AuCat"}));
		DatabaseHandler.addSubmision(3, "dog and cat", "we are pet", true);
		DatabaseHandler.addWork(1, "AuTurtle",  Arrays.asList( new String[] {"AuFish"}));
		DatabaseHandler.addSubmision(4, "marine life", "we live in water", true);
		DatabaseHandler.addWork(1, "AuFire",  Arrays.asList( new String[] {"AuWater","AuWind"}));
		DatabaseHandler.addSubmision(5, "elements of world", "fire water wind", true);
	}

	public static void createReview() throws Exception {
		DatabaseHandler.addReview(3, 1, 1, "it is bad", 1);
		DatabaseHandler.addReview(3, 1, 2, "it is good", 1);
		DatabaseHandler.addReview(3, 1, 3, "it is very bad", 4);
	}
	public static void createResponse() {
		
	}
	public static void createFinalVerdict() {
		
	}
}
