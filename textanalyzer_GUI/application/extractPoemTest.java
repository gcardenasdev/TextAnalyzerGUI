package application;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class extractPoemTest {

	String url1 = "https://www.gutenberg.org/files/1065/1065-h/1065-h.htm";
	String url2 = "https://www.gutenberg.org/files/932/932-h/932-h.htm";
	
	@Test
	void test() throws IOException {
		TextAnalyzer test = new TextAnalyzer();
		
		assertFalse(test.extractPoem(test.getURL(url1)).isEmpty());
	}
	
	@Test
	void test2() throws IOException {
		TextAnalyzer test = new TextAnalyzer();
		
		assertFalse(test.extractPoem(test.getURL(url2)).isEmpty());
	}

}
