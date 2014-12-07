package tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.Prediction;
import model.Probability;
import utilities.Utils;
import algorithms.NaiveBayes;
import application.MainApplication;
/**
 * Clase que ejecuta un entrenamiento y una predicción como Task.
 * El conjunto de entrenamiento se coge como el porcentaje de correos que el usuario introdujera.
 * El conjunto de predicción son los correos restantes.
 * Al ejecutarse en segundo plano impide que la aplicación se quede bloqueada.
 * 
 * @see NaiveBayes#train(String, Integer)
 * @see NaiveBayes#predict(String)
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class TrainPredictTask extends Task<Void> {

	private static final String SPAM = "spam";
	private static final String HAM = "ham";

	private final MainApplication mainApplication;
	private final String path;
	private final Integer percentage;
	private final Integer threshold;

	public TrainPredictTask(MainApplication mainApplication, String path, Integer percentage, Integer threshold) {
		this.mainApplication = mainApplication;
		this.path = path;
		this.percentage = percentage;
		this.threshold = threshold;
	}

	@Override
	protected Void call() {
		try {
			updateTitle("Entrenando");
			File file;
			List<File> filesToAnalize = new ArrayList<File>();
			List<File> filesToPredict = new ArrayList<File>();

			file = new File(path);

			if (file.isDirectory()) {
				Utils.iterateDirectories(file, filesToAnalize);
			} else {
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Ruta inválida", null, mainApplication);
				alert.showAndWait();
				this.cancel();
			}

			Utils.getFilesByPercentage(percentage, filesToAnalize, filesToPredict);

			// Cogemos las variables del algoritmo que necesitaremos
			NaiveBayes alg = mainApplication.getAlg();
			alg.setnDocuments(filesToAnalize.size());
			alg.setnPredictDocuments(filesToPredict.size());
			Integer totalWords = alg.getTotalWords();
			Set<String> vocabulary = alg.getVocabulary();
			Map<String, Integer> spamWords = alg.getSpamWords();
			Map<String, Integer> hamWords = alg.getHamWords();
			Integer nDocuments = alg.getnDocuments();
			Integer nSpamDocuments = alg.getnSpamDocuments();
			Integer nHamDocuments = alg.getnHamDocuments();
			Integer noRepeatingWords = 0;
			// Predicción
			Map<String, List<Float>> probabilities = alg.getProbabilities();
			Map<String, List<Float>> predictResults = alg.getPredictResults();
			Integer nPredictDocuments = alg.getnPredictDocuments();
			Integer nPredictSpamDocuments = 0;
			Integer nPredictHamDocuments = 0;
			Integer wellAnalized = 0;
			Integer badAnalized = 0;
			Integer wellSpam = 0;
			Integer badSpam = 0;
			Integer wellHam = 0;
			Integer badHam = 0;

			// Ejecutamos el entrenamiento

			updateMessage("Analizando las palabras contenidas en los correos... 0%");

			for (Integer i = 0; i < nDocuments; i++) {
				if (isCancelled()) {
					updateMessage("Cancelado");
					break;
				}
				File f = filesToAnalize.get(i);
				String[] fileWords = Utils.loadWords(f);
				totalWords += fileWords.length;
				if (f.getParentFile().getName().equals(SPAM)) {
					// Contamos el número de correos que son spam
					nSpamDocuments++;
					for (String s : fileWords) {
						// Rellenamos también el set de palabras "vocabulario"
						if (!vocabulary.contains(s)) {
							vocabulary.add(s);
						}
						if(spamWords.getOrDefault(s, 0) <= threshold){
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
						if(hamWords.getOrDefault(s, 0) <= threshold){
							hamWords.put(s, hamWords.getOrDefault(s, 0) + 1);
						}
					}
				}
				Integer perc = (int) Math.round((i.doubleValue() / nDocuments) * 100);
				updateProgress(i.doubleValue(), nDocuments.doubleValue());
				updateMessage("Analizando las palabras contenidas en los correos... "+ perc + "%");
			}
			
			spamWords = Utils.filterMap(spamWords);
			hamWords = Utils.filterMap(hamWords);

			updateMessage("Generando probabilidades para cada palabra... 0%");

			Integer cont = 0;
			noRepeatingWords = vocabulary.size();
			Float prob = new Float(1.0);

			// For que recorre las palabras de spam
			for (Entry<String, Integer> entry : spamWords.entrySet()) {
				if (isCancelled()) {
					updateMessage("Cancelado");
					break;
				}
				List<Float> aux = new ArrayList<Float>();
				// Calculamos la probabilidad de que la palabra sea spam
				prob = (entry.getValue().floatValue() + 1) / (totalWords + vocabulary.size());
				aux.add(0, prob);
				// Añadimos una probabilidad 1 de que no sea spam (por si no aparece en el otro mapa. 1 porque el logaritmo de 1 es 0)
				aux.add(1, new Float(1.0));
				probabilities.put(entry.getKey(), aux);

				Integer perc = (int) Math.round((cont.doubleValue() / noRepeatingWords) * 100);
				updateProgress(cont.doubleValue(), noRepeatingWords);
				updateMessage("Generando probabilidades para cada palabra... "+ perc + "%");
				cont++;
			}

			// For que recorre las palabras ham
			for (Entry<String, Integer> entry : hamWords.entrySet()) {
				if (isCancelled()) {
					updateMessage("Cancelado");
					break;
				}
				List<Float> aux = new ArrayList<Float>();
				// Calculamos la probabilidad de que la palabra sea ham
				prob = (entry.getValue().floatValue() + 1) / (totalWords + vocabulary.size());
				// Apareció en el mapa de palabras spam
				if (probabilities.containsKey(entry.getKey())) {
					aux = probabilities.get(entry.getKey());
					aux.set(1, prob);
				} else { // Palabra que no apareció en los correos spam.
					// Probabilidad 1 de que sea spam
					aux.add(0, new Float(1.0));
					aux.add(1, prob);
				}
				probabilities.put(entry.getKey(), aux);

				Integer perc = (int) Math.round((cont.doubleValue() / noRepeatingWords) * 100);
				updateProgress(cont.doubleValue(), noRepeatingWords);
				updateMessage("Generando probabilidades para cada palabra... "+ perc + "%");
				cont++;
			}

			List<Probability> probabilitiesList = new ArrayList<Probability>();
			Integer probSize = probabilities.keySet().size();
			cont = 0;
			updateMessage("Analizando datos... %");
			// Rellenamos la lista de Probability para mostrar en la interfaz
			for (Entry<String, List<Float>> entry : probabilities.entrySet()) {
				String word = entry.getKey();
				Float spamP = entry.getValue().get(0);
				Float hamP = entry.getValue().get(1);
				Probability p = new Probability(word, spamP, hamP);
				probabilitiesList.add(p);

				Integer perc = (int) Math.round((cont.doubleValue() / probSize) * 100);
				updateMessage("Analizando datos... " + perc + "%");
				updateProgress(cont.doubleValue(), probSize.doubleValue());
				cont++;
			}

			alg.setPath(path);
			alg.setnSpamDocuments(nSpamDocuments);
			alg.setnHamDocuments(nHamDocuments);
			alg.setTotalWords(totalWords);
			alg.setProbabilities(probabilities);
			alg.setVocabulary(vocabulary);
			alg.setProbabilitiesList(probabilitiesList);
			alg.setInitHamProb(Math.abs(new Float(Math.log10(nHamDocuments.doubleValue() / nDocuments))));
			alg.setInitSpamProb(Math.abs(new Float(Math.log10(nSpamDocuments.doubleValue() / nDocuments))));

			// Ejecutamos la predicción

			predictResults.clear();
			cont = 0;
			updateMessage("Realizando la predicción... 0%");
			for (File f : filesToPredict) {
				// Inicializamos las probabilidades a las iniciales del algoritmo
				Float totalSpamProb = alg.getInitSpamProb();
				Float totalHamProb = alg.getInitHamProb();
				// Inicializamos una lista que contendrá las probabilidades finales
				List<Float> results = new ArrayList<Float>();

				// Cargamos las palabras contenidas en el correo
				String[] fileWords = Utils.loadWords(f);
				for (String s : fileWords) {
					if (probabilities.containsKey(s)) {
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
				// Aumentamos el número de archivos que el algoritmo ha predecido como spam o ham, respectivamente
				if (f.getParentFile().getName().equals(SPAM)) {
					nPredictSpamDocuments++;
					results.add(new Float(1));
				} else if (f.getParentFile().getName().equals(HAM)) {
					nPredictHamDocuments++;
					results.add(new Float(2));
				}

				// Comprobamos si el algoritmo de predicción ha dado resultado o no
				if (results.get(2).equals(results.get(3))) {
					wellAnalized++;
					if(f.getParentFile().getName().equals(SPAM))
						wellSpam++;
					else
						wellHam++;
				} else {
					badAnalized++;
					if(f.getParentFile().getName().equals(SPAM))
						badSpam++;
					else
						badHam++;
				}

				// Introducimos los resultados en el mapa que se mostrará luego en la interfaz
				predictResults.put(f.getName(), results);

				Integer perc = (int) Math.round((cont.doubleValue() / nPredictDocuments) * 100);
				updateMessage("Realizando la predicción... " + perc + "%");
				updateProgress(cont.doubleValue(), nPredictDocuments.doubleValue());
				cont++;
			}

			List<Prediction> predictionList = new ArrayList<Prediction>();

			cont = 0;
			updateMessage("Analizando datos... 0%");
			for (Entry<String, List<Float>> entry : predictResults.entrySet()) {
				Prediction p = new Prediction();
				List<String> aux = Utils.convertPredictions(entry.getValue());
				p.setFilename(entry.getKey());
				p.setSpamProbability(aux.get(0));
				p.setHamProbability(aux.get(1));
				p.setCategory(aux.get(2));
				p.setRealCategory(aux.get(3));
				predictionList.add(p);

				Integer perc = (int) Math.round((cont.doubleValue() / nPredictDocuments) * 100);
				updateMessage("Analizando datos... " + perc + "%");
				updateProgress(cont.doubleValue(), nPredictDocuments.doubleValue());
				cont++;
			}

			alg.setPredictResults(predictResults);
			alg.setnPredictSpamDocuments(nPredictSpamDocuments);
			alg.setnPredictHamDocuments(nPredictHamDocuments);
			alg.setWellAnalized(wellAnalized);
			alg.setBadAnalized(badAnalized);
			alg.setWellSpam(wellSpam);
			alg.setWellHam(wellHam);
			alg.setBadSpam(badSpam);
			alg.setBadHam(badHam);
			alg.setPredictionList(predictionList);
		} catch (Exception e) {
			fireEvent(new WorkerStateEvent(this, WorkerStateEvent.WORKER_STATE_FAILED));
		}
		return null;
	}

	public MainApplication getMainApplication() {
		return mainApplication;
	}

	public String getPath() {
		return path;
	}

	public Integer getPercentage() {
		return percentage;
	}

	public Integer getThreshold() {
		return threshold;
	}

}
