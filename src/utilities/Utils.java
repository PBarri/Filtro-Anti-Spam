package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import application.MainApplication;

import com.google.common.io.Files;

import exceptions.OpenFileException;
/**
 * Clase auxiliar con un conjunto de m�todos �tiles para el resto de la aplicaci�n
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class Utils {
	
	/**
	 * M�todo est�tico que convierte la lista de Float que crea la predicci�n a una lista de String con los mensajes que se mostrar�n en la interfaz
	 * @param predictions Lista de float en el mapa de predicciones correspondiente al valor de una clave de dicho mapa
	 * @return Lista de String  con las cadenas que se mostrar�n en la interfaz
	 */
	public static List<String> convertPredictions(List<Float> predictions){
		List<String> result = new ArrayList<String>();
		for(Float f : predictions){
			switch(predictions.indexOf(f)){
			case 0:
				result.add(f.toString());
				break;
			case 1:
				result.add(f.toString());
				break;
			case 2:
				if(f.equals(new Float(1))){
					result.add("Spam");
				}else if(f.equals(new Float(2))){
					result.add("Ham");
				}else{
					result.add("Error clasificaci�n");
				}
				break;
			case 3:
				if(f.equals(new Float(1))){
					result.add("Spam");
				}else{
					result.add("Ham");
				}
				break;
			default:
				break;
			}
		}
		return result;
	}
	
	/**
	 * M�todo est�tico que calcula un porcentaje dados los datos
	 * @param number Numerador
	 * @param total Total sobre el que hacer el porcentaje
	 * @return Una cadena con el valor del porcentaje con dos decimales
	 */
	public static String getPercentage(Number number, Number total){
		DecimalFormat df = new DecimalFormat("#.##");
		Float percentage;
		if(total != null){
			percentage = (number.floatValue() / total.floatValue()) * 100;
		}else{
			percentage = number.floatValue() * 100;
		}		
		return df.format(percentage) + " %";
	}
	
	/**
	 * M�todo que recorre los directorios, insertando en filesToAnalize todos
	 * los archivos deseados
	 * 
	 * @param file Directorio/fichero a analizar
	 * @param filesToAnalize Lista de archivos que se utilizar�n para cargar las palabras
	 * @throws NullPointerException Fallo al abrir la ruta que se ha pasado
	 */
	public static void iterateDirectories(File file, List<File> filesToAnalize) throws NullPointerException {
		// Si el archivo es un directorio, se van recorriendo todos sus hijos llamando recursivamente a este m�todo
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				iterateDirectories(f, filesToAnalize);
			}
		} else { // Si es un archivo se manda a una funci�n para que sea analizado
			// Comprobamos que los archivos est�n dentro de las carpetas spam o ham
			if (file.getParentFile().getName().toLowerCase().matches("spam|ham")) {
				filesToAnalize.add(file);
			}
		}
	}
	
	/**
	 * M�todo que se utiliza para leer el contenido del archivo
	 * @param file Archivo del que se quiere leer el contenido
	 * @throws OpenFileException Cuando se produce un error al acceder a alg�n archivo
	 * @return Un array de String con todas las palabras que contiene el archivo
	 */
	public static String[] loadWords(File file) throws OpenFileException {
		try {
			// Pasamos a una cadena de texto el correo
			String content = Files.toString(file, Charset.defaultCharset());
			// Se ponen en min�scula
			content.toLowerCase();
			// Expresi�n regular que separa por todos los signos de puntuaci�n
			String[] fileWords = content.split("([^a-zA-Z0-9])+");

			return fileWords;
		} catch (IOException e) {
			throw new OpenFileException(file);
		}
	}
	
	/**
	 * M�todo que, dado un porcentaje y dos listas (entrenamiento y predicci�n), rellena ambas listas con los correos que se utilizar�n en cada una
	 * @param percentage Porcentaje que se utilizar� para los correos de entrenamiento
	 * @param filesToTrain Lista en la que se depositar�n los correos dedicados al entrenamiento
	 * @param filesToPredict Lista en la que se depositar�n los correos dedicados a la predicci�n
	 */
	public static void getFilesByPercentage(Integer percentage, List<File> filesToTrain, List<File> filesToPredict){
		
		// Se crea una lista auxiliar con todos los correos contenidos en filesToTrain
		List<File> allFiles = new ArrayList<File>(filesToTrain);
		// Se vac�a la lista de filesToTrain
		filesToTrain.clear();
		
		// Se calcula el n�mero de correos que se necesitar�n para entrenar
		Integer nFilesToTrain = allFiles.size() * percentage / 100;
		
		// Se desordena la lista auxiliar con todos los contenidos
		Collections.shuffle(allFiles);
		
		// Se a�aden a filesToTrain el n�mero de correos calculado anteriormente
		filesToTrain.addAll(allFiles.subList(0, nFilesToTrain));
		// Se a�aden a filesToPredict el resto de correos
		filesToPredict.addAll(allFiles.subList(nFilesToTrain, allFiles.size()));
		
	}
	
	/**
	 * M�todo que crea un Alert
	 * @param type Tipo de alerta
	 * @see AlertType
	 * @param title T�tulo de la alerta
	 * @param content Contenido de la alerta
	 * @param head Cabecera de la alerta
	 * @param app Referencia a la aplicaci�n
	 * @return Un Alert de JavaFx8 inicializado
	 */
	public static Alert createAlert(AlertType type, String title, String content, String head, MainApplication app){
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.setHeaderText(null);
		
		// Se le asigna el mismo icono que a la aplicaci�n
		Stage stage = (Stage) (alert.getDialogPane().getScene().getWindow());
		stage.getIcons().add(new Image("/css/ico.png"));
		
		return alert;
	}
	
	/**
	 * M�todo que filtra un mapa, eliminando todas las claves cuyo valor sea inferior o igual a 1
	 * En nuestro c�digo, esto elimina a todas aquellas palabras que hayan aparecido solo una vez en los correos de entrenamiento
	 * @param map Mapa sobre el que se quiere aplicar el filtro
	 * @return Un mapa con el filtro aplicado
	 */
	public static Map<String, Integer> filterMap(Map<String, Integer> map){
		
		// Filtro aplicado mediante una expresi�n lambda
		Map<String, Integer> result = map.entrySet().stream()
				.filter(p -> p.getValue() <= 1)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		
		return result;
		
	}

}
