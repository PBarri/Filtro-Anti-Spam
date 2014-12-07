package controllers;

import java.io.File;
import java.util.Optional;
import java.util.prefs.Preferences;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import model.Probability;

import org.controlsfx.dialog.Dialogs;

import tasks.PredictTask;
import utilities.Utils;
import algorithms.NaiveBayes;
import application.MainApplication;
/**
 * Clase controladora de la pantalla de datos del entrenamiento
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
@SuppressWarnings("deprecation")
public class NaiveBayesDataController {

	// Referencia a la aplicación
	private MainApplication mainApplication;
	
	// Lista para ver las probabilidades
	private ObservableList<Probability> probabilitiesData = FXCollections.observableArrayList();
	
	@FXML
	private TableView<Probability> table;
	
	@FXML
	private TableColumn<Probability, String> wordColumn;
	
	@FXML
	private TableColumn<Probability, String> spamColumn;
	
	@FXML
	private TableColumn<Probability, String> hamColumn;
	
	@FXML
	private Label rootPathLabel;
	
	@FXML
	private Label nDocumentsLabel;
	
	@FXML
	private Label nSpamDocsLabel;
	
	@FXML
	private Label nHamDocsLabel;
	
	@FXML
	private Label wordsAnalizedLabel;
	
	@FXML
	private Label initSpamProb;
	
	@FXML
	private Label initHamProb;
	
	public NaiveBayesDataController(){
		
	}
	
	/**
	 * Método que se ejecuta al inicializar el controlador
	 */
	@FXML
	private void initialize(){
		// Se rellena la tabla con los datos de las probabilidades
		wordColumn.setCellValueFactory(cellData -> cellData.getValue().getWord());
		spamColumn.setCellValueFactory(cellData -> cellData.getValue().getSpamProbability());
		hamColumn.setCellValueFactory(cellData -> cellData.getValue().getHamProbability());
		probabilitiesData.clear();
	}
	
	public MainApplication getMainApplication(){
		return mainApplication;
	}
	
	public void setMainApplication(MainApplication application){
		this.mainApplication = application;
	}
	
	public void getProbabilities(NaiveBayes alg){
		probabilitiesData.addAll(alg.getProbabilitiesList());
		table.setItems(probabilitiesData);
	}
	
	/**
	 * Método que rellena la interfaz con los datos del algoritmo
	 * @param alg Instancia del algoritmo que ha sido entrenado
	 */
	public void getAlgorithmData(NaiveBayes alg){
		this.rootPathLabel.setText(alg.getPath());
		this.nDocumentsLabel.setText(alg.getnDocuments().toString());
		this.nSpamDocsLabel.setText(alg.getnSpamDocuments().toString());
		this.nHamDocsLabel.setText(alg.getnHamDocuments().toString());
		this.wordsAnalizedLabel.setText(alg.getTotalWords().toString());
		this.initSpamProb.setText(Utils.getPercentage(alg.getInitSpamProb(), null));
		this.initHamProb.setText(Utils.getPercentage(alg.getInitHamProb(), null));
	}
	
	/**
	 * Método que se ejecuta al pulsar el botón de Atrás.
	 * Este método vuelve a la pantalla de inicio.
	 */
	@FXML
	public void back(){
		mainApplication.showHome(false);
	}
	
	/**
	 * Método que se ejecuta al pulsar el botón de Nuevo Entrenamiento
	 */
	@FXML
	public void newTrain(){
		Alert alert = Utils.createAlert(AlertType.CONFIRMATION, "Confirmar acción", "¿Desea crear un nuevo entrenamiento?\nPerderá todos los datos no guardados", null, mainApplication);
		Optional<ButtonType> response = alert.showAndWait();
		if(response.get().equals(ButtonType.OK)){
			this.mainApplication.getPrimaryStage().setTitle("Filtro Anti Spam");
			this.mainApplication.showHome(true);
		}
	}
	
	/**
	 * Método que se ejecuta al pulsar el botón de predecir. Este método pide al usuario una ruta de un
	 * directorio para usar esos datos para una predicción.
	 */
	@FXML
	public void predict(){
		Preferences pref = Preferences.userNodeForPackage(MainApplication.class);
		String filePath = pref.get("predictDirectory", System.getProperty("user.home"));
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Escoge un archivo o directorio");
		File initialFile = new File(filePath);
		chooser.setInitialDirectory(initialFile);
		
		File file = chooser.showDialog(mainApplication.getPrimaryStage());
		
		if(file != null){
			pref.put("predictDirectory", file.getAbsolutePath());
			NaiveBayes alg = mainApplication.getAlg();
			Task<Void> task = new PredictTask(mainApplication, file.getAbsolutePath());
			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					mainApplication.setAlg(alg);
					mainApplication.showPredictions();
				}
			});
			task.setOnFailed(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent arg0) {
					Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error. Por favor inténtelo de nuevo", null, mainApplication);
					alert.showAndWait();
					return;
				}
			});
			Thread t = new Thread(task);
			t.setDaemon(true);
			Dialogs.create().owner(mainApplication.getPrimaryStage()).title("Prediciendo").masthead(null).showWorkerProgress(task);
			t.start();
		}
	}
	
}
