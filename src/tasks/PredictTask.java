package tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import model.Prediction;
import utilities.Utils;
import algorithms.NaiveBayes;
import application.MainApplication;

public class PredictTask extends Task<Void> {

	private static final String SPAM = "spam";
	private static final String HAM = "ham";

	private final MainApplication mainApplication;
	private final String path;

	public PredictTask(MainApplication app, String path) {
		this.mainApplication = app;
		this.path = path;
	}

	@Override
	protected Void call() {
		try {
			updateTitle("Prediciendo");

			// Atributos que necesitaremos
			NaiveBayes alg = mainApplication.getAlg();
			File file = new File(path);
			List<File> filesToAnalize = new ArrayList<File>();

			// Rellenamos los archivos a analizar
			if (file.isDirectory()) {
				Utils.iterateDirectories(file, filesToAnalize);
			} else {
				filesToAnalize.add(file);
			}

			Integer nPredictDocuments = filesToAnalize.size();
			Integer wellAnalized = 0;
			Integer badAnalized = 0;
			Map<String, List<Float>> predictResults = new HashMap<String, List<Float>>();
			Map<String, List<Float>> probabilities = alg.getProbabilities();
			Integer nPredictSpamDocuments = 0;
			Integer nPredictHamDocuments = 0;
			List<Prediction> predictionList = new ArrayList<Prediction>();

			Integer count = 0;

			updateMessage("Prediciendo correos... 0%");
			for (File f : filesToAnalize) {
				if(isCancelled())
					break;
				// Inicializamos las probabilidades a las iniciales del
				// algoritmo
				Float totalSpamProb = alg.getInitSpamProb();
				Float totalHamProb = alg.getInitHamProb();
				// Inicializamos una lista que contendrá las probabilidades
				// finales
				List<Float> results = new ArrayList<Float>();

				// Cargamos las palabras contenidas en el correo
				String[] fileWords = Utils.loadWords(f);

				for (String s : fileWords) {
					if(isCancelled())
						break;
					if (probabilities.containsKey(s)) {
						Float tempSpamProb = Math.abs(new Float(Math
								.log10(probabilities.get(s).get(0))));
						Float tempHamProb = Math.abs(new Float(Math
								.log10(probabilities.get(s).get(1))));
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
				// Aumentamos el número de archivos que el algoritmo ha
				// predecido como spam o ham, respectivamente
				if (f.getParentFile().getName().equals(SPAM)) {
					nPredictSpamDocuments++;
					results.add(new Float(1));
				} else if (f.getParentFile().getName().equals(HAM)) {
					nPredictHamDocuments++;
					results.add(new Float(2));
				}

				// Comprobamos si el algoritmo de predicción ha dado resultado o
				// no
				if (results.get(2).equals(results.get(3))) {
					wellAnalized++;
				} else {
					badAnalized++;
				}

				// Introducimos los resultados en el mapa que se mostrará luego
				// en la interfaz
				predictResults.put(f.getName(), results);

				Integer perc = (int) Math
						.round((count.doubleValue() / nPredictDocuments) * 100);
				updateProgress(perc.doubleValue(),
						nPredictDocuments.doubleValue());
				updateMessage("Prediciendo correos... " + perc + "%");
				count++;
			}

			updateMessage("Analizando los resultados... 0%");
			count = 0;
			Integer resultsSize = predictResults.keySet().size();
			for (Entry<String, List<Float>> entry : predictResults.entrySet()) {
				if(isCancelled())
					break;
				Prediction p = new Prediction();
				List<String> aux = Utils.convertPredictions(entry.getValue());
				p.setFilename(entry.getKey());
				p.setSpamProbability(aux.get(0));
				p.setHamProbability(aux.get(1));
				p.setCategory(aux.get(2));
				p.setRealCategory(aux.get(3));
				predictionList.add(p);

				Integer perc = (int) Math
						.round((count.doubleValue() / resultsSize) * 100);
				updateProgress(count.doubleValue(), resultsSize.doubleValue());
				updateMessage("Analizando los resultados... " + perc + "%");
				count++;
			}

			// Guardamos los resultados en el algoritmo
			alg.setWellAnalized(wellAnalized);
			alg.setBadAnalized(badAnalized);
			alg.setPredictResults(predictResults);
			alg.setPredictionList(predictionList);
			alg.setnPredictDocuments(nPredictDocuments);
			alg.setnPredictSpamDocuments(nPredictSpamDocuments);
			alg.setnPredictHamDocuments(nPredictHamDocuments);

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
