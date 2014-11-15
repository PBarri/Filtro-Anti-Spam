package controllers;

import java.io.File;
import java.util.prefs.Preferences;

import javafx.beans.binding.Bindings;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;

import org.controlsfx.dialog.Dialogs;

import tasks.TrainTask;
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
		TrainTask trainTask = new TrainTask(mainApplication, trainPath.getText());
		Thread t = new Thread(trainTask);
		t.setDaemon(true);
		Dialogs.create().owner(mainApplication.getPrimaryStage()).title("Entrenando").masthead(null).showWorkerProgress(trainTask);
		trainTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			
			@Override
			public void handle(WorkerStateEvent event) {
				mainApplication.setAlg(alg);
				mainApplication.getPrimaryStage().setTitle("Filtro Anti-Spam - Nuevo[Sin guardar]");
				if(new Double(slider.getValue()).intValue() != 100){
					mainApplication.showTrainPredictData();
				}else{
					mainApplication.showNaiveBayesData();
				}		
			}
		});
		
		t.start();
/*		try {
			alg.train(trainPath.getText(), new Double(slider.getValue()).intValue());
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
			e.printStackTrace();
			Dialogs.create().title("Error").masthead(null).message("Se ha producido un error").showError();
			return;
		}*/
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