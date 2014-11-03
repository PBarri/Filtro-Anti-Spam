package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Probability {
	
	private final StringProperty word;
	private final StringProperty spamProbability;
	private final StringProperty hamProbability;
	
	public Probability(){
		this(null, null, null);
	}
	
	public Probability(String word, String spamProbability, String hamProbability){
		this.word = new SimpleStringProperty(word);
		this.spamProbability = new SimpleStringProperty(spamProbability);
		this.hamProbability = new SimpleStringProperty(hamProbability);
	}

	public StringProperty getWord() {
		return word;
	}

	public StringProperty getSpamProbability() {
		return spamProbability;
	}

	public StringProperty getHamProbability() {
		return hamProbability;
	}
	
	public void setWord(String word){
		this.word.set(word);
	}
	
	public void setSpamProbability(String spamProbability){
		this.spamProbability.set(spamProbability);
	}
	
	public void setHamProbability(String hamProbability){
		this.hamProbability.set(hamProbability);
	}

}
