package views;

import model.NaiveBayesData;
import algorithms.NaiveBayes2;
import application.MainApplication;

public class NaiveBayesDataController {

	private MainApplication mainApp;
	
	public NaiveBayesDataController(){
		
	}

	public MainApplication getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApplication mainApp) {
		this.mainApp = mainApp;
	}
	
	public void getAlgorithmData(NaiveBayes2 alg, String path){
		NaiveBayesData data = new NaiveBayesData();
		data.setRootPath(path);
		data.setNDocuments(alg.getnDocuments());
		data.setNSpamDocuments(alg.getnSpamDocuments());
		data.setNHamDocuments(alg.getnHamDocuments());
		data.setWordsAnalized(alg.getVocabulary().size());
	}
	
}
