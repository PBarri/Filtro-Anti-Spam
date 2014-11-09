package tasks;

import javafx.concurrent.Task;
import algorithms.NaiveBayes;

public class TrainTask extends Task<Void> {

	private final NaiveBayes alg;
	
	public TrainTask(NaiveBayes alg){
		this.alg = alg;
	}
	
	@Override
	protected Void call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
