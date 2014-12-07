package algorithms;

import java.io.File;
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

import model.Prediction;
import model.Probability;
import utilities.Utils;
import exceptions.InvalidPathException;
import exceptions.NotTrainedException;
import exceptions.NotValidPercentageException;
import exceptions.OpenFileException;

/**
 * 
 * Clase principal del algoritmo. En ella se definen todos los atributos necesarios y los métodos para entrenar y predecir
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */

@XmlRootElement(name = "NaiveBayes")
@XmlAccessorType(XmlAccessType.FIELD)
public class NaiveBayes {

	// Declaración de atributos con anotaciones para guardar y cargar los datos
	
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
	@XmlElement(name = "threshold")
	private Integer threshold;
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
	@XmlTransient
	private Integer wellSpam;
	@XmlTransient
	private Integer badSpam;
	@XmlTransient
	private Integer wellHam;
	@XmlTransient
	private Integer badHam;
	@XmlTransient
	private List<Prediction> predictionList;

	/**
	 *  Constructor que inicializa todos los atributos de la clase
	 */
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
		this.threshold = 0;
		this.path = "";
		// List<Float> ==> [0] -> Probabilidad SPAM, [1] -> Probabilidad HAM,
		// [2] -> Clasificación algoritmo {1 -> Spam, 2 -> Ham, 0 -> Error
		// clasificación},
		// [3] -> Clasificación real {1 -> Spam, 2 -> Ham}
		this.predictResults = new HashMap<String, List<Float>>();
		this.predictionList = new ArrayList<Prediction>();
		this.nPredictDocuments = 0;
		this.nPredictSpamDocuments = 0;
		this.nPredictHamDocuments = 0;
		this.wellAnalized = 0;
		this.badAnalized = 0;
		this.wellSpam = 0;
		this.wellHam = 0;
		this.badHam = 0;
		this.badSpam = 0;
	}

	// Getters y setters
	
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

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
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

	public List<Prediction> getPredictionList() {
		return predictionList;
	}

	public void setPredictionList(List<Prediction> predictionList) {
		this.predictionList = predictionList;
	}

	public Integer getWellSpam() {
		return wellSpam;
	}

	public void setWellSpam(Integer wellSpam) {
		this.wellSpam = wellSpam;
	}

	public Integer getBadSpam() {
		return badSpam;
	}

	public void setBadSpam(Integer badSpam) {
		this.badSpam = badSpam;
	}

	public Integer getWellHam() {
		return wellHam;
	}

	public void setWellHam(Integer wellHam) {
		this.wellHam = wellHam;
	}

	public Integer getBadHam() {
		return badHam;
	}

	public void setBadHam(Integer badHam) {
		this.badHam = badHam;
	}
	
	// Fin de getters y setters

	/**
	 * Método para entrenar un correo a partir de una ruta y de un porcentaje
	 * 
	 * @param path Ruta que contiene todos los archivos a entrenar
	 * @param percentage Porcentage de correos que se utilizarán para el entrenamiento. 
	 * Si es menor que 100, el resto se usará para predecir
	 * @throws NullPointerException Error al abrir uno de los archivos
	 * @throws InvalidPathException En el caso de que la ruta sea inválida
	 * @throws OpenFileException Error al abrir un archivo
	 * @throws InvalidPathException El usuario ha escogido un porcentaje inválido
	 */
	public void train(String path, Integer percentage) throws NullPointerException,	InvalidPathException, OpenFileException, NotValidPercentageException {
		
		// Comprobamos que el porcentaje se encuentra entre unos valores aceptables (mayor que el 20%)
		if(percentage < 20)
			throw new NotValidPercentageException();
		
		// Se guarda en una variable del algortimo para mostrarla en la interfaz
		this.path = path;
		List<File> filesToAnalize = new ArrayList<File>();

		File file = new File(path);
		// Comprobamos que nos han pasado una ruta de una carpeta y no de un archivo
		if (file.isDirectory()) {
			// Iteramos sobre sus subcarpetas y archivos para sacar todos los  archivos a analizar
			Utils.iterateDirectories(file, filesToAnalize);
		} else {
			throw new InvalidPathException("La ruta es inválida");
		}
		
		// Si el porcentaje es menor que 100, creamos dos archivos, y hacemos el entrenamiento y la predicción a la vez
		if(percentage < 100){
			List<File> filesToPredict = new ArrayList<File>();
			// Rellena filesToAnalize y filesToPredict con los datos para entrenamiento y predicción
			Utils.getFilesByPercentage(percentage, filesToAnalize, filesToPredict);
			this.nDocuments = filesToAnalize.size();
			this.nPredictDocuments = filesToPredict.size();
			// Ejecuta el entrenamiento
			this.train(filesToAnalize);
			try {
				// Ejecuta la predicción
				this.predict(filesToPredict);
			} catch (NotTrainedException e) {
			}
			return;
		}

		// Porcentaje igual a 100, todos los correos para entrenamiento
		
		nDocuments = filesToAnalize.size();

		// Ejecuta el entrenamiento
		this.train(filesToAnalize);
	}
	
	/**
	 * Método que ejecuta el entrenamiento de un conjunto de archivos
	 * 
	 * @param filesToAnalize Lista de archivos para entrenar
	 * @throws OpenFileException Error al abrir uno de los archivos de la lista
	 */
	private void train(List<File> filesToAnalize) throws OpenFileException {

		// Para cada archivo, rellena los mapas spamWords y hamWords con las
		// palabras que aparecen en ellos y el número de repeticiones
		for (File f : filesToAnalize) {
			// Carga las palabras que aparecen en el archivo
			String[] fileWords = Utils.loadWords(f);
			// También vamos contando el número total de palabras
			totalWords += fileWords.length;
			if (f.getParentFile().getName().equals(SPAM)) {
				// Contamos el número de correos que son spam
				nSpamDocuments++;
				for (String s : fileWords) {
					// Rellenamos también el set de palabras "vocabulario"
					if (!vocabulary.contains(s)) {
						vocabulary.add(s);
					}
					// Comprobamos que la palabra está por debajo del umbral
					if(spamWords.getOrDefault(s, 0) <= threshold){
						// Aumentamos el número de repeticiones de cada palabra
						spamWords.put(s, spamWords.getOrDefault(s, 0) + 1);
					}
				}
			} else if (f.getParentFile().getName().equals(HAM)) {
				// Contamos el número de correos que son ham
				nHamDocuments++;
				for (String s : fileWords) {
					if (!vocabulary.contains(s)) {
						vocabulary.add(s);
					}
					// Comprobamos que la palabra está por debajo del umbral
					if(hamWords.getOrDefault(s, 0) <= threshold){
						// Aumentamos el número de repeticiones de cada palabra
						hamWords.put(s, hamWords.getOrDefault(s, 0) + 1);
					}
				}
			}
		}
		
		// Eliminamos las palabras que solo hayan aparecido 1 vez
		spamWords = Utils.filterMap(spamWords);
		hamWords = Utils.filterMap(hamWords);

		// Llamamos al método que genera el mapa de probabilidades para cada
		// palabra de ser spam o ham
		probabilities = generateProbabilities(spamWords, hamWords);

	}

	/**
	 * Método que se usa para predecir
	 * 
	 * @param path Ruta del directorio a analizar
	 * @throws OpenFileException
	 * @throws NotTrainedException
	 * @throws NullPointerException
	 */
	public void predict(String path) throws OpenFileException, NotTrainedException, NullPointerException {
		File file = new File(path);
		List<File> filesToAnalize = new ArrayList<File>();

		// Rellenamos los archivos a analizar
		if (file.isDirectory()) {
			Utils.iterateDirectories(file, filesToAnalize);
		} else {
			filesToAnalize.add(file);
		}
		this.nPredictDocuments = filesToAnalize.size();

		// Llamamos al método que realmente analiza los archivos
		this.predict(filesToAnalize);
	}

	/**
	 * Método que analiza y predice una lista de archivos concreta
	 * @param filesToAnalize Lista de archivos a predecir
	 * @throws OpenFileException Error al abrir un archivo
	 * @throws NotTrainedException Error en el caso de que no se haya entrenado ningún conjunto previamente
	 */
	private void predict(List<File> filesToAnalize) throws OpenFileException, NotTrainedException {

		// Comprobamos que el Mapa de probabilidades contiene datos
		if (probabilities.isEmpty())
			throw new NotTrainedException();
		
		// Vaciamos los resultados previos
		clearPrediction();
		
		// Para cada archivo, intentamos predecir si será un correo spam o ham
		for (File file : filesToAnalize) {

			// Inicializamos las probabilidades a las iniciales del algoritmo
			Float totalSpamProb = this.initSpamProb;
			Float totalHamProb = this.initHamProb;
			// Inicializamos una lista que contendrá las probabilidades finales
			List<Float> results = new ArrayList<Float>();

			// Cargamos las palabras contenidas en el correo
			String[] fileWords = Utils.loadWords(file);
			for (String s : fileWords) {
				// Por cada palabra, comprobamos si el mapa de entrenamiento la contiene
				if (probabilities.containsKey(s)) {
					// Aumentamos la puntuación del correo
					Float tempSpamProb = Math.abs(new Float(Math.log10(probabilities.get(s).get(0))));
					Float tempHamProb = Math.abs(new Float(Math.log10(probabilities.get(s).get(1))));
					totalSpamProb += tempSpamProb;
					totalHamProb += tempHamProb;
				}
			}
			results.add(totalSpamProb);
			results.add(totalHamProb);
			switch (totalSpamProb.compareTo(totalHamProb)) {
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
			// Aumentamos el número de archivos que el algoritmo ha predecido como spam o ham, respectivamente, 
			// e introducimos en la lista de resultados su clasificación real
			if (file.getParentFile().getName().equals(SPAM)) {
				this.nPredictSpamDocuments++;
				results.add(new Float(1));
			} else if (file.getParentFile().getName().equals(HAM)) {
				this.nPredictHamDocuments++;
				results.add(new Float(2));
			}
			
			// Comprobamos si el algoritmo de predicción ha clasificado bien
			if (results.get(2).equals(results.get(3))) {
				wellAnalized++;
				// Comprobamos si ha clasificado bien un correo SPAM o HAM
				if(file.getParentFile().getName().equals(SPAM))
					wellSpam++;
				else
					wellHam++;
			} else {
				badAnalized++;
				// Comprobamos si ha clasificado mal un correo SPAM o HAM
				if(file.getParentFile().getName().equals(SPAM))
					badSpam++;
				else
					badHam++;
			}
			
			// Introducimos los resultados en el mapa que se mostrará luego en la interfaz
			predictResults.put(file.getName(), results);
		}
		
		// Creamos la lista de Predicciones con los resultados de la predicción (Necesario para la interfaz)
		createPredictionList();
		
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
			// Calculamos la probabilidad de que la palabra sea spam (Aplicando el suavizado de Laplace)
			prob = (entry.getValue().floatValue() + 1) / (totalWords + vocabulary.size());
			aux.add(0, prob);
			// Añadimos una probabilidad 1 de que no sea spam (por si no aparece
			// en el otro mapa. 1 porque el logaritmo de 1 es 0)
			aux.add(1, new Float(1.0));
			result.put(entry.getKey(), aux);
		}

		for (Entry<String, Integer> entry : hamWords.entrySet()) {
			List<Float> aux = new ArrayList<Float>();
			// Calculamos la probabilidad de que la palabra sea ham (Aplicando el suavizado de Laplace)
			prob = (entry.getValue().floatValue() + 1) / (totalWords + vocabulary.size());
			// Apareció en el mapa de palabras spam
			if (result.containsKey(entry.getKey())) {
				aux = result.get(entry.getKey());
				aux.set(1, prob);
			} else { // Palabra que no apareció en los correos spam.
				// Probabilidad 1 de que sea spam
				aux.add(0, new Float(1.0));
				aux.add(1, prob);
			}
			result.put(entry.getKey(), aux);
		}
		
		// Rellenamos la lista de Probability para mostrar en la interfaz
		createProbabiltyList(result);

		// Calculamos las probabilidades iniciales para que no haga falta calcularlas en cada predicción
		this.initSpamProb = Math.abs(new Float(Math.log10(nSpamDocuments.doubleValue() / nDocuments)));
		this.initHamProb = Math.abs(new Float(Math.log10(nHamDocuments.doubleValue() / nDocuments)));

		return result;

	}
	
	/**
	 * Método que crea una lista de Probabilidades con los datos del entreamiento del algoritmo
	 * @param probabilities Mapa con los datos de las probabilidades
	 */
	private void createProbabiltyList(Map<String, List<Float>> probabilites) {
		for (Entry<String, List<Float>> entry : probabilities.entrySet()) {
			String word = entry.getKey();
			Float spamP = entry.getValue().get(0);
			Float hamP = entry.getValue().get(1);
			Probability p = new Probability(word, spamP, hamP);
			probabilitiesList.add(p);
		}		
	}

	/**
	 * Creamos una lista de Predicciones a partir de los resultados de la predicción
	 */
	private void createPredictionList() {
		for(Entry<String, List<Float>> entry : predictResults.entrySet()){
			Prediction p = new Prediction();
			List<String> aux = Utils.convertPredictions(entry.getValue());
			p.setFilename(entry.getKey());
			p.setSpamProbability(aux.get(0));
			p.setHamProbability(aux.get(1));
			p.setCategory(aux.get(2));
			p.setRealCategory(aux.get(3));
			predictionList.add(p);
		}		
	}

	/**
	 * Vacía los datos de la predicción actual
	 */
	private void clearPrediction() {
		predictResults.clear();
		wellAnalized = 0;
		badAnalized = 0;
		wellSpam = 0;
		badSpam = 0;
		wellHam = 0;
		badHam = 0;
		nPredictHamDocuments = 0;
		nPredictSpamDocuments = 0;		
	}
}
