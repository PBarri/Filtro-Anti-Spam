package controllers;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import org.controlsfx.dialog.Dialogs;

import algorithms.NaiveBayes;
import application.MainApplication;
import exceptions.InvalidPathException;
import exceptions.OpenFileException;

@SuppressWarnings("deprecation")
public class HomeController {

	private MainApplication mainApplication;
	
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
	
	public HomeController(){
	
	}
	
	public MainApplication getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApplication mainApplication) {
		this.mainApplication = mainApplication;
	}
	
	@FXML
	private void load(){
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Escoge directorio inicial");
		File initialFile = new File(System.getProperty("user.home"));
		directoryChooser.setInitialDirectory(initialFile);
		
		File file = directoryChooser.showDialog(mainApplication.getPrimaryStage());
		if(file != null){
			trainPath.setText(file.getAbsolutePath());
		}else{
			Dialogs.create().title("Error").masthead("Directorio erróneo").message("Se ha producido un error al abrir el directorio").showError();
		}
	}
	
	@FXML
	private void train(){
		NaiveBayes alg = new NaiveBayes();
		try {
			alg.train(trainPath.getText());
		} catch (NullPointerException e) {
			Dialogs.create().title("Error").masthead("Archivo erróneo").message(e.getMessage()).showError();
		} catch (InvalidPathException e) {
			Dialogs.create().title("Error").masthead("Ruta inválida").message(e.getMessage()).showError();
		} catch (OpenFileException e) {
			Dialogs.create().title("Error").masthead("Archivo erróneo").message(e.getMessage()).showError();
		}
		this.mainApplication.setAlg(alg);
		this.mainApplication.showNaiveBayesData();
	}

}