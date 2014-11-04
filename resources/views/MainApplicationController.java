package views;

import java.io.File;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import org.controlsfx.dialog.Dialogs;

import algorithms.NaiveBayes2;
import application.MainApplication;

@SuppressWarnings("deprecation")
public class MainApplicationController {
	
	private MainApplication mainApp;
	
	@FXML
	private TextField trainPath;
	
	@FXML
	private TextField predictPath;
	
	@FXML
	private Button loadPredictFile;
	
	@FXML
	private Button loadTrainFile;
	
	@FXML
	private Button train;
	
	@FXML
	private Button predict;
	
	public MainApplicationController(){
		
	}

	public MainApplication getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApplication mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	private void initialize(){
	}
	
	@FXML
	private void close(){
		Platform.exit();
	}
	
	@FXML
	private void about(){
		String mensaje = "En esta aplicación se recrea el algoritmo de Naives Bayes ";
		mensaje += "y se aplica al filtrado de mensajes entre mensajes spam o no spam (ham)\n\n";
		mensaje += "Autores de la aplicación: \n";
		mensaje += "	Pablo Barrientos Lobato\n";
		mensaje += "	Alberto Salas Cantalejo";
		Dialogs.create().title("Acerca de...").masthead("Filtro Anti-Spam").message(mensaje).showInformation();
	}
	
	@FXML
	private void load(){
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Escoge directorio inicial");
		File initialFile = new File(System.getProperty("user.home"));
		directoryChooser.setInitialDirectory(initialFile);
		
		File file = directoryChooser.showDialog(mainApp.getPrimaryStage());
		if(file != null){
			trainPath.setText(file.getAbsolutePath());
		}else{
			Dialogs.create().title("Error").masthead("Directorio erróneo").message("Se ha producido un error al abrir el directorio");
		}
	}
	
	@FXML
	private void train(){
		NaiveBayes2 alg = new NaiveBayes2();
		alg.train(trainPath.getText());
		this.mainApp.setAlg(alg);
		this.mainApp.showNaiveBayesData();
	}

}
