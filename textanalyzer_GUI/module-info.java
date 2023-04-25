module textanalyzer_GUI {
	requires javafx.controls;
	requires javafx.graphics;
	requires org.junit.jupiter.api;
	requires java.sql;
	
	opens application to javafx.graphics, javafx.fxml;
}
