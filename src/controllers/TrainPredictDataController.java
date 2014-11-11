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

public class TrainPredictDataController {

	private MainApplication mainApplication;

	// Lista para ver las probabilidades
	private ObservableList<Probability> probabilitiesData = FXCollections.observableArrayList();
	private ObservableList<Prediction> predictionsData = FXCollections.observableArrayList();

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
	private Label percentage;
	
	public TrainPredictDataController(){
		
	}

	public MainApplication getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApplication mainApplication) {
		this.mainApplication = mainApplication;
	}
	
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
		this.percentage.setText(Utils.getPercentage(alg.getWellAnalized(), alg.getnPredictDocuments()));
	}

}
