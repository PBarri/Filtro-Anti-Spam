package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

import exceptions.OpenFileException;

public class Utils {
	
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
					result.add("Error clasificación");
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
	
	public static void iterateDirectories(File file, List<File> filesToAnalize) throws NullPointerException {
		// Si el archivo es un directorio, se van recorriendo todos sus hijos llamando recursivamente a este método
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				iterateDirectories(f, filesToAnalize);
			}
		} else { // Si es un archivo se manda a una función para que sea analizado
			// Comprobamos que los archivos están dentro de las carpetas spam o ham
			if (file.getParentFile().getName().toLowerCase().matches("spam|ham")) {
				filesToAnalize.add(file);
			}
		}
	}
	
	public static String[] loadWords(File file) throws OpenFileException {
		try {
			// Pasamos a una cadena de texto el correo
			String content = Files.toString(file, Charset.defaultCharset());
			// Se ponen en minúscula
			content.toLowerCase();
			// Expresión regular que separa por todos los signos de puntuación
			String[] fileWords = content.split("([^a-zA-Z0-9])+");

			return fileWords;
		} catch (IOException e) {
			throw new OpenFileException(file);
		}
	}

}
