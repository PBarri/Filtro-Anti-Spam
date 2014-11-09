package controllers;

import java.io.File;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import algorithms.NaiveBayes;
import application.MainApplication;

@SuppressWarnings("deprecation")
public class MainApplicationController {
	
	private MainApplication mainApplication;
	
	public MainApplicationController(){
		
	}

	public MainApplication getMainApplication() {
		return mainApplication;
	}

	public void setMainApplication(MainApplication mainApp) {
		this.mainApplication = mainApp;
	}
	
	@FXML
	private void close(){
		Platform.exit();
	}
	
	@FXML
	private void about(){
		String mensaje = "En esta aplicación se recrea el algoritmo de Naives Bayes ";
		mensaje += "y se aplica al filtrado de mensajes entre mensajes spam o no spam (ham)\n\n";
		mensaje += "Autores de la aplicación: \n";
		mensaje += "	Pablo Barrientos Lobato\n";
		mensaje += "	Alberto Salas Cantalejo";
		Dialogs.create().title("Acerca de...").masthead("Filtro Anti-Spam").message(mensaje).showInformation();
	}
	
	@FXML
	private void newTraining(){
		Action response = Dialogs.create().title("Confirma solicitud").masthead(null).message("¿Desea borrar los datos del entrenamiento?").showConfirm();
		if(response.equals(Dialog.ACTION_YES)){
			this.mainApplication.showHome(true);
			Dialogs.create().title("Nuevo entrenamiento").masthead(null).message("Todos los datos del entrenamiento realizado se han borrado").showInformation();
		}else if(response.equals(Dialog.ACTION_NO)){
			this.mainApplication.showHome(false);
		}
	}
	
	@FXML
	private void save(){
		Preferences pref = Preferences.userNodeForPackage(MainApplication.class);
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File(pref.get("saveFile", System.getProperty("user.home"))));
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files (*xml)", "*.xml");
		chooser.getExtensionFilters().add(filter);
		File file = chooser.showSaveDialog(this.mainApplication.getPrimaryStage());
		
		if(file != null){
			if(!file.getPath().endsWith("*.xml")){
				try {
					JAXBContext context = JAXBContext.newInstance(NaiveBayes.class);
					Marshaller m = context.createMarshaller();
					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					m.marshal(this.mainApplication.getAlg(), file);
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Dialogs.create().title("Error").masthead(null).message("No es un archivo .xml").showError();
			}
		}		
	}
	
	@FXML
	private void load(){
		Preferences pref  = Preferences.userNodeForPackage(MainApplication.class);
		NaiveBayes alg = new NaiveBayes();
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files (*xml)", "*.xml");
		chooser.getExtensionFilters().add(filter);
		chooser.setInitialDirectory(new File(pref.get("loadFile", System.getProperty("user.home"))));
		
		File file = chooser.showOpenDialog(mainApplication.getPrimaryStage());
		
		if(file != null){
			pref.put("loadFile", file.getAbsolutePath());
			try {
				JAXBContext context = JAXBContext.newInstance(NaiveBayes.class);
				Unmarshaller um = context.createUnmarshaller();
				
				alg = (NaiveBayes) um.unmarshal(file);
				
				this.mainApplication.setFilePath(file);
				this.mainApplication.setAlg(alg);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
