package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;

import org.controlsfx.dialog.Dialogs;

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
	private void home(){
		this.mainApplication.showHome(false);
	}
	
	@FXML
	private void newTraining(){
		this.mainApplication.showHome(true);
	}
}
