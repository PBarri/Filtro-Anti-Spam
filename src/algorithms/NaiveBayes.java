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
	//Contador de palabras sin repeticion
	private Integer totalWordsSR;
	private Integer nSpamDocuments;
	private Integer nHamDocuments;
	private Float initSpamProb;
	private Float initHamProb;
	private Map<String, Integer> spamWords;
	private Map<String, Integer> hamWords;
	private Map<String, List<Float>> probabilities;
	private Map<String,Integer> prior;
	private Set<String> vocabulary;
	
	
	// Para predicción
	private String predictPath;
	private List<Float> predictResults;

	public NaiveBayes() {
		this.probabilities = new HashMap<String, List<Float>>();
		this.spamWords = new HashMap<String, Integer>();
		this.hamWords = new HashMap<String, Integer>();
		this.prior = new HashMap<String,Integer>();
		this.vocabulary = new HashSet<String>();
		this.totalWords = 0;
		this.totalWordsSR=0;
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
	
	public Integer getTotalWordsSR()
	{
		return totalWordsSR;
	}
	
	public void setTotalWordsSR(Integer totalWordsSR)
	{
		
		this.totalWordsSR=totalWordsSR;
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
		
		//Estas 4 lineas siguientes corresponde al array prior que debe devolver el entrenamiento, no se si al unir los archivos lo borraste
		Integer auxSpam = nSpamDocuments / nDocuments;
		Integer auxham = nHamDocuments / nDocuments;
		prior.put("spam", auxSpam);
		prior.put("ham", auxham);
	}
	
	public void train() throws NullPointerException, InvalidPathException, OpenFileException {

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
					spamWords.put(s, spamWords.getOrDefault(s, 0) + 1);
					
					if(!spamWords.containsKey(s))
					{
						totalWordsSR++;
						
					}
				}
			} else if (f.getParentFile().getName().equals(HAM)) {
				nHamDocuments++;
				for(String s : fileWords){
					hamWords.put(s, hamWords.getOrDefault(s, 0) + 1);
					
					if(!hamWords.containsKey(s))
					{
						totalWordsSR++;
					}
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
			/*String str = FileUtils.readFileToString(file);*/ //-->> Idea : Vendria a sustituir las 4 lineas siguiente.
			FileReader fr = new FileReader(file); 
			char[] chars = new char[(int) file.length()];
			fr.read(chars);
			String content = new String(chars);
			fr.close();
			content.toLowerCase();
			String[] fileWords = content.split("([^a-zA-Z0-9])+");   //--> Esta bien asi pero , si justo antes se pasa a minusculas, para que poner en la expresion las mayusculas?
			
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
		Float prob = new Float(0.0);
		
		//Posible fallo:  en el algoritmo del PDF , en el punto 2.4 , especifica La division de T_-tc ("ocurrencias de t en texto_c " en teoria esta bien , aunque te piden los textos concatenados por
		//categoria , tenemos diferenciados dos mapas asiq es como si ya estuvieran contadod.) pero a ese T_-tc se le suma "1" . Esto si habria que añadirlo no ? , que no lo veo
		// Este cambio lo he hecho ya . Pero lo revisamos, te dejo señalado con AQUI donde he cambiado
		
		
		
		// Se recorren los mapas para calcular las probabilidades
		for (Entry<String, Integer> entry : spamWords.entrySet()) {
			List<Float> aux = new ArrayList<Float>();
			// Calculamos la probabilidad de que la palabra sea spam
			prob = (entry.getValue().floatValue() +1)/ (totalWords + totalWordsSR); //AQUI , el denominador tambien he cambiado , en el algoritmo y los otros lo tienen asi.(preguntame y te cuento)
			aux.add(0, prob);
			// Añadimos una probabilidad 0 de que no sea spam (por si no aparece
			// en el otro mapa)
			aux.add(1, new Float(0.0));
			result.put(entry.getKey(), aux);
		}

		for (Entry<String, Integer> entry : hamWords.entrySet()) {
			List<Float> aux = new ArrayList<Float>();
			// Calculamos la probabilidad de que la palabra sea ham
			prob = entry.getValue().floatValue() / (totalWords + totalWordsSR);//AQUI , el denominador tambien he cambiado , en el algoritmo y los otros lo tienen asi.
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
		
		
		//Esto siguiente no deberia aparecer, pues lo que es el calculo del log , se hace ya en "clasifica" que es el clasificar un nuevo correo con el entrenamiento ya echo.
		this.initSpamProb = new Float(Math.log10(nSpamDocuments.doubleValue() / nDocuments));
		this.initHamProb = new Float(Math.log10(nHamDocuments.doubleValue() / nDocuments));
		
		return result;
		
	}
	
	public String clasificaCorreo(File file) throws OpenFileException 
	{
				Set<String> listaPalabras   = new HashSet<String>();
				
			try{
					
					
					FileReader fr = new FileReader(file); 
					char[] chars = new char[(int) file.length()];
					fr.read(chars);
					String content = new String(chars);
					fr.close();
					content.toLowerCase();
					String[] fileWords = content.split("([^a-zA-Z0-9])+");  
					
					for(String s : fileWords){
						
						if(vocabulary.contains(s))
						{
							listaPalabras.add(s);
							this.initSpamProb += new Float (Math.log10(probabilities.get(s).get(0).doubleValue()));
							this.initHamProb +=  new Float (Math.log10(probabilities.get(s).get(1).doubleValue()));
						}
					}
					
					
					if(this.initSpamProb>this.initHamProb)
						return "spam";
					if(this.initHamProb>this.initSpamProb)
						return "ham";
					else
						return "iguales";
		
		} catch (IOException e) {
			throw new OpenFileException(file);
		}
		
	}

}
