package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Probability;
import utilities.Utils;
import algorithms.NaiveBayes;
import application.MainApplication;

public class NaiveBayesDataController {

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
	
	@FXML
	private void initialize(){
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
	
	public void getAlgorithmData(NaiveBayes alg){
		this.rootPathLabel.setText(alg.getPath());
		this.nDocumentsLabel.setText(alg.getnDocuments().toString());
		this.nSpamDocsLabel.setText(alg.getnSpamDocuments().toString());
		this.nHamDocsLabel.setText(alg.getnHamDocuments().toString());
		this.wordsAnalizedLabel.setText(alg.getTotalWords().toString());
		this.initSpamProb.setText(Utils.getPercentage(alg.getInitSpamProb(), null));
		this.initHamProb.setText(Utils.getPercentage(alg.getInitHamProb(), null));
	}
	
}
