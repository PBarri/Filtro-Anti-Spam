package controllers;

import java.io.File;
import java.util.prefs.Preferences;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import model.Probability;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import tasks.PredictTask;
import utilities.Utils;
import algorithms.NaiveBayes;
import application.MainApplication;

@SuppressWarnings("deprecation")
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
	
	@FXML
	public void back(){
		mainApplication.showHome(false);
	}
	
	@FXML
	public void newTrain(){
		Action response = Dialogs.create().title("Confirmar acción").masthead(null).message("¿Desea crear un nuevo entrenamiento?\nPerderá todos los datos no guardados").showConfirm();
		if (response.equals(Dialog.ACTION_YES)) {
			this.mainApplication.getPrimaryStage().setTitle("Filtro Anti Spam");
			this.mainApplication.showHome(true);
		}
	}
	
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
			try {
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
						Dialogs.create().title("Error").masthead(null).message("Se ha producido un error. Por favor inténtelo de nuevo").showError();
						return;
					}
				});
				Thread t = new Thread(task);
				t.setDaemon(true);
				Dialogs.create().owner(mainApplication.getPrimaryStage()).title("Prediciendo").masthead(null).showWorkerProgress(task);
				t.start();
			} catch (Exception e) {
				Dialogs.create().title("Error").masthead(null).message("Se ha producido un error. Por favor inténtelo de nuevo").showError();
				return;
			}
		}
	}
	
}
