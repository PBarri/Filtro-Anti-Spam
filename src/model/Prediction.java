package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Prediction {
	
	private final StringProperty filename;
	private final StringProperty spamProbability;
	private final StringProperty hamProbability;
	private final StringProperty category;
	private final StringProperty realCategory;
	
	public Prediction(){
		this(null, null, null, null, null);
	}
	
	public Prediction(String filename, String spamProb, String hamProb, String category, String realCategory){
		this.filename = new SimpleStringProperty(filename);
		this.spamProbability = new SimpleStringProperty(spamProb);
		this.hamProbability = new SimpleStringProperty(hamProb);
		this.category = new SimpleStringProperty(category);
		this.realCategory = new SimpleStringProperty(realCategory);
	}
	
	public StringProperty getFilename(){
		return filename;
	}
	
	public void setFilename(String filename){
		this.filename.set(filename);
	}

	public StringProperty getSpamProbability() {
		return spamProbability;
	}
	
	public void setSpamProbability(String spamProb){
		this.spamProbability.set(spamProb);
	}

	public StringProperty getHamProbability() {
		return hamProbability;
	}
	
	public void setHamProbability(String hamProb){
		this.hamProbability.set(hamProb);
	}

	public StringProperty getCategory() {
		return category;
	}
	
	public void setCategory(String category){
		this.category.set(category);
	}

	public StringProperty getRealCategory() {
		return realCategory;
	}
	
	public void setRealCategory(String realCategory){
		this.realCategory.set(realCategory);
	}

}
