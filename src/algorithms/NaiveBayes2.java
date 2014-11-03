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

public class NaiveBayes2 {
	
	private static final String SPAM = "spam";
	//private static final String HAM = "ham";

	private Integer nDocuments;
	private Integer totalWords;
	private Integer nSpamDocuments;
	private Integer nHamDocuments;
	private Map<String, Integer> spamWords;
	private Map<String, Integer> hamWords;
	private Map<String, List<Float>> probabilities;
	private Set<String> vocabulary;

	public NaiveBayes2() {
		this.probabilities = new HashMap<String, List<Float>>();
		this.spamWords = new HashMap<String, Integer>();
		this.hamWords = new HashMap<String, Integer>();
		this.vocabulary = new HashSet<String>();
		this.totalWords = 0;
		this.nDocuments = 0;
		this.nSpamDocuments = 0;
		this.nHamDocuments = 0;
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



	/**
	 * Método para cargar los correos de entrenamiento
	 * 
	 * @param path
	 *            -> Ruta padre en la que estén todos los archivos
	 */
	public void train(String path) {

		File file;
		List<File> filesToAnalize = new ArrayList<File>();

		try {
			file = new File(path);
			// Comprobamos que nos han pasado una ruta de una carpeta y no de un archivo
			if (file.isDirectory()) {
				iterateDirectories(file, filesToAnalize);
			} else {
				throw new InvalidPathException("La ruta es inválida");
			}
			nDocuments = filesToAnalize.size();
			
			for(File f : filesToAnalize){
				loadWords(f);
			}
			
			probabilities = generateProbabilities(spamWords, hamWords);
			vocabulary = probabilities.keySet();

		} catch (NullPointerException e) {
			throw new NullPointerException("Inserte la ruta");
		}
	}
	
	public void predict() {

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
			filesToAnalize.add(file);
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
	private void loadWords(File file) throws OpenFileException {
		try {
			// Pasamos a una cadena de texto el correo
			FileReader fr = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			fr.read(chars);
			String content = new String(chars);
			fr.close();
			String[] fileWords = content.split("([^a-zA-Z0-9])+");
			totalWords += fileWords.length;
			if (file.getParentFile().getName().equals(SPAM)){
				nSpamDocuments++;
				for(String s : fileWords){
					spamWords.put(s, spamWords.getOrDefault(s, 0) + 1);
				}
			} else {
				nHamDocuments++;
				for(String s : fileWords){
					hamWords.put(s, hamWords.getOrDefault(s, 0) + 1);
				}
			}
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
		Float prob = new Float(0.0);
		

		// Se recorren los mapas para calcular las probabilidades
		for (Entry<String, Integer> entry : spamWords.entrySet()) {
			List<Float> aux = new ArrayList<Float>();
			// Calculamos la probabilidad de que la palabra sea spam
			prob = entry.getValue().floatValue() / totalWords;
			aux.add(0, prob);
			// Añadimos una probabilidad 0 de que no sea spam (por si no aparece
			// en el otro mapa)
			aux.add(1, new Float(0.0));
			result.put(entry.getKey(), aux);
		}

		for (Entry<String, Integer> entry : hamWords.entrySet()) {
			List<Float> aux = new ArrayList<Float>();
			// Calculamos la probabilidad de que la palabra sea ham
			prob = entry.getValue().floatValue() / totalWords;
			// Apareció en el mapa de palabras spam
			if (result.containsKey(entry.getKey())) {
				aux = result.get(entry.getKey());
				aux.set(1, prob);
			} else { // Palabra que no apareció en los correos spam.
				// Probabilidad 0 de que sea spam
				aux.add(0, new Float(0.0));
				aux.add(1, prob);
			}
			result.put(entry.getKey(), aux);
		}
		
		return result;
		
	}

}
