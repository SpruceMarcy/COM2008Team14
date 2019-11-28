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
		DatabaseHandler.signUp("test1","pw");
		DatabaseHandler.setAuthor("test1");
		DatabaseHandler.signUp("test2","pw");
		DatabaseHandler.setAuthor("test2");
		Integer[] a = new Integer[] {2};
		DatabaseHandler.createWork(1,Arrays.asList(a));
	}
	@Test
	@Order(0)
	void testSingleAccountPerEmail() throws Exception {
		try {
			// old ac already exist
			assertFalse(DatabaseHandler.signUp("test1","pw2"));
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
			List<Integer> actual = DatabaseHandler.getAuthor(1);
			assertThat(actual, CoreMatchers.hasItems(1,2));
		}
		finally {
			
		}
	}

	@Test
	@Order(0)
	void testChangeCorrespondingAuthor() throws Exception {
		try {
			DatabaseHandler.ChangeCorrespondingAuthor(1, 2);
			assertEquals(DatabaseHandler.getCorrespondingAuthor(1),2);
		}
		finally {
			
		}
	}

	@Test
	@Order(0)
	void testUploadSubmission() throws Exception {
		try {
			boolean success = DatabaseHandler.uploadSubmision(1, "title1", "abstract test", true);
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
			// repeat verdict 
			boolean success2 = DatabaseHandler.uploadVerdict(1, 1, 1, 1);
			assertFalse(success2);
			// verdict by same user
			boolean success3 = DatabaseHandler.uploadVerdict(1, 1, 2, 1);
			assertTrue(success3);
		}
		finally {
			
		}
	}
	@Test
	@Order(2)
	void testUploadReview() throws Exception {
		try {
			boolean success = DatabaseHandler.uploadVerdict(1, 1, 1, 1);
			assertTrue(success);
			// repeat verdict 
			boolean success2 = DatabaseHandler.uploadVerdict(1, 1, 1, 1);
			assertFalse(success2);
			// verdict by same user
			boolean success3 = DatabaseHandler.uploadVerdict(1, 1, 2, 1);
			assertTrue(success3);
		}
		finally {
			
		}
	}
}
