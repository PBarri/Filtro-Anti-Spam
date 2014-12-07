package controllers;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Prediction;
import utilities.Utils;
import algorithms.NaiveBayes;
import application.MainApplication;
/**
 * Clase controladora de la pantalla en la que se muestran los datos de la predicción
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class PredictionDataController {
	
	// Referencia a la aplicación
	private MainApplication mainApplication;
	// Lista para mostrar los datos de la predicción
	private ObservableList<Prediction> predictionsData = FXCollections.observableArrayList();
	
	// Atributos para conectar los datos que se quieren mostrar con la interfaz
	@FXML
	private TableView<Prediction> table;
	
	@FXML
	private TableColumn<Prediction, String> fileName;
	
	@FXML
	private TableColumn<Prediction, String> spamColumn;
	
	@FXML
	private TableColumn<Prediction, String> hamColumn;
	
	@FXML
	private TableColumn<Prediction, String> category;
	
	@FXML
	private TableColumn<Prediction, String> realCategory;
	
	@FXML
	private Label totalDocuments;
	
	@FXML
	private Label spamDocuments;
	
	@FXML
	private Label hamDocuments;
	
	@FXML
	private Label wellClasiffied;
	
	@FXML
	private Label badClassified;
	
	@FXML
	private Label wellSpam;
	
	@FXML
	private Label badSpam;
	
	@FXML
	private Label wellHam;
	
	@FXML
	private Label badHam;
	
	@FXML
	private Label percentage;
	
	@FXML
	private Label spamPercentage;
	
	@FXML
	private Label hamPercentage;
	
	public PredictionDataController(){
		
	}
	
	/**
	 * Método que se ejecuta al inicializarse el controlador. En este método se inicializan los valores de la tabla
	 */
	@FXML
	private void initialize(){
		fileName.setCellValueFactory(cellData -> cellData.getValue().getFilename());
		spamColumn.setCellValueFactory(cellData -> cellData.getValue().getSpamProbability());
		hamColumn.setCellValueFactory(cellData -> cellData.getValue().getHamProbability());
		category.setCellValueFactory(cellData -> cellData.getValue().getCategory());
		realCategory.setCellValueFactory(cellData -> cellData.getValue().getRealCategory());
		predictionsData.clear();
	}

	public MainApplication getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApplication mainApplication) {
		this.mainApplication = mainApplication;
	}
	
	/**
	 * Método que se ejecuta para mostrar los datos de la predicción del algoritmo
	 * @param alg Instancia del algoritmo de NaiveBayes
	 */
	public void getPredictionsData(NaiveBayes alg){			
		predictionsData.addAll(alg.getPredictionList());
		table.setItems(predictionsData);
		this.totalDocuments.setText(alg.getnPredictDocuments().toString());
		this.spamDocuments.setText(alg.getnPredictSpamDocuments().toString());
		this.hamDocuments.setText(alg.getnPredictHamDocuments().toString());
		this.wellClasiffied.setText(alg.getWellAnalized().toString());
		this.badClassified.setText(alg.getBadAnalized().toString());
		this.wellSpam.setText(alg.getWellSpam().toString());
		this.badSpam.setText(alg.getBadSpam().toString());
		this.wellHam.setText(alg.getWellHam().toString());
		this.badHam.setText(alg.getBadHam().toString());
		this.percentage.setText(Utils.getPercentage(alg.getWellAnalized(), alg.getnPredictDocuments()));
		this.spamPercentage.setText(Utils.getPercentage(alg.getWellSpam(), alg.getnPredictSpamDocuments()));
		this.hamPercentage.setText(Utils.getPercentage(alg.getWellHam(), alg.getnPredictHamDocuments()));
	}
	
	/**
	 * Método que se ejecuta al pulsar el botón de Atrás
	 */
	@FXML
	public void back(){
		Alert alert = Utils.createAlert(AlertType.CONFIRMATION, "Confirmar acción", "¿Desea volver atrás?\nPerderá los datos de la predicción", null, mainApplication);
		Optional<ButtonType> response = alert.showAndWait();
		if(response.get().equals(ButtonType.OK))
			this.mainApplication.showNaiveBayesData();
	}

}
