import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.*;

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
		DatabaseHandler.signUp("test1","pw","first name","last");
		DatabaseHandler.setAuthor("test1");
		DatabaseHandler.signUp("test2","pw","first name2","last2");
		DatabaseHandler.setAuthor("test2");

		DatabaseHandler.setEditor("test1");
		Integer[] b = new Integer[] {};
		DatabaseHandler.createJounral(1,"name",1,Arrays.asList(b));
		Integer[] a = new Integer[] {2};
		DatabaseHandler.createWork(1,1,Arrays.asList(a));
	}
	@Test
	@Order(0)
	void testSingleAccountPerEmail() throws Exception {
		try {
			// old ac already exist
			assertFalse(DatabaseHandler.signUp("test1","pw2","haha","ypo"));
		}
		finally {
			
		}
	}
	@Test
	@Order(0)
	void testCorrespondingAuthor() throws Exception {
		try {
			assertEquals(DatabaseHandler.getCorrespondingAuthor(1),1);
		}
		finally {
			
		}
	}
	@Test
	@Order(0)
	void testAuthor() throws Exception {
		try {
			List<Author> actual = DatabaseHandler.getAuthor(1);
			//assertThat(actual, CoreMatchers.hasItems(1,2));
		}
		finally {
			
		}
	}

	@Test
	@Order(0)
	void testChangeCorrespondingAuthor() throws Exception {
		try {
			DatabaseHandler.changeCorrespondingAuthor(1, 2);
			assertEquals(DatabaseHandler.getCorrespondingAuthor(1),2);
		}
		finally {
			
		}
	}

	@Test
	@Order(0)
	void testUploadSubmission() throws Exception {
		try {
			boolean success = DatabaseHandler.createSubmision(1, "title1", "abstract test", true);
			assertTrue(success);
		}
		finally {
			
		}
	}
	@Test
	@Order(1)
	void testUploadVerdict() throws Exception {
		try {
			boolean success = DatabaseHandler.uploadVerdict(1, 1, 1, 1);
			assertTrue(success);
			// repeat verdict, turn to update
			boolean success2 = DatabaseHandler.uploadVerdict(1, 1, 1, 2);
			assertTrue(success2);
			// verdict by same user
			boolean success3 = DatabaseHandler.uploadVerdict(1, 1, 2, 1);
			assertTrue(success3);
			boolean fail1 = DatabaseHandler.uploadVerdict(1, 1, 1, 5);
			assertFalse(fail1);
		}
		finally {
			
		}
	}
	@Test
	@Order(2)
	void testUploadReview() throws Exception {
		try {
			boolean success = DatabaseHandler.uploadReview(1, 1, 1, "this submission is good",1);
			assertTrue(success);
			boolean success2 = DatabaseHandler.uploadReview(1, 1, 1, "this submission may not be that good actually",2);
			assertTrue(success2);
		}
		finally {
			
		}
	}
	@Test
	@Order(3)
	void testUploadResponse() throws Exception {
		try {
			boolean success = DatabaseHandler.uploadResponse(1, 1, 1, "i agree with you");
			assertTrue(success);
			// response not exist
			boolean fail = DatabaseHandler.uploadResponse(1, 1, 3, "i agree with you");
			assertFalse(fail);
		}
		finally {
			
		}
	}
}
