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
		DatabaseHandler.setAuthor("AuPeter");
		DatabaseHandler.signUp("AuMary","pw","mr","first name2","last2","uos");
		DatabaseHandler.setAuthor("AuMary");

		DatabaseHandler.signUp("AuJordon","pw","mr","first name2","last2","uos");
		DatabaseHandler.setAuthor("AuJordon");
		
		DatabaseHandler.signUp("EdGordon","pw","mr","first name","last","uos");
		DatabaseHandler.setEditor("EdGordon");
		DatabaseHandler.signUp("EdTomas","pw","mr","first name2","last2","uos");
		DatabaseHandler.setEditor("EdTomas");

		DatabaseHandler.signUp("EdTom","pw","mr","first name2","last2","uos");
		DatabaseHandler.setEditor("EdTom");
		
		DatabaseHandler.setReviewer("AuPeter");
		DatabaseHandler.setReviewer("AuMary");
		DatabaseHandler.setReviewer("EdGordon");
		DatabaseHandler.setReviewer("EdTomas");
		Integer[] b = new Integer[] {2};
		DatabaseHandler.createJounral(1,"name",1,Arrays.asList(b));
		Integer[] a = new Integer[] {2};
		DatabaseHandler.addWork(1,1,Arrays.asList(a));
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
			
		}
		finally {
			
		}
	}
	
	@Test
	@Order(0)
	void testAuthor() throws Exception {
		try {
			List<Author> authors1 = DatabaseHandler.getAuthors(1);
			List<Integer> ids = new ArrayList<Integer>();
			for(Author author : authors1) {
				ids.add(author.getId());
			}
			assertThat(ids, CoreMatchers.hasItems(1,2));
			
			// test corresponding author
			assertEquals(DatabaseHandler.getCorrespondingAuthor(1).getId(),1);
			// test change corresponding author
			DatabaseHandler.changeCorrespondingAuthor(1, 2);
			assertEquals(DatabaseHandler.getCorrespondingAuthor(1).getId(),2);
			
			DatabaseHandler.addAuthoring(1, 3);
			List<Author> authors2 = DatabaseHandler.getAuthors(1);
			List<Integer> ids2 = new ArrayList<Integer>();
			for(Author author : authors2) {
				ids2.add(author.getId());
			}
			assertThat(ids2, CoreMatchers.hasItems(1,2,3));
			
			DatabaseHandler.removeAuthoring(1, 1);
			List<Author> authors3 = DatabaseHandler.getAuthors(1);
			List<Integer> ids3 = new ArrayList<Integer>();
			for(Author author : authors3) {
				ids3.add(author.getId());
			}
			assertThat(ids3, CoreMatchers.hasItems(2,3));
			
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
	void testUploadReview() throws Exception {
		try {
			boolean success = DatabaseHandler.addReview(1, 1, 1, "this submission is good",1);
			assertTrue(success);
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
			List<Integer> editors = DatabaseHandler.getEditors(1);
			assertThat(editors, CoreMatchers.hasItems(1,2));
			Integer chiefEditor = DatabaseHandler.getChiefEditor(1);
			assertEquals(chiefEditor,1);
			//test change editor
			DatabaseHandler.changeChiefEditor(1, 2);
			Integer chiefEditor2 = DatabaseHandler.getChiefEditor(1);
			assertEquals(chiefEditor2,2);
			//test add editor
			DatabaseHandler.addEditing(1, 3);
			List<Integer> editors2 = DatabaseHandler.getEditors(1);
			assertThat(editors2, CoreMatchers.hasItems(1,2,3));
			//test remove editor
			DatabaseHandler.removeEditing(1, 1);
			List<Integer> editors3 = DatabaseHandler.getEditors(1);
			assertThat(editors3, CoreMatchers.hasItems(2,3));
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
			Integer[] a = new Integer[] {1,3};
			DatabaseHandler.addWork(1,2,Arrays.asList(a));
			DatabaseHandler.addArticle(1, 1, 1, 6, 8, 2);

			assertThat(DatabaseHandler.getJournals(), CoreMatchers.hasItems(1));
			assertThat(DatabaseHandler.getArticles(1, 1, 1), CoreMatchers.hasItems(1,2));
		}
		finally {
			
		}
	}
}
