package main;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;

import org.junit.Assert;
import org.junit.Test;

public class SampleTest extends Assert {
	
	@Test
    public void testSuccess() {
        assertTrue(true);
    }
	
	@Test
	public void swingTest() {
		JButton b = new JButton("Test...");
		assertEquals(b.getText(), "Test...");
	}
	
	//Verify interactions
	@Test
	public void mockInteractionTest() {
		
		// mock creation
		List mockedList = mock(List.class);

		// using mock object - it does not throw any "unexpected interaction" exception
		mockedList.add("one");
		mockedList.clear();

		// selective, explicit, highly readable verification
		verify(mockedList).add("one");
		verify(mockedList).clear();
		
	}
	
	//verify stub method calls
	@Test
	public void mockMethodCallsTest() {

		// you can mock concrete classes, not only interfaces
		LinkedList mockedList = mock(LinkedList.class);
	
		// stubbing appears before the actual execution
		when(mockedList.get(0)).thenReturn("first");
	
		//Now that we have stubbed, this call will return "first"
		assertEquals(mockedList.get(0), "first");
	
		// the following should be  "null" because get(999) was not stubbed
		assertEquals(mockedList.get(999), null);
	}
	
}
