package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TextAnalyzer extends Application{
	
	Button button;

	public static void main(String[] args) {
		launch(args);

	}
	
	/**
	 * Sets the stage for a JavaFx GUI.
	 * The GUI will prompt the user for a file name and a url to be scanned.
	 * 
	 * @param primarystage Sets the JavaFx stage 
	 */
	
	@Override
	public void start(Stage primarystage) {
		primarystage.setTitle("Poem Text Analyzer");
		Label label1 = new Label("Enter name of file you wish to create:");
		TextField filename = new TextField();
		Label label2 = new Label("Enter URL:");
		TextField URLInput = new TextField();
		button = new Button();
		button.setText("Submit");
		
		
		button.setOnAction(e -> {
			try {
				runTextAnalyzer(filename.getText(), URLInput.getText());
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		});
		
		VBox layout = new VBox(20);
		layout.setPadding(new Insets(20, 20, 20, 20));
		layout.getChildren().addAll(label1, filename,label2, URLInput, button);
		
		
		Scene scene = new Scene(layout, 500, 400);
		
		 URL url = this.getClass().getResource("application.css");
		    if (url == null) {
		        System.out.println("Resource not found. Aborting.");
		        System.exit(-1);
		    }
		    String css = url.toExternalForm(); 
		    scene.getStylesheets().add(css);
		
		primarystage.setScene(scene);
		primarystage.show();
	}
	
	/**
	 * Returns a file object if the file does not already exist.
	 * Displays alertbox if file was successfully created or not.
	 * 
	 * @param filename  string input from user
	 * @return  file object created with filename input from user
	 */
	
	public File createFile(String filename) {
		
		String fileName = filename + ".txt";
		File file = new File(fileName);
		
		if(file.exists()) {
			System.out.println("File already exists");
			AlertBox.display("Not a success", "File already exists");
			System.exit(1);
		}
		
		AlertBox.display("Success", "Successfully created file");
		
		return file;
	}
	
	/**
	 * Reads a url in string form that is converted into a URL object.
	 * URL is then scanned by a scanner object.
	 * 
	 * @param urlinput  string form of url to be read by scanner
	 * @return input  a scanner object that scans the url provided by user
	 * @throws IOException
	 */

	public Scanner getURL(String urlinput) throws IOException {
		
		String URLString = urlinput;
		java.net.URL url = null;
		try {
			url = new java.net.URL(URLString);
			
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}
		Scanner input = new Scanner(url.openStream());
		
		return input;
	}
	
	/**
	 * Creates two ArrayLists, one to contain the full contents of the scanned url,
	 * and another to contain a specific portion of the scanned contents of the url.
	 * Returns second ArrayList.
	 * 
	 * @param input scanner object of url input by user
	 * @return  ArrayList object that contains a portion of the url content separated by a period.
	 */
	
	public ArrayList<String> extractPoem(Scanner input) {
			
			Boolean isInChapter = false;
			ArrayList<String> nextLineArray = new ArrayList<>();
			ArrayList<String> poemArray = new ArrayList<>();
			
			while(input.hasNext()) {
				nextLineArray.add(input.nextLine());
			}
			
			for(int i = 0; i < nextLineArray.size(); i++) {
				if(nextLineArray.get(i).contains("<h1>")) {
					isInChapter = true;
				}
				if(nextLineArray.get(i).contains("</div>")) {
					isInChapter = false;
				}
				if(isInChapter) {
					
				poemArray.add(nextLineArray.get(i).replaceAll("(<[^>]*>)|([;?!@#$%^&*:.,\"“”’])", " "));
					
				}
			}
			
			return poemArray;
		}

	/**
	 * Returns a HashMap with each word in the passed in ArrayList 
	 * and the count of each occurrence of each word.
	 * 
	 * @param poemArray  ArrayList with part of the contents of the scanned url
	 * @return  HashMap with each word in the scanned url and its count
	 */
	
	public HashMap<String, Integer> countWords(ArrayList<String> poemArray) {
		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

		for(int j = 0; j < poemArray.size(); j++) {
				String[] words = poemArray.get(j).toLowerCase().split(" ");
				
				for(String word: words) {
				
				if(wordCount.containsKey(word)) {
					wordCount.put(word, wordCount.get(word)+1);
				} else {
					wordCount.put(word, 1);
				}	
			
			}	
		}
		return wordCount;
	}
	
	/**
	 * Creates a sorted HashMap from the one passed in. 
	 * It sorts the HashMap by value from to greatest to smallest.
	 * 
	 * @param wordCount  HashMap with words and it's corresponding count
	 * @return  sorted HashMap from greatest to smallest
	 */
	
	public Map<String, Integer> sortHashMap(HashMap<String, Integer> wordCount) {
		Map<String, Integer> sorted = wordCount
		        .entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
		        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		
		return sorted;
	}
	
	/**
	 * Prints the sorted HashMap contents to a .txt file created from user input.
	 * 
	 * @param file  File object created by user
	 * @param wordCount  sorted HashMap 
	 * @throws FileNotFoundException
	 */


	public void printToFile(File file, Map<String, Integer> wordCount) throws FileNotFoundException {
		PrintWriter output = new PrintWriter(file);
		
		for(String key: wordCount.keySet()) {
			
			output.print(key + " " + wordCount.get(key) + "\n");
		}
		
		output.close();
	}
	
	/**
	 * Runs all the methods in the class when called. 
	 * 
	 * @param filename  String input from the user to name File object
	 * @param urlinput  String input from user to point to url to be scanned by Scanner object
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
	public void runTextAnalyzer(String filename, String urlinput) throws FileNotFoundException, IOException {
		printToFile(createFile(filename), sortHashMap(countWords(extractPoem(getURL(urlinput)))));
	}

}
