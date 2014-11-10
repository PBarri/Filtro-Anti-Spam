package algorithms;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import model.Probability;

import com.google.common.io.Files;

import exceptions.InvalidPathException;
import exceptions.NotTrainedException;
import exceptions.OpenFileException;

@XmlRootElement(name = "NaiveBayes")
@XmlAccessorType(XmlAccessType.FIELD)
public class NaiveBayes {
	
	@XmlTransient
	private static final String SPAM = "spam";
	@XmlTransient
	private static final String HAM = "ham";

	// Para entrenamiento
	@XmlTransient
	private String path;
	@XmlElement(name = "totalDocuments")
	private Integer nDocuments;
	@XmlElement(name = "totalWords")
	private Integer totalWords;
	@XmlElement(name = "totalSpamDocuments")
	private Integer nSpamDocuments;
	@XmlElement(name = "totalHamDocuments")
	private Integer nHamDocuments;
	@XmlElement(name = "initialSpamProbability")
	private Float initSpamProb;
	@XmlElement(name = "initialHamProbability")
	private Float initHamProb;
	@XmlTransient
	private Map<String, Integer> spamWords;
	@XmlTransient
	private Map<String, Integer> hamWords;
	@XmlTransient
	private Map<String, List<Float>> probabilities;
	@XmlElementWrapper(name = "probabilities")
	@XmlElement(name = "probability")
	private List<Probability> probabilitiesList;
	@XmlTransient
	private Set<String> vocabulary;
	
	
	// Para predicción
	@XmlTransient
	private Map<String, List<Float>> predictResults;
	@XmlTransient
	private Integer nPredictDocuments;
	@XmlTransient
	private Integer nPredictSpamDocuments;
	@XmlTransient
	private Integer nPredictHamDocuments;
	@XmlTransient
	private Integer wellAnalized;
	@XmlTransient
	private Integer badAnalized;

