package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Probability;
import views.NaiveBayesDataController;
import algorithms.NaiveBayes2;

public class MainApplication extends Application {

	private Stage primaryStage;
	private BorderPane mainWindow;
	private NaiveBayes2 alg;
	
	// Lista para ver las probabilidades
	private ObservableList<Probability> probabilitiesData = FXCollections.observableArrayList();

	public ObservableList<Probability> getProbabilitiesData() {
		return probabilitiesData;
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Filtro Anti Spam");
		
		this.alg = new NaiveBayes2();
		
		initMainWindow();
		showProbabilitiesOverview();
	}

	public void initMainWindow() {
		try{
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("/views/MainApplication.fxml"));
			mainWindow = (BorderPane) loader.load();
			
			Scene scene = new Scene(mainWindow);
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();
			
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void showProbabilitiesOverview(){
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("/views/NaiveBayesData.fxml"));
			AnchorPane probabilitiesOverview = (AnchorPane) loader.load();
			
			mainWindow.setCenter(probabilitiesOverview);
			
			NaiveBayesDataController controller = loader.getController();
			
			String path = "C:\\Users\\Desarrollador\\Desktop\\Corpus";
			alg.train(path);
			
			controller.setMainApplication(this);
			controller.getProbabilities(alg, probabilitiesData);
			controller.getAlgorithmData(alg, path);
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
