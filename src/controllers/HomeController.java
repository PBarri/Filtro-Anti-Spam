package controllers;

import java.io.File;
import java.util.prefs.Preferences;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;

import org.controlsfx.dialog.Dialogs;

import tasks.PredictTask;
import tasks.TrainPredictTask;
import tasks.TrainTask;
import utilities.Utils;
import algorithms.NaiveBayes;
import application.MainApplication;

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
	
	@FXML
	private Slider slider;
	
	@FXML
	private TextField sliderText;
	
	public HomeController(){
	
	}
	
	public MainApplication getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApplication mainApplication) {
		this.mainApplication = mainApplication;
	}
	
	@FXML
	private void initialize(){
		Bindings.bindBidirectional(sliderText.textProperty(), slider.valueProperty(), new StringConverter<Number>() {

			@Override
			public Number fromString(String text) {
				return new Integer(text);
			}

			@Override
			public String toString(Number number) {
				return new Integer(number.intValue()).toString();
			}
		});
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
		Integer percentage = new Double(slider.getValue()).intValue();
		Task<Void> task;
		if(percentage != 100){
			if(percentage < 20){
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "El porcentaje de correos dedicado al entrenamiento no puede ser menor que 20", null, mainApplication);
				//Dialogs.create().title("Error").masthead(null).message("El porcentaje de correos dedicado al entrenamiento no puede ser menor que 20").showError();
				alert.showAndWait();
				return;
			}else{
				task = new TrainPredictTask(mainApplication, trainPath.getText(), percentage);
				task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						mainApplication.setAlg(alg);
						mainApplication.getPrimaryStage().setTitle("Filtro Anti-Spam - Nuevo[Sin guardar]");
						mainApplication.showTrainPredictData();
					}
				});
			}
		}else{
			task = new TrainTask(mainApplication, trainPath.getText());
			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {				
				@Override
				public void handle(WorkerStateEvent event) {
					mainApplication.setAlg(alg);
					mainApplication.getPrimaryStage().setTitle("Filtro Anti-Spam - Nuevo[Sin guardar]");
					mainApplication.showNaiveBayesData();
				}
			});
		}
		task.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error. Por favor int�ntelo de nuevo", null, mainApplication);
				//Dialogs.create().title("Error").masthead(null).message("Se ha producido un error. Por favor int�ntelo de nuevo").owner(mainApplication.getPrimaryStage()).showError();
				alert.showAndWait();
				return;
			}
		});
		Thread t = new Thread(task);
		t.setDaemon(true);
		Dialogs.create().owner(mainApplication.getPrimaryStage()).title("Entrenando").masthead(null).showWorkerProgress(task);		
		t.start();
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
		if(alg.getProbabilities().isEmpty()){
			Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Debe entrenar un conjunto de correos antes de clasificar", null, mainApplication);
			//Dialogs.create().title("Error").masthead(null).message("Debe entrenar un conjunto de correos antes de clasificar").showError();
			alert.showAndWait();
			return;
		}
		try {
			Task<Void> task = new PredictTask(mainApplication, predictPath.getText());
			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					mainApplication.setAlg(alg);
					mainApplication.showPredictions();
				}
			});
			task.setOnFailed(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error. Por favor int�ntelo de nuevo", null, mainApplication);
					//Dialogs.create().title("Error").masthead(null).message("Se ha producido un error. Por favor int�ntelo de nuevo").showError();
					alert.showAndWait();
					return;
				}
			});
			Thread t = new Thread(task);
			t.setDaemon(true);
			Dialogs.create().owner(mainApplication.getPrimaryStage()).title("Prediciendo").masthead(null).showWorkerProgress(task);
			t.start();
		} catch (Exception e) {
			Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error. Por favor int�ntelo de nuevo", null, mainApplication);
			//Dialogs.create().title("Error").masthead(null).message("Se ha producido un error. Por favor int�ntelo de nuevo").showError();
			alert.showAndWait();
			return;
		}
	}
}