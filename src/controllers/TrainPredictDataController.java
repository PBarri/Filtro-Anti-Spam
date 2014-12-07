package controllers;

import utilities.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Prediction;
import model.Probability;
import algorithms.NaiveBayes;
import application.MainApplication;
/**
 * Clase controladora de la pantalla en la que se muestra el entrenamiento y la predicción
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class TrainPredictDataController {

	// Referencia de la aplicación
	private MainApplication mainApplication;

	// Lista para ver las probabilidades del entrenamiento y la probabilidad
	private ObservableList<Probability> probabilitiesData = FXCollections.observableArrayList();
	private ObservableList<Prediction> predictionsData = FXCollections.observableArrayList();

	// Atributos para conectar los datos que se quieren mostrar con la interfaz
	@FXML
	private TableView<Probability> algorithmDataTable;

	@FXML
	private TableColumn<Probability, String> algorithmWordColumn;

	@FXML
	private TableColumn<Probability, String> algorithmSpamColumn;

	@FXML
	private TableColumn<Probability, String> algorithmHamColumn;
	
	@FXML
	private TableView<Prediction> predictionsDataTable;
	
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
	
	public TrainPredictDataController(){
		
	}

	public MainApplication getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApplication mainApplication) {
		this.mainApplication = mainApplication;
	}
	
	/**
	 * Método que se ejecuta al cargar el controlador. En el se inicializan los datos de las tablas
	 */
	@FXML
	private void initialize(){
		algorithmWordColumn.setCellValueFactory(cellData -> cellData.getValue().getWord());
		algorithmSpamColumn.setCellValueFactory(cellData -> cellData.getValue().getSpamProbability());
		algorithmHamColumn.setCellValueFactory(cellData -> cellData.getValue().getHamProbability());
		fileName.setCellValueFactory(cellData -> cellData.getValue().getFilename());
		spamColumn.setCellValueFactory(cellData -> cellData.getValue().getSpamProbability());
		hamColumn.setCellValueFactory(cellData -> cellData.getValue().getHamProbability());
		category.setCellValueFactory(cellData -> cellData.getValue().getCategory());
		realCategory.setCellValueFactory(cellData -> cellData.getValue().getRealCategory());
		probabilitiesData.clear();
		predictionsData.clear();
	}
	
	/**
	 * Método que recoge todos los datos que se tienen que mostrar en las tablas
	 * @param alg Instancia del algoritmo NaiveBayes
	 */
	public void getData(NaiveBayes alg){
		probabilitiesData.addAll(alg.getProbabilitiesList());
		algorithmDataTable.setItems(probabilitiesData);
		predictionsData.addAll(alg.getPredictionList());
		predictionsDataTable.setItems(predictionsData);
		this.rootPathLabel.setText(alg.getPath());
		this.nDocumentsLabel.setText(alg.getnDocuments().toString());
		this.nSpamDocsLabel.setText(alg.getnSpamDocuments().toString());
		this.nHamDocsLabel.setText(alg.getnHamDocuments().toString());
		this.wordsAnalizedLabel.setText(alg.getTotalWords().toString());
		this.initSpamProb.setText(Utils.getPercentage(alg.getInitSpamProb(), null));
		this.initHamProb.setText(Utils.getPercentage(alg.getInitHamProb(), null));
		this.totalDocuments.setText(alg.getnPredictDocuments().toString());
		this.spamDocuments.setText(alg.getnPredictSpamDocuments().toString());
		this.hamDocuments.setText(alg.getnPredictHamDocuments().toString());
		this.wellClasiffied.setText(alg.getWellAnalized().toString());
		this.badClassified.setText(alg.getBadAnalized().toString());
		this.wellSpam.setText(alg.getWellSpam().toString());
		this.wellHam.setText(alg.getWellHam().toString());
		this.badSpam.setText(alg.getBadSpam().toString());
		this.badHam.setText(alg.getBadHam().toString());
		this.percentage.setText(Utils.getPercentage(alg.getWellAnalized(), alg.getnPredictDocuments()));
		this.spamPercentage.setText(Utils.getPercentage(alg.getWellSpam(), alg.getnPredictSpamDocuments()));
		this.hamPercentage.setText(Utils.getPercentage(alg.getWellHam(), alg.getnPredictHamDocuments()));
	}

}
