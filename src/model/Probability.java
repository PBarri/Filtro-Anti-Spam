package model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * Clase que modela una probabilidad
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
@XmlRootElement(name = "probability")
@XmlAccessorType(XmlAccessType.FIELD)
public class Probability {
	
	// Atributos de la clase con anotaciones para cuando se guarde un entrenamiento
	
	@XmlElement(name = "word") 
	private String wordValue;
	@XmlTransient
	private final StringProperty word;
	@XmlElement(name = "spamProbability")
	private Float spamProbabilityValue;
	@XmlTransient
	private final StringProperty spamProbability;
	@XmlElement(name = "hamProbability")
	private Float hamProbabilityValue;
	@XmlTransient
	private final StringProperty hamProbability;
	
	// Constructor vacío
	public Probability(){
		this.word = new SimpleStringProperty();
		this.spamProbability = new SimpleStringProperty();
		this.hamProbability = new SimpleStringProperty();
	}
	
	// Constructor con valores iniciales
	public Probability(String word, Float spamProbability, Float hamProbability){
		this.word = new SimpleStringProperty(word);
		this.spamProbability = new SimpleStringProperty(spamProbability.toString());
		this.hamProbability = new SimpleStringProperty(hamProbability.toString());
		this.wordValue = word;
		this.spamProbabilityValue = spamProbability;
		this.hamProbabilityValue = hamProbability;
	}

	// Getters y setters
	
	public StringProperty getWord() {
		return word;
	}

	public StringProperty getSpamProbability() {
		return spamProbability;
	}

	public StringProperty getHamProbability() {
		return hamProbability;
	}
	
	public String getWordValue() {
		return wordValue;
	}

	public Float getSpamProbabilityValue() {
		return spamProbabilityValue;
	}

	public Float getHamProbabilityValue() {
		return hamProbabilityValue;
	}
	
	public void setWord(String word){
		this.word.set(word);
		this.wordValue = word;
	}
	
	public void setSpamProbability(Float spamProbability){
		this.spamProbability.set(spamProbability.toString());
		this.spamProbabilityValue = spamProbability;
	}
	
	public void setHamProbability(Float hamProbability){
		this.hamProbability.set(hamProbability.toString());
		this.hamProbabilityValue = hamProbability;
	}

}
