package controllers;

import java.io.File;
import java.util.prefs.Preferences;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import org.controlsfx.dialog.Dialogs;

import algorithms.NaiveBayes;
import application.MainApplication;
import exceptions.InvalidPathException;
import exceptions.NotTrainedException;
import exceptions.NotValidPercentageException;
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
		Preferences pref = Preferences.userNodeForPackage(MainApplication.class);
		String filePath = pref.get("trainDirectory", System.getProperty("user.home"));
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Escoge directorio inicial");
		File initialFile = new File(filePath);
		directoryChooser.setInitialDirectory(initialFile);
		
		File file = directoryChooser.showDialog(mainApplication.getPrimaryStage());
		if(file != null){
			trainPath.setText(file.getAbsolutePath());
			pref.put("trainDirectory", file.getAbsolutePath());
		}
	}
	
	@FXML
	private void train(){
		NaiveBayes alg = mainApplication.getAlg();
		try {
			alg.train(trainPath.getText(), 100);
		} catch (NullPointerException e) {
			Dialogs.create().title("Error").masthead("Archivo erróneo").message(e.getMessage()).showError();
			return;
		} catch (InvalidPathException e) {
			Dialogs.create().title("Error").masthead("Ruta inválida").message(e.getMessage()).showError();
			return;
		} catch (OpenFileException e) {
			Dialogs.create().title("Error").masthead("Archivo erróneo").message(e.getMessage()).showError();
			return;
		} catch (NotValidPercentageException e) {
			Dialogs.create().title("Error").masthead(null).message("El porcentaje no puede ser menor que el 20%").showError();
			return;
		} catch (Exception e) {
			Dialogs.create().title("Error").masthead(null).message("Se ha producido un error").showError();
			return;
		}
		this.mainApplication.setAlg(alg);
		this.mainApplication.showNaiveBayesData();
	}
	
	@FXML
	private void loadPredictionsFile(){
		Preferences pref = Preferences.userNodeForPackage(MainApplication.class);
		String filePath = pref.get("predictDirectory", System.getProperty("user.home"));
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Escoge un archivo o directorio");
		File initialFile = new File(filePath);
		chooser.setInitialDirectory(initialFile);
		
		File file = chooser.showDialog(mainApplication.getPrimaryStage());
		
		if(file != null){
			predictPath.setText(file.getAbsolutePath());
			pref.put("predictDirectory", file.getAbsolutePath());
		}
	}
	
	@FXML
	private void predict(){
		NaiveBayes alg = mainApplication.getAlg();
		try {
			alg.predict(predictPath.getText());
		} catch (OpenFileException e) {
			Dialogs.create().title("Error").masthead("Archivo erróneo").message(e.getMessage()).showError();
			return;
		} catch (NotTrainedException e) {
			Dialogs.create().title("Error").masthead("Entrenamiento erróneo").message("Debe entrenar un conjunto de correos antes de clasificar").showError();
			return;
		} catch (Exception e) {
			Dialogs.create().title("Error").masthead(null).message("Se ha producido un error. Por favor inténtelo de nuevo").showError();
			return;
		}
		this.mainApplication.setAlg(alg);
		this.mainApplication.showPredictions();
	}

}