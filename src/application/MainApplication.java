package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import algorithms.NaiveBayes;
import controllers.HomeController;
import controllers.MainApplicationController;
import controllers.NaiveBayesDataController;
import controllers.PredictionDataController;

public class MainApplication extends Application {

	private Stage primaryStage;
	private BorderPane mainWindow;
	private NaiveBayes alg;
	
	public BorderPane getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(BorderPane mainWindow) {
		this.mainWindow = mainWindow;
	}

	public NaiveBayes getAlg() {
		return alg;
	}

	public void setAlg(NaiveBayes alg) {
		this.alg = alg;
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Filtro Anti Spam");
		
		initMainWindow();
		showHome(true);
	}

	public void initMainWindow() {
		try{
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("/views/MainApplication.fxml"));
			mainWindow = (BorderPane) loader.load();
			
			MainApplicationController controller = loader.getController();
			controller.setMainApplication(this);
			
			Scene scene = new Scene(mainWindow);
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();
			
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void showNaiveBayesData(){
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("/views/NaiveBayesData.fxml"));
			AnchorPane naiveBayesData = (AnchorPane) loader.load();
			
			mainWindow.setCenter(naiveBayesData);
				
			NaiveBayesDataController controller = loader.getController();
			
			controller.setMainApplication(this);
			controller.getProbabilities(alg);
			controller.getAlgorithmData(alg);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void showHome(Boolean init){
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("/views/Home.fxml"));
			AnchorPane home = (AnchorPane) loader.load();
			
			mainWindow.setCenter(home);
			
			if(init)
				this.alg = new NaiveBayes();
			
			HomeController controller = loader.getController();
			controller.setMainApplication(this);
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void showPredictions(){
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("/views/PredictionsData.fxml"));
			AnchorPane predictionData = (AnchorPane) loader.load();
			
			mainWindow.setCenter(predictionData);
			
			PredictionDataController controller = loader.getController();
			controller.setMainApplication(this);
			controller.getPredictionsData(alg);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}
