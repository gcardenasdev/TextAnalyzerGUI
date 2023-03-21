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
				runTextAnalyzer(filename, URLInput);
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
	
	
	
	public static File createFile(TextField filename) {
		
		String fileName = filename.getText();
		File file = new File(fileName);
		
		if(file.exists()) {
			System.out.println("File already exists");
			AlertBox.display("Not a success", "File already exists");
			System.exit(1);
		}
		
		AlertBox.display("Success", "Successfully created file");
		
		return file;
	}

	public static Scanner getURL(TextField urlinput) throws IOException {
		
		String URLString = urlinput.getText();
		java.net.URL url = null;
		try {
			url = new java.net.URL(URLString);
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}
		Scanner input = new Scanner(url.openStream());
		
		return input;
	}
	
public static ArrayList<String> extractPoem(Scanner input) {
		
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
	
	public static HashMap<String, Integer> countWords(ArrayList<String> poemArray) {
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
	
	public static Map<String, Integer> sortHashMap(HashMap<String, Integer> wordCount) {
		Map<String, Integer> sorted = wordCount
		        .entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
		        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		
		return sorted;
	}
	


	public static void printToFile(File file, Map<String, Integer> wordCount) throws FileNotFoundException {
		PrintWriter output = new PrintWriter(file);
		
		for(String key: wordCount.keySet()) {
			
			output.print(key + " " + wordCount.get(key) + "\n");
		}
		
		output.close();
	}
	
	public static void runTextAnalyzer(TextField filename, TextField urlinput) throws FileNotFoundException, IOException {
		printToFile(createFile(filename), sortHashMap(countWords(extractPoem(getURL(urlinput)))));
	}

}
