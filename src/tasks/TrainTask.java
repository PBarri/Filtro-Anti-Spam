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
import model.Probability;
import utilities.Utils;
import algorithms.NaiveBayes;
import application.MainApplication;
/**
 * Clase que ejecuta un entrenamiento como Task.
 * Al ejecutarse en segundo plano impide que la aplicación se quede bloqueada.
 * 
 * @see NaiveBayes#train(String, Integer)
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class TrainTask extends Task<Void> {

	private static final String SPAM = "spam";
	private static final String HAM = "ham";

	private final MainApplication mainApplication;
	private final String path;
	private final Integer threshold;

	public TrainTask(MainApplication app, String path, Integer threshold) {
		this.mainApplication = app;
		this.path = path;
		this.threshold = threshold;
	}

	@Override
	protected Void call() {
		try {

			updateTitle("Entrenando");
			File file;
			List<File> filesToAnalize = new ArrayList<File>();

			file = new File(path);

			if (file.isDirectory()) {
				Utils.iterateDirectories(file, filesToAnalize);
			} else {
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Ruta inválida", null, mainApplication);
				alert.showAndWait();
				this.cancel();
			}

			// Cogemos las variables del algoritmo que necesitaremos
			NaiveBayes alg = mainApplication.getAlg();
			alg.setnDocuments(filesToAnalize.size());
			Integer totalWords = alg.getTotalWords();
			Set<String> vocabulary = alg.getVocabulary();
			Map<String, Integer> spamWords = alg.getSpamWords();
			Map<String, Integer> hamWords = alg.getHamWords();
			Integer nDocuments = alg.getnDocuments();
			Integer nSpamDocuments = alg.getnSpamDocuments();
			Integer nHamDocuments = alg.getnHamDocuments();
			Map<String, List<Float>> probabilities = alg.getProbabilities();

			updateMessage("Analizando las palabras contenidas en los correos... 0%");

			for (Integer i = 0; i < nDocuments; i++) {
				if (isCancelled()) 
					break;
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
			Integer nHamWords = hamWords.keySet().size();
			Integer nSpamWords = spamWords.keySet().size();
			Integer noRepeatingWords = nHamWords + nSpamWords;
			Float prob = new Float(1.0);

			// For que recorre las palabras de spam
			for (Entry<String, Integer> entry : spamWords.entrySet()) {
				if (isCancelled())
					break;
				List<Float> aux = new ArrayList<Float>();
				// Calculamos la probabilidad de que la palabra sea spam
				prob = (entry.getValue().floatValue() + 1) / (totalWords + vocabulary.size());
				aux.add(0, prob);
				// Añadimos la probabilidad como si no estuviera contenido en ham
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
				if(isCancelled())
					break;
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

			// Metemos en alg todos los cambios hechos

			alg.setPath(path);
			alg.setnSpamDocuments(nSpamDocuments);
			alg.setnHamDocuments(nHamDocuments);
			alg.setTotalWords(totalWords);
			alg.setProbabilities(probabilities);
			alg.setVocabulary(vocabulary);
			alg.setProbabilitiesList(probabilitiesList);
			alg.setInitHamProb(Math.abs(new Float(Math.log10(nHamDocuments.doubleValue() / nDocuments))));
			alg.setInitSpamProb(Math.abs(new Float(Math.log10(nSpamDocuments.doubleValue() / nDocuments))));

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

}
