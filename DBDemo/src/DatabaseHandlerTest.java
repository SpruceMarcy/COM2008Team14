import static org.junit.jupiter.api.Assertions.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

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
	void testCorrespondingAuthor() throws Exception {
		System.out.println(x);
		try {
			assertEquals(DatabaseHandler.getCorrespondingAuthor(1),1);
		}
		finally {
			
		}
		//fail("Not yet implemented");
	}
	@Test
	void testAuthor() throws Exception {
		try {
			List<Integer> actual = DatabaseHandler.getAuthor(1);
			List<Integer> expected = Arrays.asList(new Integer[] {2,1});
			assertThat(actual, CoreMatchers.hasItems(1,2));
		}
		finally {
			
		}
		//fail("Not yet implemented");
	}

}