	public NaiveBayes() {
		this.probabilities = new HashMap<String, List<Float>>();
		this.probabilitiesList = new ArrayList<Probability>();
		this.spamWords = new HashMap<String, Integer>();
		this.hamWords = new HashMap<String, Integer>();
		this.vocabulary = new HashSet<String>();
		this.totalWords = 0;
		this.nDocuments = 0;
		this.nSpamDocuments = 0;
		this.nHamDocuments = 0;
		this.initSpamProb = new Float(0.0);
		this.initHamProb = new Float(0.0);
		this.path = "";
		// List<Float> ==> [0] -> Probabilidad SPAM, [1] -> Probabilidad HAM, 
		// [2] -> Clasificación algoritmo {1 -> Spam, 2 -> Ham, 0 -> Error clasificación}, 
		// [3] -> Clasificación real {1 -> Spam, 2 -> Ham}
		this.predictResults = new HashMap<String, List<Float>>();
		this.nPredictDocuments = 0;
		this.nPredictSpamDocuments = 0;
		this.nPredictHamDocuments = 0;
		this.wellAnalized = 0;
		this.badAnalized = 0;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getnDocuments() {
		return nDocuments;
	}

	public void setnDocuments(Integer nDocuments) {
		this.nDocuments = nDocuments;
	}

	public Integer getTotalWords() {
		return totalWords;
	}

	public void setTotalWords(Integer totalWords) {
		this.totalWords = totalWords;
	}

	public Integer getnSpamDocuments() {
		return nSpamDocuments;
	}

	public void setnSpamDocuments(Integer nSpamDocuments) {
		this.nSpamDocuments = nSpamDocuments;
	}

	public Integer getnHamDocuments() {
		return nHamDocuments;
	}

	public void setnHamDocuments(Integer nHamDocuments) {
		this.nHamDocuments = nHamDocuments;
	}

	public Float getInitSpamProb() {
		return initSpamProb;
	}

	public void setInitSpamProb(Float initSpamProb) {
		this.initSpamProb = initSpamProb;
	}

	public Float getInitHamProb() {
		return initHamProb;
	}

	public void setInitHamProb(Float initHamProb) {
		this.initHamProb = initHamProb;
	}

	public Map<String, Integer> getSpamWords() {
		return spamWords;
	}

	public void setSpamWords(Map<String, Integer> spamWords) {
		this.spamWords = spamWords;
	}

	public Map<String, Integer> getHamWords() {
		return hamWords;
	}

	public void setHamWords(Map<String, Integer> hamWords) {
		this.hamWords = hamWords;
	}

	public Map<String, List<Float>> getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(Map<String, List<Float>> probabilities) {
		this.probabilities = probabilities;
	}

	public List<Probability> getProbabilitiesList() {
		return probabilitiesList;
	}

	public void setProbabilitiesList(List<Probability> probabilitiesList) {
		this.probabilitiesList = probabilitiesList;
	}

	public Set<String> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(Set<String> vocabulary) {
		this.vocabulary = vocabulary;
	}

	public Map<String, List<Float>> getPredictResults() {
		return predictResults;
	}

	public void setPredictResults(Map<String, List<Float>> predictResults) {
		this.predictResults = predictResults;
	}

	public Integer getnPredictDocuments() {
		return nPredictDocuments;
	}

	public void setnPredictDocuments(Integer nPredictDocuments) {
		this.nPredictDocuments = nPredictDocuments;
	}

	public Integer getnPredictSpamDocuments() {
		return nPredictSpamDocuments;
	}

	public void setnPredictSpamDocuments(Integer nPredictSpamDocuments) {
		this.nPredictSpamDocuments = nPredictSpamDocuments;
	}

	public Integer getnPredictHamDocuments() {
		return nPredictHamDocuments;
	}

	public void setnPredictHamDocuments(Integer nPredictHamDocuments) {
		this.nPredictHamDocuments = nPredictHamDocuments;
	}

	public Integer getWellAnalized() {
		return wellAnalized;
	}

	public void setWellAnalized(Integer wellAnalized) {
		this.wellAnalized = wellAnalized;
	}

	public Integer getBadAnalized() {
		return badAnalized;
	}

	public void setBadAnalized(Integer badAnalized) {
		this.badAnalized = badAnalized;
	}

	/**
	 * Método para cargar los correos de entrenamiento
	 * 
	 * @param path
	 *            -> Ruta padre en la que estén todos los archivos
	 * @throws OpenFileException 
	 * @throws InvalidPathException 
	 * @throws NullPointerException 
	 */
	public void train(String path) throws NullPointerException, InvalidPathException, OpenFileException{
		this.path = path;
		this.train();
		
//		//Estas 4 lineas siguientes corresponde al array prior que debe devolver el entrenamiento, no se si al unir los archivos lo borraste
//		Integer auxSpam = nSpamDocuments / nDocuments;
//		Integer auxham = nHamDocuments / nDocuments;
//		prior.put("spam", auxSpam);
//		prior.put("ham", auxham);
	}
	
	private void train() throws NullPointerException, InvalidPathException, OpenFileException {

		File file = new File(path);
		List<File> filesToAnalize = new ArrayList<File>();
		// Comprobamos que nos han pasado una ruta de una carpeta y no de un archivo
		if (file.isDirectory()) {
			iterateDirectories(file, filesToAnalize);
		} else {
			throw new InvalidPathException("La ruta es inválida");
		}
		nDocuments = filesToAnalize.size();
		
		for(File f : filesToAnalize){
			String[] fileWords = loadWords(f);
			totalWords += fileWords.length;
			if (f.getParentFile().getName().equals(SPAM)){
				nSpamDocuments++;
				for(String s : fileWords){
					if(!vocabulary.contains(s)){
						vocabulary.add(s);
					}
					spamWords.put(s, spamWords.getOrDefault(s, 0) + 1);
				}
			} else if (f.getParentFile().getName().equals(HAM)) {
				nHamDocuments++;
				for(String s : fileWords){
					if(!vocabulary.contains(s)){
						vocabulary.add(s);
					}
					hamWords.put(s, hamWords.getOrDefault(s, 0) + 1);
				}
			}
		}
		
		probabilities = generateProbabilities(spamWords, hamWords);

	}
	
	public void predict(String path) throws OpenFileException, NotTrainedException{
		File file = new File(path);
		List<File> filesToAnalize = new ArrayList<File>();
		// Vaciamos los resultados previos
		predictResults.clear();
		if(file.isDirectory()){
			iterateDirectories(file, filesToAnalize);
			for(File f : filesToAnalize){
				this.predict(f);
			}
			this.nPredictDocuments = filesToAnalize.size();
		}else{
			this.nPredictDocuments = 1;
			this.predict(file);
		}
	}
	
	private void predict(File file) throws OpenFileException, NotTrainedException {
		
		// Comprobamos que el Mapa de probabilidades contiene datos
		if(probabilities.isEmpty())
			throw new NotTrainedException();
		
		Float totalSpamProb = this.initSpamProb;
		Float totalHamProb = this.initHamProb;
		List<Float> results = new ArrayList<Float>();
//		Map<String, Integer> trainedWords = new HashMap<String, Integer>();
//		Map<String, Integer> untrainedWords = new HashMap<String, Integer>();
		
		String[] fileWords = loadWords(file);
		for(String s : fileWords){
			if(probabilities.containsKey(s)){
				Float tempSpamProb = Math.abs(new Float(Math.log10(probabilities.get(s).get(0))));
				Float tempHamProb = Math.abs(new Float(Math.log10(probabilities.get(s).get(1))));
				totalSpamProb += tempSpamProb;
				totalHamProb += tempHamProb;
			}else{
				// Aqui que tiene que haber ¿?
			}
		}
		results.add(totalSpamProb);
		results.add(totalHamProb);
		switch(totalSpamProb.compareTo(totalHamProb)){
		case 1: // Clasificado como SPAM
			results.add(new Float(1));
			break;
		case 0: // Error al clasificar
			results.add(new Float(0));
			break;
		case -1: // Clasificado como HAM
			results.add(new Float(2));
			break;
		}
		if(file.getParentFile().getName().equals(SPAM)){
			this.nPredictSpamDocuments++;
			results.add(new Float(1));
		}else if(file.getParentFile().getName().equals(HAM)){
			this.nPredictHamDocuments++;
			results.add(new Float(2));
		}
		
		if(results.get(2).equals(results.get(3))){
			wellAnalized++;
		}else{
			badAnalized++;
		}
		predictResults.put(file.getName(), results);
	}

	/**
	 * Método que recorre los directorios, insertando en filesToAnalize todos
	 * los archivos deseados
	 * 
	 * @param file -> Directorio/fichero a analizar
	 * @param filesToAnalize -> Lista de archivos que se utilizarán para cargar las palabras
	 */
	private void iterateDirectories(File file, List<File> filesToAnalize) {
		// Si el archivo es un directorio, se van recorriendo todos sus hijos
		// llamando recursivamente a este método
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				iterateDirectories(f, filesToAnalize);
			}
		} else { // Si es un archivo se manda a una función para que sea analizado
			// Comprobamos que los archivos están dentro de las carpetas spam o ham
			if(file.getParentFile().getName().toLowerCase().matches("spam|ham")){
				filesToAnalize.add(file);
			}
		}
	}

	/**
	 * Método que se utiliza para cargar en el mapa todas las palabras
	 * relevantes de un fichero
	 * 
	 * @param f
	 *            -> Archivo que se está analizando
	 * @param result
	 *            -> Mapa en el que se cargarán las palabras
	 * @throws OpenFileException
	 *             -> Error al abrir el archivo
	 */
	private String[] loadWords(File file) throws OpenFileException {
		try {
			// Pasamos a una cadena de texto el correo
			String content = Files.toString(file, Charset.defaultCharset());
			content.toLowerCase();
			String[] fileWords = content.split("([^a-z0-9])+");
			
			return fileWords;			
		} catch (IOException e) {
			throw new OpenFileException(file);
		}
	}

	/**
	 * Método consistente en generar las probabilidades de que una palabra sea o
	 * no spam
	 * 
	 * @param spamWords
	 *            -> Palabras obtenidas de los correos spam
	 * @param hamWords
	 *            -> Palabras obtenidas de los correos que no son spam
	 * @return Un mapa con la palabra como clave, y una lista de la forma
	 *         [Probabilidad de que sea spam, Probabilidad de que sea ham]
	 */
	private Map<String, List<Float>> generateProbabilities(Map<String, Integer> spamWords, Map<String, Integer> hamWords) {

		Map<String, List<Float>> result = new HashMap<String, List<Float>>();
		Float prob = new Float(1.0);
		
		
		// Se recorren los mapas para calcular las probabilidades
		for (Entry<String, Integer> entry : spamWords.entrySet()) {
			List<Float> aux = new ArrayList<Float>();
			// Calculamos la probabilidad de que la palabra sea spam
			prob = (entry.getValue().floatValue() +1)/ (totalWords + vocabulary.size()); //AQUI , el denominador tambien he cambiado , en el algoritmo y los otros lo tienen asi.(preguntame y te cuento)
			aux.add(0, prob);
			// Añadimos una probabilidad 0 de que no sea spam (por si no aparece
			// en el otro mapa)
			aux.add(1, new Float(1.0));
			result.put(entry.getKey(), aux);
		}

		for (Entry<String, Integer> entry : hamWords.entrySet()) {
			List<Float> aux = new ArrayList<Float>();
			// Calculamos la probabilidad de que la palabra sea ham
			prob = entry.getValue().floatValue() / (totalWords + vocabulary.size());//AQUI , el denominador tambien he cambiado , en el algoritmo y los otros lo tienen asi.
			// Apareció en el mapa de palabras spam
			if (result.containsKey(entry.getKey())) {
				aux = result.get(entry.getKey());
				aux.set(1, prob);
			} else { // Palabra que no apareció en los correos spam.
				// Probabilidad 0 de que sea spam
				aux.add(0, new Float(1.0));
				aux.add(1, prob);
			}
			result.put(entry.getKey(), aux);			
		}
		
		for(Entry<String, List<Float>> entry : result.entrySet()){
			String word = entry.getKey();
			Float spamP = entry.getValue().get(0);
			Float hamP = entry.getValue().get(1);
			Probability p = new Probability(word, spamP, hamP);
			probabilitiesList.add(p);
		}
		

		this.initSpamProb = Math.abs(new Float(Math.log10(nSpamDocuments.doubleValue() / nDocuments)));
		this.initHamProb = Math.abs(new Float(Math.log10(nHamDocuments.doubleValue() / nDocuments)));
		
		return result;
		
	}
	
	
	public void generatePercentage(Integer percentage , String path)throws OpenFileException, NotTrainedException, NullPointerException, InvalidPathException
	{
		File file = new File(path);
		
		//Una vez pasado el while en esta lista quedarian los correos para probar el algoritmo despues de entrenarlo
		List<File> allfiles = new ArrayList<File>();
		//En esta lista pasado el while estan los correos de entrenamiento
		List<File> entrenamiento = new ArrayList<>();
		
		this.iterateDirectories(file, allfiles);
		
		Integer max = allfiles.size()*percentage/100;
		Integer aux = 0;
		
		while(entrenamiento.size()<max)
		{	
			
			File f =  allfiles.get((int) (Math.random()*allfiles.size()));
			entrenamiento.add(f);
			allfiles.remove(f);
			aux++;
			
		}
		
		//En este momento en entrenamiento tenemos los correos para entrenar y en allfiles el resto de correos
		
	}
}
