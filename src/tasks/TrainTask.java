package tasks;

import javafx.concurrent.Task;
import algorithms.NaiveBayes;
import application.MainApplication;

public class TrainTask extends Task<Void> {

	private final NaiveBayes alg;
	private final MainApplication mainApplication;
	
	public TrainTask(NaiveBayes alg, MainApplication app){
		this.alg = alg;
		this.mainApplication = app;
	}
	
	@Override
	protected Void call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public NaiveBayes getAlg() {
		return alg;
	}

	public MainApplication getMainApplication() {
		return mainApplication;
	}

}
