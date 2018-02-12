package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class App extends Application {
	@Override
	public void start(Stage primaryStage) 
	throws IOException {
//		primaryStage.initStyle(StageStyle.UNIFIED);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/application/UI.fxml"));
		BorderPane root = (BorderPane)loader.load();
		
		primaryStage.setTitle("Song Library");
		
		Controller controller = loader.getController();
		controller.start();
		controller.allowWindowDrag(primaryStage);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
