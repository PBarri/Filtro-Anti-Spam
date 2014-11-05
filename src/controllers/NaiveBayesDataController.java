package controllers;

import java.util.List;
import java.util.Map.Entry;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Probability;
import algorithms.NaiveBayes;
import application.MainApplication;

public class NaiveBayesDataController {

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
	
	private MainApplication mainApplication;
	
	public NaiveBayesDataController(){
		
	}
	
	@FXML
	private void initialize(){
		wordColumn.setCellValueFactory(cellData -> cellData.getValue().getWord());
		spamColumn.setCellValueFactory(cellData -> cellData.getValue().getSpamProbability());
		hamColumn.setCellValueFactory(cellData -> cellData.getValue().getHamProbability());
	}
	
	public MainApplication getMainApplication(){
		return mainApplication;
	}
	
	public void setMainApplication(MainApplication application){
		this.mainApplication = application;
	}
	
	public void getProbabilities(NaiveBayes alg, ObservableList<Probability> ol){
		String word, spamP, hamP;
		Float probSpam, probHam;
		for(Entry<String, List<Float>> entry : alg.getProbabilities().entrySet()){
			probSpam = entry.getValue().get(0);
			probHam = entry.getValue().get(1);
			word = entry.getKey();
			spamP = probSpam.toString();
			hamP = probHam.toString();
			Probability p = new Probability(word, spamP, hamP);
			ol.add(p);
		}
		table.setItems(ol);
	}
	
	public void getAlgorithmData(NaiveBayes alg){
		this.rootPathLabel.setText(alg.getPath());
		this.nDocumentsLabel.setText(alg.getnDocuments().toString());
		this.nSpamDocsLabel.setText(alg.getnSpamDocuments().toString());
		this.nHamDocsLabel.setText(alg.getnHamDocuments().toString());
		this.wordsAnalizedLabel.setText(alg.getTotalWords().toString());
		this.initSpamProb.setText(alg.getInitSpamProb().toString());
		this.initHamProb.setText(alg.getInitHamProb().toString());
	}
	
}