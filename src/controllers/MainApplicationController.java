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

public class MainApplicationController {

	private MainApplication mainApplication;

	public MainApplicationController() {

	}

	public MainApplication getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApplication mainApp) {
		this.mainApplication = mainApp;
	}

	@FXML
	private void close() {
		Platform.exit();
	}

	@FXML
	private void about() {
		String mensaje = "En esta aplicación se recrea el algoritmo de Naives Bayes ";
		mensaje += "y se aplica al filtrado de mensajes entre mensajes spam o no spam (ham)\n\n";
		mensaje += "Autores de la aplicación: \n";
		mensaje += "	Pablo Barrientos Lobato\n";
		mensaje += "	Alberto Salas Cantalejo";
		Alert alert = Utils.createAlert(AlertType.INFORMATION, "Acerca de...", mensaje, "Filtro Anti-Spam", mainApplication);
		//Dialogs.create().title("Acerca de...").masthead("Filtro Anti-Spam").message(mensaje).showInformation();
		alert.showAndWait();
	}

	@FXML
	private void newTraining() {
		Alert alert = Utils.createAlert(AlertType.CONFIRMATION, "Confirmar acción", "¿Desea crear un nuevo entrenamiento?\nPerderá los datos no guardados", null, mainApplication);
		Optional<ButtonType> response = alert.showAndWait();
		if(response.get().equals(ButtonType.OK)){
			this.mainApplication.getPrimaryStage().setTitle("Filtro Anti Spam");
			this.mainApplication.showHome(true);
		}
//		Action response = Dialogs.create().title("Confirmar acción").masthead(null).message("¿Desea crear un nuevo entrenamiento?\nPerderá todos los datos no guardados").showConfirm();
//		if (response.equals(Dialog.ACTION_YES)) {
//			this.mainApplication.getPrimaryStage().setTitle("Filtro Anti Spam");
//			this.mainApplication.showHome(true);
//		}
	}

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
				//Dialogs.create().title("Error").masthead(null).message("No es un archivo .xml").showError();
				return;
			} catch (JAXBException e) {
				Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error al guardar el entrenamiento", null, mainApplication);
				alert.showAndWait();
				//Dialogs.create().title("Error").masthead(null).message("Se ha producido un error al guardar el entrenamiento").showError();
				return;
			}
		}
	}

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
			//Dialogs.create().title("Error").masthead(null).message("No es un archivo .xml").showError();
			return;
		} catch (JAXBException e) {
			Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "Se ha producido un error al guardar el entrenamiento", null, mainApplication);
			alert.showAndWait();
			//Dialogs.create().title("Error").masthead(null).message("Se ha producido un error al guardar el entrenamiento").showError();
			return;
		}
	}

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
				//Dialogs.create().title("Error").masthead(null).message("Se ha producido un error al cargar el entrenamiento").showError();
				return;
			}
		}
	}
	
	@FXML
	public void seeTraining(){
		if(mainApplication.getAlg().getProbabilities().isEmpty()){
			Alert alert = Utils.createAlert(AlertType.ERROR, "Error", "No existe ningún conjunto de entrenamiento", null, mainApplication);
			alert.showAndWait();
			//Dialogs.create().title("Error").masthead(null).message("No existe ningún conjunto de entrenamiento").showError();
		}else{
			mainApplication.showNaiveBayesData();
		}
	}

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