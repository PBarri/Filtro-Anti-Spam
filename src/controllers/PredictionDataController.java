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
		predictionsData.addAll(alg.getPredictionList());
		table.setItems(predictionsData);
		this.totalDocuments.setText(alg.getnPredictDocuments().toString());
		this.spamDocuments.setText(alg.getnPredictSpamDocuments().toString());
		this.hamDocuments.setText(alg.getnPredictHamDocuments().toString());
		this.wellClasiffied.setText(alg.getWellAnalized().toString());
		this.badClassified.setText(alg.getBadAnalized().toString());
		this.percentage.setText(Utils.getPercentage(alg.getWellAnalized(), alg.getnPredictDocuments()));
	}
	
	@FXML
	public void back(){
		Alert alert = Utils.createAlert(AlertType.CONFIRMATION, "Confirmar acci�n", "�Desea volver atr�s?\nPerder� los datos de la predicci�n", null, mainApplication);
		Optional<ButtonType> response = alert.showAndWait();
		if(response.get().equals(ButtonType.OK))
			this.mainApplication.showNaiveBayesData();
//		Action response = Dialogs.create().title("Confirmar acci�n").masthead(null).message("�Desea volver atr�s?\nPerder� los datos de la predicci�n").showConfirm();
//		if(response.equals(Dialog.ACTION_YES)){
//			this.mainApplication.showNaiveBayesData();
//		}
	}

}
