package myUI;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseHandlerTest {
	int x = 1;
	@BeforeAll
	static void setup() throws Exception {
		//x = 10;
		System.out.println("resetting");
		DatabaseInitializer.main(new String[0]);
		//Aupeter is supposed to be an email address
		DatabaseHandler.signUp("AuPeter","pw","mr","first name","last","uos");
		DatabaseHandler.signUp("AuMary","pw","mr","first name2","last2","uos");
		DatabaseHandler.signUp("AuJordon","pw","mr","first name2","last2","uos");
		
		DatabaseHandler.signUp("EdGordon","pw","mr","first name","last","uos");
		DatabaseHandler.signUp("EdTomas","pw","mr","first name2","last2","uos");
		DatabaseHandler.signUp("EdTom","pw","mr","first name2","last2","uos");
		
		DatabaseHandler.setReviewer("AuPeter");
		DatabaseHandler.setReviewer("AuMary");
		DatabaseHandler.setReviewer("EdGordon");
		DatabaseHandler.setReviewer("EdTomas");
		String[] b = new String[] {"EdTomas"};
		DatabaseHandler.createJounral(1,"name","EdGordon",Arrays.asList(b));
		String[] a = new String[] {"AuMary"};
		DatabaseHandler.addWork(1,"AuPeter",Arrays.asList(a));
	}
	@Test
	@Order(0)
	void testAccount() throws Exception {
		try {
			// test signup using repeated email
			assertFalse(DatabaseHandler.signUp("AuMary","pw2","mr","haha","ypo","uos"));
			// test sign in using wrong pw
			assertTrue(DatabaseHandler.logIn("AuMary", "pw"));
			assertFalse(DatabaseHandler.logIn("AuMary", "wrong pw"));
			
			// test change pw then sign in using old/new pw
			DatabaseHandler.changePassword("AuMary", "new pw");
			assertFalse(DatabaseHandler.logIn("AuMary", "pw"));
			assertTrue(DatabaseHandler.logIn("AuMary", "new pw"));
			
			assertTrue(DatabaseHandler.isAuthor("AuMary"));
			assertFalse(DatabaseHandler.isAuthor("EdGordon"));
			

			assertTrue(DatabaseHandler.isEditor("EdGordon"));
			assertTrue(DatabaseHandler.isReviewer("EdTomas"));
			assertEquals(DatabaseHandler.getReviewerID("AuPeter"),1);
		}
		finally {
			
		}
	}
	
	@Test
	@Order(0)
	void testAuthor() throws Exception {
		try {
			
			List<Author> authors1 = DatabaseHandler.getAuthors(1);
			List<String> emails = new ArrayList<String>();
			for(Author author : authors1) {
				emails.add(author.getEmail());
			}
			assertThat(emails, CoreMatchers.hasItems("AuPeter","AuMary"));
			// test corresponding author
			assertTrue(DatabaseHandler.getCorrespondingAuthor(1).getEmail().equals("AuPeter"));
			// test change corresponding author
			DatabaseHandler.changeCorrespondingAuthor(1, "AuMary");
			assertTrue(DatabaseHandler.getCorrespondingAuthor(1).getEmail().equals("AuMary"));
			
			DatabaseHandler.addAuthoring(1, "AuJordon");
			List<Author> authors2 = DatabaseHandler.getAuthors(1);
			List<String> emails2 = new ArrayList<String>();
			for(Author author : authors2) {
				emails2.add(author.getEmail());
			}
			assertThat(emails2, CoreMatchers.hasItems("AuPeter","AuMary","AuJordon"));
			
			DatabaseHandler.removeAuthoring(1, "AuPeter");
			List<Author> authors3 = DatabaseHandler.getAuthors(1);
			List<String> emails3 = new ArrayList<String>();
			for(Author author : authors3) {
				emails3.add(author.getEmail());
			}
			assertThat(emails3, CoreMatchers.hasItems("AuMary","AuJordon"));
			
		}
		finally {
			
		}
	}
	@Test
	@Order(0)
	void testUploadSubmission() throws Exception {
		try {
			boolean success = DatabaseHandler.addSubmision(1, "title1", "abstract test", true);
			assertTrue(success);
			boolean success2 = DatabaseHandler.addSubmision(1, "title update", "abstract test", false);
			assertTrue(success2);
			
			
			String[] a = new String[] {};
			DatabaseHandler.addWork(1,"AuMary",Arrays.asList(a));
			boolean success3 = DatabaseHandler.addSubmision(2, "strongest AI", "pineapple juice", true);
			assertTrue(success3);
			
			List<Work> works = DatabaseHandler.getWorksReview("AuPeter");
			List<Integer> workID = new ArrayList<Integer>();
			for(Work w : works) {
				workID.add(w.workID);
			}
			System.out.println(workID);
			assertThat(workID, CoreMatchers.hasItems(2));

			
			List<Work> works2 = DatabaseHandler.getWorksReview("");
			List<Integer> workID2 = new ArrayList<Integer>();
			for(Work w : works2) {
				workID2.add(w.workID);
			}
			System.out.println(workID2);
			assertThat(workID2, CoreMatchers.hasItems(1,2));
			

			
		}
		finally {
			
		}
	}
	@Test
	@Order(1)
	void testUploadVerdict() throws Exception {
		try {
			boolean success = DatabaseHandler.addVerdict(1, 1, 1, 1);
			assertTrue(success);
			// repeat verdict, turn to update
			boolean success2 = DatabaseHandler.addVerdict(1, 1, 1, 2);
			assertTrue(success2);

			boolean fail1 = DatabaseHandler.addVerdict(1, 1, 1, 5);
			assertFalse(fail1);
		}
		finally {
			
		}
	}
	@Test
	@Order(2)
	void testGetWork() throws Exception {
	}
	
	@Test
	@Order(2)
	void testUploadReview() throws Exception {
		try {
			boolean success = DatabaseHandler.addReview(1, 1, 1, "this submission is good",1);
			assertTrue(success);

			String response = DatabaseHandler.getReview(1, 1);
			assertTrue(response.equals("this submission is good"));
			int verdict = DatabaseHandler.getVerdict(1, 1, 1);
			assertEquals(verdict,1);
			
			boolean successUpdate = DatabaseHandler.addReview(1, 1, 1, "this submission may not be that good actually",2);
			assertTrue(successUpdate);
			boolean enoughReviews = DatabaseHandler.hasEnoughReviews(1, 1);
			assertFalse(enoughReviews);

			boolean success2 = DatabaseHandler.addReview(1, 1, 2, "this submission may not be that good actually",2);
			assertTrue(success2);
			boolean enoughReviews2 = DatabaseHandler.hasEnoughReviews(1, 1);
			assertFalse(enoughReviews2);

			boolean success3 = DatabaseHandler.addReview(1, 1, 3, "this submission may not be that good actually",2);
			assertTrue(success3);
			boolean enoughReviews3 = DatabaseHandler.hasEnoughReviews(1, 1);
			assertTrue(enoughReviews3);
		}
		finally {
			
		}
	}
	@Test
	@Order(3)
	void testUploadResponse() throws Exception {
		try {
			boolean success = DatabaseHandler.addResponse(1, 1, 1, "i agree with you");
			assertTrue(success);
			// reviewerID not exist
			boolean fail = DatabaseHandler.addResponse(1, 1, 5, "i agree with you");
			assertFalse(fail);
			
			boolean notEnoughResponse = DatabaseHandler.hasEnoughResponse(1, 1);
			assertFalse(notEnoughResponse);
			DatabaseHandler.addResponse(1, 1, 2, "i agree with you");
			DatabaseHandler.addResponse(1, 1, 3, "thank you for your review");
			boolean enoughResponse = DatabaseHandler.hasEnoughResponse(1, 1);
			assertTrue(enoughResponse);
		}
		finally {
			
		}
	}
	@Test
	@Order(4)
	void testGetEditor() throws Exception {
		try {
			List<String> editors = DatabaseHandler.getEditors(1);
			assertThat(editors, CoreMatchers.hasItems("EdGordon","EdTomas"));
			String chiefEditor = DatabaseHandler.getChiefEditor(1);
			assertTrue(chiefEditor.equals("EdGordon"));
			//test change editor
			DatabaseHandler.changeChiefEditor(1, "EdTomas");
			String chiefEditor2 = DatabaseHandler.getChiefEditor(1);
			assertTrue(chiefEditor2.equals("EdTomas"));
			//test add editor
			DatabaseHandler.addEditing(1, "EdTom");
			List<String> editors2 = DatabaseHandler.getEditors(1);
			assertThat(editors2, CoreMatchers.hasItems("EdGordon","EdTomas","EdTom"));
			//test remove editor
			DatabaseHandler.removeEditing(1, "EdGorden");
			List<String> editors3 = DatabaseHandler.getEditors(1);
			assertThat(editors3, CoreMatchers.hasItems("EdTomas","EdTom"));
		}
		finally {
			
		}
	}
	@Test
	@Order(4)
	void testAddArticle() throws Exception {
		try {
			DatabaseHandler.addVolumne(1, 1);
			DatabaseHandler.addEdition(1, 1, 1);
			DatabaseHandler.addArticle(1, 1, 1, 1, 5, 1);
//			Integer[] a = new Integer[] {1,3};
			String[] a = new String[] {"AuPeter","AuJordon"};
			DatabaseHandler.addWork(1,"AuMary",Arrays.asList(a));
			DatabaseHandler.addArticle(1, 1, 1, 6, 8, 2);

			//assertThat(DatabaseHandler.getJournals(), CoreMatchers.hasItems(1));
//			assertThat(DatabaseHandler.getArticles(1, 1, 1), CoreMatchers.hasItems(1,2));
		}
		finally {
			
		}
	}
	@Test
	@Order(5)
	void testRemoveReviewer() throws Exception{
		assertTrue(DatabaseHandler.isReviewer("Edtomas"));
		int reviewerID = DatabaseHandler.getReviewerID("EdTomas");
		DatabaseHandler.removeReviewer(reviewerID);
		assertFalse(DatabaseHandler.isReviewer("Edtomas"));
		DatabaseHandler.setReviewer("EdTomas");
	}
}
