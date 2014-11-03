package views;

import java.util.List;
import java.util.Map.Entry;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Probability;
import algorithms.NaiveBayes2;
import application.MainApplication;

public class ProbabilitiesOverviewController {
	
	@FXML
	private TableView<Probability> table;
	
	@FXML
	private TableColumn<Probability, String> wordColumn;
	
	@FXML
	private TableColumn<Probability, String> spamColumn;
	
	@FXML
	private TableColumn<Probability, String> hamColumn;
	
	private MainApplication mainApp;
	
	public ProbabilitiesOverviewController(){
		
	}
	
	@FXML
	private void initialize(){
		wordColumn.setCellValueFactory(cellData -> cellData.getValue().getWord());
		spamColumn.setCellValueFactory(cellData -> cellData.getValue().getSpamProbability());
		hamColumn.setCellValueFactory(cellData -> cellData.getValue().getHamProbability());
	}
	
	public MainApplication getMainApplication(){
		return mainApp;
	}
	
	public void setMainApplication(MainApplication app){
		this.mainApp = app;
	}
	
	public void getProbabilities(NaiveBayes2 alg, ObservableList<Probability> ol){
		alg.train("C:\\Users\\Pablo\\UNIVERSIDAD\\En curso\\4º\\Inteligencia Artificial 2\\Filtro Anti-Spam\\Corpus");
		String word, spamP, hamP;
		for(Entry<String, List<Float>> entry : alg.getProbabilities().entrySet()){
			word = entry.getKey();
			spamP = entry.getValue().get(0).toString();
			hamP = entry.getValue().get(1).toString();
			Probability p = new Probability(word, spamP, hamP);
			ol.add(p);
		}
		table.setItems(ol);
	}

}
