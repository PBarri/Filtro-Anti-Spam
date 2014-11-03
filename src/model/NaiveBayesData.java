package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NaiveBayesData {
	
	private final StringProperty rootPath;
	private final IntegerProperty nDocuments;
	private final IntegerProperty nSpamDocuments;
	private final IntegerProperty nHamDocuments;
	private final IntegerProperty wordsAnalized;
	
	public NaiveBayesData(){
		this(null, null, null, null, null);
	}
	
	public NaiveBayesData(String rootPath, Integer nDocuments, Integer nSpamDocuments, Integer nHamDocuments, Integer wordsAnalized){
		this.rootPath = new SimpleStringProperty(rootPath);
		this.nDocuments = new SimpleIntegerProperty(nDocuments);
		this.nSpamDocuments = new SimpleIntegerProperty(nSpamDocuments);
		this.nHamDocuments = new SimpleIntegerProperty(nHamDocuments);
		this.wordsAnalized = new SimpleIntegerProperty(wordsAnalized);
	}
	
	public StringProperty getRootPath() {
		return rootPath;
	}
	
	public void setRootPath(String rootPath){
		this.rootPath.set(rootPath);
	}
	
	public IntegerProperty getnDocuments() {
		return nDocuments;
	}
	
	public void setNDocuments(Integer nDocuments){
		this.nDocuments.set(nDocuments);
	}
	
	public IntegerProperty getnSpamDocuments() {
		return nSpamDocuments;
	}
	
	public void setNSpamDocuments(Integer spamDocuments){
		this.nSpamDocuments.set(spamDocuments);
	}
	
	public IntegerProperty getnHamDocuments() {
		return nHamDocuments;
	}
	
	public void setNHamDocuments(Integer hamDocuments){
		this.nHamDocuments.set(hamDocuments);
	}
	
	public IntegerProperty getWordsAnalized() {
		return wordsAnalized;
	}
	
	public void setWordsAnalized(Integer wordsAnalized){
		this.wordsAnalized.set(wordsAnalized);
	}	

}
