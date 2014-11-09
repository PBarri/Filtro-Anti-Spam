package controllers;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Prediction;
import algorithms.NaiveBayes;
import application.MainApplication;
import utilities.Utils;

public class PredictionDataController {
	
	private MainApplication mainApplication;
	
	private ObservableList<Prediction> predictionsData = FXCollections.observableArrayList();
	
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
	private Label percentage;
	
	public PredictionDataController(){
		
	}
	
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
	
	public void getPredictionsData(NaiveBayes alg){
		for(Entry<String, List<Float>> entry : alg.getPredictResults().entrySet()){
			Prediction p = new Prediction();
			List<String> aux = Utils.convertPredictions(entry.getValue());
			p.setFilename(entry.getKey());
			p.setSpamProbability(aux.get(0));
			p.setHamProbability(aux.get(1));
			p.setCategory(aux.get(2));
			p.setRealCategory(aux.get(3));
			predictionsData.add(p);
		}
		table.setItems(predictionsData);
		this.totalDocuments.setText(alg.getnPredictDocuments().toString());
		this.spamDocuments.setText(alg.getnPredictSpamDocuments().toString());
		this.hamDocuments.setText(alg.getnPredictHamDocuments().toString());
		this.wellClasiffied.setText(alg.getWellAnalized().toString());
		this.badClassified.setText(alg.getBadAnalized().toString());
		Float percentage = (alg.getWellAnalized().floatValue() / alg.getnPredictDocuments()) * 100;
		DecimalFormat df = new DecimalFormat("#.##");
		this.percentage.setText(df.format(percentage) + " %");
	}

}
