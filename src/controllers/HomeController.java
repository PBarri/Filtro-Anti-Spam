package controllers;

import java.io.File;
import java.util.prefs.Preferences;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import org.controlsfx.dialog.Dialogs;

import tasks.PredictTask;
import tasks.TrainPredictTask;
import tasks.TrainTask;
import utilities.SliderConverter;
import utilities.Utils;
import algorithms.NaiveBayes;
import application.MainApplication;

/**
 * Clase controladora de la pantalla de inicio
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
@SuppressWarnings("deprecation")
public class HomeController {

	// Referencia a la aplicación
	private MainApplication mainApplication;
	
	// Inicio de atributos que conectan con la interfaz
	
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
	
	@FXML
	private Slider thresholdSlider;
	
	@FXML
	private TextField thresholdText;
	
	// Fin de los atributos que conectan con la interfaz
	
	public HomeController(){
	
	}
	
	public MainApplication getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApplication mainApplication) {
		this.mainApplication = mainApplication;
	}
	
	@FXML
	/**
	 * Método que se ejecuta al inicializar el controlador
	 */
	private void initialize(){
		// Conecta los dos sliders con la caja de texto que tienen asociada
		Bindings.bindBidirectional(sliderText.textProperty(), slider.valueProperty(), new SliderConverter());
		Bindings.bindBidirectional(thresholdText.textProperty(), thresholdSlider.valueProperty(), new SliderConverter());
	}
	
	/**
	 * Método para cargar la ruta del entrenamiento. Esta ruta debe contener los datos del entrenamiento 
	 */
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
	
	/**
	 * Método que se ejecuta al pulsar el botón de entrenar
	 */
	@FXML
	private void train(){
		// Coge los datos del algoritmo, del porcentaje y el umbral introducidos por el usuario
		NaiveBayes alg = mainApplication.getAlg();
		Integer percentage = new Double(slider.getValue()).intValue();
		Integer threshold = new Double(thresholdSlider.getValue()).intValue();
		// Actualizamos el valor del umbral en el algoritmo
		alg.setThreshold(threshold);
		Task<Void> task;
		// Alerta en el caso de que el umbral sea inferior a 1
		if(threshold <= 1){
			Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "El umbral debe de ser mayor que 1", null, mainApplication);
			alert.showAndWait();
		}
		// Comprobamos si el porcentaje es distinto de 100
		if(percentage != 100){
			// Si está por debajo de 20, da un error al usuario
			if(percentage < 20){
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "El porcentaje de correos dedicado al entrenamiento no puede ser menor que 20", null, mainApplication);
				//Dialogs.create().title("Error").masthead(null).message("El porcentaje de correos dedicado al entrenamiento no puede ser menor que 20").showError();
				alert.showAndWait();
				return;
			}else{
				// Inicializamos la Task con una tarea que ejecuta el entrenamiento y la predicción.
				task = new TrainPredictTask(mainApplication, trainPath.getText(), percentage, threshold);
				// Creamos un evento que se ejecutará cuando la tarea termine, que muestre la pantalla con los datos.
				task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						mainApplication.setAlg(alg);
						mainApplication.getPrimaryStage().setTitle("Filtro Anti-Spam - Nuevo[Sin guardar]");
						mainApplication.showTrainPredictData();
					}
				});
			}
		// En el caso de que el porcentaje sea 100
		}else{
			// Creamos una tarea que ejecute el entrenamiento
			task = new TrainTask(mainApplication, trainPath.getText(), threshold);
			// Creamos el evento que se ejecutará cuando la tarea termine
			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {				
				@Override
				public void handle(WorkerStateEvent event) {
					mainApplication.setAlg(alg);
					mainApplication.getPrimaryStage().setTitle("Filtro Anti-Spam - Nuevo[Sin guardar]");
					mainApplication.showNaiveBayesData();
				}
			});
		}
		// Creamos un evento que se ejecute si el entrenamiento o predicción falle, mostrando un error por pantalla
		task.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error. Por favor inténtelo de nuevo", null, mainApplication);
				alert.showAndWait();
				return;
			}
		});
		// Creamos un hilo asociado a la tarea que acabamos de crear
		Thread t = new Thread(task);
		// Lo ponemos como demonio (si la aplicación termina, el hilo creado también)
		t.setDaemon(true);
		// Asociamos un popup al hilo que muestre el progreso de la tarea
		Dialogs.create().owner(mainApplication.getPrimaryStage()).title("Entrenando").masthead(null).showWorkerProgress(task);		
		// Ejecutamos el hilo
		t.start();
	}
	
	/**
	 * Método que carga la ruta del directorio que se va a predecir
	 */
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
	
	/**
	 * Método que se ejecuta al pulsar el botón de predecir
	 */
	@FXML
	private void predict(){
		// Cogemos los datos del algoritmo
		NaiveBayes alg = mainApplication.getAlg();
		// Mostramos un error si no hay ningún conjunto entrenado
		if(alg.getProbabilities().isEmpty()){
			Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Debe entrenar un conjunto de correos antes de clasificar", null, mainApplication);
			//Dialogs.create().title("Error").masthead(null).message("Debe entrenar un conjunto de correos antes de clasificar").showError();
			alert.showAndWait();
			return;
		}
		// Creamos una tarea que ejecute la predicción y muestre los errores en el caso de que los haya
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
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error. Por favor inténtelo de nuevo", null, mainApplication);
				//Dialogs.create().title("Error").masthead(null).message("Se ha producido un error. Por favor inténtelo de nuevo").showError();
				alert.showAndWait();
				return;
			}
		});
		// Creamos un hilo asociado a la tarea anterior, y lo iniciamos
		Thread t = new Thread(task);
		t.setDaemon(true);
		Dialogs.create().owner(mainApplication.getPrimaryStage()).title("Prediciendo").masthead(null).showWorkerProgress(task);
		t.start();
	}
}