package algorithms;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import exceptions.InvalidPathException;
import exceptions.OpenFileException;

public class NaiveBayes {
	
	private static final String SPAM = "spam";
	private static final String HAM = "ham";

	// Para entrenamiento
	private String path;
	private Integer nDocuments;
	private Integer totalWords;
	private Integer nSpamDocuments;
	private Integer nHamDocuments;
	private Float initSpamProb;
	private Float initHamProb;
	private Map<String, Integer> spamWords;
	private Map<String, Integer> hamWords;
	private Map<String, List<Float>> probabilities;
	private Set<String> vocabulary;
	
	// Para predicci�n
	private String predictPath;
	private List<Float> predictResults;

	public NaiveBayes() {
		this.probabilities = new HashMap<String, List<Float>>();
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
		this.predictPath = "";
		this.predictResults = new ArrayList<Float>();
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

	public Set<String> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(Set<String> vocabulary) {
		this.vocabulary = vocabulary;
	}



	public String getPredictPath() {
		return predictPath;
	}

	public void setPredictPath(String predictPath) {
		this.predictPath = predictPath;
	}

	public List<Float> getPredictResults() {
		return predictResults;
	}

	public void setPredictResults(List<Float> predictResults) {
		this.predictResults = predictResults;
	}

	/**
	 * M�todo para cargar los correos de entrenamiento
	 * 
	 * @param path
	 *            -> Ruta padre en la que est�n todos los archivos
	 * @throws OpenFileException 
	 * @throws InvalidPathException 
	 * @throws NullPointerException 
	 */
	public void train(String path) throws NullPointerException, InvalidPathException, OpenFileException{
		this.path = path;
		this.train();
	}
	
	public void train() throws NullPointerException, InvalidPathException, OpenFileException {

		File file = new File(path);
		List<File> filesToAnalize = new ArrayList<File>();

		// Comprobamos que nos han pasado una ruta de una carpeta y no de un archivo
		if (file.isDirectory()) {
			iterateDirectories(file, filesToAnalize);
		} else {
			throw new InvalidPathException("La ruta es inv�lida");
		}
		nDocuments = filesToAnalize.size();
		
		for(File f : filesToAnalize){
			String[] fileWords = loadWords(f);
			totalWords += fileWords.length;
			if (f.getParentFile().getName().equals(SPAM)){
				nSpamDocuments++;
				for(String s : fileWords){
					spamWords.put(s, spamWords.getOrDefault(s, 0) + 1);
				}
			} else if (f.getParentFile().getName().equals(HAM)) {
				nHamDocuments++;
				for(String s : fileWords){
					hamWords.put(s, hamWords.getOrDefault(s, 0) + 1);
				}
			}
		}
		
		probabilities = generateProbabilities(spamWords, hamWords);
		vocabulary = probabilities.keySet();

	}
	
	public void predict(String path) throws OpenFileException{
		this.predictPath = path;
		this.predict();
	}
	
	public void predict() throws OpenFileException {
		
		File file = new File(predictPath);
		Float totalSpamProb = this.initSpamProb;
		Float totalHamProb = this.initHamProb;
		Map<String, Integer> trainedWords = new HashMap<String, Integer>();
		Map<String, Integer> untrainedWords = new HashMap<String, Integer>();
		
		// Comprobamos que es un fichero
		if(file.isFile()){
			String[] fileWords = loadWords(file);
			for(String s : fileWords){
				
			}
		}

	}

	/**
	 * M�todo que recorre los directorios, insertando en filesToAnalize todos
	 * los archivos deseados
	 * 
	 * @param file -> Directorio/fichero a analizar
	 * @param filesToAnalize -> Lista de archivos que se utilizar�n para cargar las palabras
	 */
	private void iterateDirectories(File file, List<File> filesToAnalize) {
		// Si el archivo es un directorio, se van recorriendo todos sus hijos
		// llamando recursivamente a este m�todo
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				iterateDirectories(f, filesToAnalize);
			}
		} else { // Si es un archivo se manda a una funci�n para que sea analizado
			filesToAnalize.add(file);
		}
	}

	/**
	 * M�todo que se utiliza para cargar en el mapa todas las palabras
	 * relevantes de un fichero
	 * 
	 * @param f
	 *            -> Archivo que se est� analizando
	 * @param result
	 *            -> Mapa en el que se cargar�n las palabras
	 * @throws OpenFileException
	 *             -> Error al abrir el archivo
	 */
	private String[] loadWords(File file) throws OpenFileException {
		try {
			// Pasamos a una cadena de texto el correo
			FileReader fr = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			fr.read(chars);
			String content = new String(chars);
			fr.close();
			content.toLowerCase();
			String[] fileWords = content.split("([^a-zA-Z0-9])+");
			
			return fileWords;
			
		} catch (IOException e) {
			throw new OpenFileException(file);
		}

	}

	/**
	 * M�todo consistente en generar las probabilidades de que una palabra sea o
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
		Float prob = new Float(0.0);
		

		// Se recorren los mapas para calcular las probabilidades
		for (Entry<String, Integer> entry : spamWords.entrySet()) {
			List<Float> aux = new ArrayList<Float>();
			// Calculamos la probabilidad de que la palabra sea spam
			prob = entry.getValue().floatValue() / totalWords;
			aux.add(0, prob);
			// A�adimos una probabilidad 0 de que no sea spam (por si no aparece
			// en el otro mapa)
			aux.add(1, new Float(0.0));
			result.put(entry.getKey(), aux);
		}

		for (Entry<String, Integer> entry : hamWords.entrySet()) {
			List<Float> aux = new ArrayList<Float>();
			// Calculamos la probabilidad de que la palabra sea ham
			prob = entry.getValue().floatValue() / totalWords;
			// Apareci� en el mapa de palabras spam
			if (result.containsKey(entry.getKey())) {
				aux = result.get(entry.getKey());
				aux.set(1, prob);
			} else { // Palabra que no apareci� en los correos spam.
				// Probabilidad 0 de que sea spam
				aux.add(0, new Float(0.0));
				aux.add(1, prob);
			}
			result.put(entry.getKey(), aux);
		}
		
		this.initSpamProb = new Float(Math.log10(nSpamDocuments.doubleValue() / nDocuments));
		this.initHamProb = new Float(Math.log10(nHamDocuments.doubleValue() / nDocuments));
		
		return result;
		
	}

}
