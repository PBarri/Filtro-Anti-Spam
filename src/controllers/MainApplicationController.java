package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import model.Probability;
import utilities.Utils;
import algorithms.NaiveBayes;
import application.MainApplication;
/**
 * Clase controladora de la pantalla principal (Barra de menús)
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class MainApplicationController {

	// Referencia de la aplicación
	private MainApplication mainApplication;

	public MainApplicationController() {

	}

	public MainApplication getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApplication mainApp) {
		this.mainApplication = mainApp;
	}

	/**
	 * Método que se ejecuta al presionar el botón en el menú de salir.
	 */
	@FXML
	private void close() {
		// Cierra la aplicación
		Platform.exit();
	}

	/**
	 * Método que se ejecuta al botón de Acerca De...
	 */
	@FXML
	private void about() {
		String mensaje = "En esta aplicación se recrea el algoritmo de Naives Bayes ";
		mensaje += "y se aplica al filtrado de mensajes entre mensajes spam o no spam (ham)\n\n";
		mensaje += "Autores de la aplicación: \n";
		mensaje += "	Pablo Barrientos Lobato\n";
		mensaje += "	Alberto Salas Cantalejo";
		Alert alert = Utils.createAlert(AlertType.INFORMATION, "Acerca de...", mensaje, "Filtro Anti-Spam", mainApplication);
		alert.showAndWait();
	}

	/**
	 * Método que se ejecuta al presionar el botón de Nuevo Entrenamiento
	 */
	@FXML
	private void newTraining() {
		// Mostramos una alerta al usuario pidiéndole una confirmación
		Alert alert = Utils.createAlert(AlertType.CONFIRMATION, "Confirmar acción", "¿Desea crear un nuevo entrenamiento?\nPerderá los datos no guardados", null, mainApplication);
		Optional<ButtonType> response = alert.showAndWait();
		if(response.get().equals(ButtonType.OK)){
			this.mainApplication.getPrimaryStage().setTitle("Filtro Anti Spam");
			this.mainApplication.showHome(true);
		}
	}

	/**
	 * Método que se ejecuta al presionar el botón de Guardar
	 */
	@FXML
	private void save() {
		if (mainApplication.getFilePath() == null) {
			saveAs();
		} else {
			try {
				saveFile(mainApplication.getFilePath());
			} catch (NullPointerException e) {
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "No es un archivo .xml", null, mainApplication);
				alert.showAndWait();
				return;
			} catch (JAXBException e) {
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error al guardar el entrenamiento", null, mainApplication);
				alert.showAndWait();
				return;
			}
		}
	}

	/**
	 * Método que se ejecuta al presionar el botón de Guardar como
	 */
	@FXML
	public void saveAs() {
		Preferences pref = Preferences.userNodeForPackage(MainApplication.class);
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File(pref.get("saveFile", System.getProperty("user.home"))));
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files (*xml)", "*.xml");
		chooser.getExtensionFilters().add(filter);
		File file = chooser.showSaveDialog(this.mainApplication.getPrimaryStage());
		try {
			saveFile(file);
		} catch (NullPointerException e) {
			Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "No es un archivo .xml", null, mainApplication);
			alert.showAndWait();
			return;
		} catch (JAXBException e) {
			Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error al guardar el entrenamiento", null, mainApplication);
			alert.showAndWait();
			return;
		}
	}

	/**
	 * Método que se ejecuta al presionar el botón de Cargar 
	 */
	@FXML
	private void load() {
		Preferences pref = Preferences.userNodeForPackage(MainApplication.class);
		NaiveBayes alg = new NaiveBayes();
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files (*xml)", "*.xml");
		chooser.getExtensionFilters().add(filter);
		chooser.setInitialDirectory(new File(pref.get("loadFile", System.getProperty("user.home"))));

		File file = chooser.showOpenDialog(mainApplication.getPrimaryStage());

		if (file != null) {
			pref.put("loadFile", file.getParentFile().getAbsolutePath());
			try {
				JAXBContext context = JAXBContext.newInstance(NaiveBayes.class);
				Unmarshaller um = context.createUnmarshaller();

				alg = (NaiveBayes) um.unmarshal(file);

				// Rellenamos el map de probabilities y los StringProperties de Probability
				List<Probability> probs = new ArrayList<Probability>();
				probs.addAll(alg.getProbabilitiesList());
				alg.getProbabilities().clear();
				alg.getProbabilitiesList().clear();
				for (Probability prob : probs) {
					Probability p = new Probability(prob.getWordValue(), prob.getSpamProbabilityValue(), prob.getHamProbabilityValue());
					List<Float> aux = new ArrayList<Float>();
					aux.add(p.getSpamProbabilityValue());
					aux.add(p.getHamProbabilityValue());
					alg.getProbabilitiesList().add(p);
					alg.getProbabilities().put(p.getWordValue(), aux);
				}
				this.mainApplication.setFilePath(file);
				this.mainApplication.setAlg(alg);
				this.mainApplication.showNaiveBayesData();
				this.mainApplication.getPrimaryStage().setTitle("Filtro Anti Spam - " + file.getName());
			} catch (JAXBException e) {
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error al cargar el entrenamiento", null, mainApplication);
				alert.showAndWait();
				return;
			}
		}
	}
	
	/**
	 *  Método que se ejecuta al pulsar el botón de Ver Actual
	 */
	@FXML
	public void seeTraining(){
		if(mainApplication.getAlg().getProbabilities().isEmpty()){
			Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "No existe ningún conjunto de entrenamiento", null, mainApplication);
			alert.showAndWait();
		}else{
			mainApplication.showNaiveBayesData();
		}
	}

	/**
	 * Método que se encarga de guardar un documento en el disco
	 * @param file
	 * @throws JAXBException
	 * @throws NullPointerException
	 */
	protected void saveFile(File file) throws JAXBException, NullPointerException {
		if (file != null) {
			if (file.getPath().endsWith(".xml")) {
				JAXBContext context = JAXBContext.newInstance(NaiveBayes.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				m.marshal(this.mainApplication.getAlg(), file);
				this.mainApplication.getPrimaryStage().setTitle("Filtro Anti Spam - " + file.getName());
				this.mainApplication.setFilePath(file);
			} else {
				throw new NullPointerException();
			}
		}
	}
}