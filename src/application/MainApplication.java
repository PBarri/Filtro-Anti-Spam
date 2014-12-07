package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import algorithms.NaiveBayes;
import controllers.HomeController;
import controllers.MainApplicationController;
import controllers.NaiveBayesDataController;
import controllers.PredictionDataController;
import controllers.TrainPredictDataController;
/**
 * Clase principal de la aplicación.
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class MainApplication extends Application {

	// Ventana de la aplicación
	private Stage primaryStage;
	// Pantalla principal
	private BorderPane mainWindow;
	// Clase del algoritmo
	private NaiveBayes alg;
	// Ruta del archivo (Se usará para salvar y guardar datos del algoritmo)
	private File filePath;
	
	
	// Getters y setters

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
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

	public File getFilePath() {
		return filePath;
	}

	public void setFilePath(File filePath) {
		this.filePath = filePath;
	}
	
	// Fin getters y setters
	
	/**
	 * Método main que lanza la aplicación
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Filtro Anti Spam");
		this.primaryStage.getIcons().add(new Image("/css/ico.png"));
		
		initMainWindow();
		showHome(true);
	}

	/**
	 * Método que crea la ventana de la aplicación
	 */
	public void initMainWindow() {
		try{
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("/views/MainApplication.fxml"));
			mainWindow = (BorderPane) loader.load();
			
			MainApplicationController controller = loader.getController();
			controller.setMainApplication(this);
			
			Scene scene = new Scene(mainWindow);
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Método que carga los datos de un entrenamiento del algoritmo
	 */
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
	
	/**
	 * Método que muestra la pantalla de inicio de la aplicación
	 * @param init Si es verdadero, reinicia los datos del algoritmo
	 */
	@SuppressWarnings("static-access")
	public void showHome(Boolean init){
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("/views/Home.fxml"));
			AnchorPane home = (AnchorPane) loader.load();
			
			mainWindow.setCenter(home);
			mainWindow.setMargin(home, new Insets(5, 15, 5, 15));
			
			if(init)
				this.alg = new NaiveBayes();
			
			HomeController controller = loader.getController();
			controller.setMainApplication(this);
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Método que muestra los datos de la predicción 
	 */
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
	
	/**
	 * Método que muestra los datos del entrenamiento y la predicción cuando se ha usado
	 * un mismo conjunto para ambos
	 */
	public void showTrainPredictData() {
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("/views/TrainPredictData.fxml"));
			AnchorPane trainPredictData = (AnchorPane) loader.load();
			
			mainWindow.setCenter(trainPredictData);
			
			TrainPredictDataController controller = loader.getController();
			controller.setMainApplication(this);
			controller.getData(alg);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
