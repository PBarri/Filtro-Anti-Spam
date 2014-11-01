package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import exceptions.OpenFileException;
import exceptions.InvalidPathException;

@SuppressWarnings("unused")
public class Utilities {

	/**
	 * Método para cargar los correos de entrenamiento
	 * @param path -> Ruta padre en la que estén todos los archivos
	 * @param category -> Si un correo es spam o ham
	 * @return Una lista con todos los archivos que hay que analizar 
	 */
	
	public static Integer numeroDocumentosSpam= 0;
	public static Integer numeroDocumentosHam = 0;
	public static Map<String, Integer> mapspam = new HashMap<String, Integer>();
	public static Map<String, Integer> mapham =  new HashMap<String, Integer>();
	
	
	public  static String auxiliarSpam =" ";
	public  static String auxiliarHam =" ";
	

	public static List<File> loadTrainingMails(String path) {
		
		File file;
		List<File> filesToAnalize = new ArrayList<File>();

		try{
			file = new File(path);
			// Comprobamos que nos han pasado una ruta de una carpeta y no de un archivo
			if(file.isDirectory()){
				
				iterateDirectories(file);
			}else{
				throw new InvalidPathException("La ruta es inválida");
			}			
		
		} catch (NullPointerException e) {
			throw new NullPointerException("Inserte la ruta");
		} 
				
		return filesToAnalize;
	}

	/**
	 * Método que recorre los directorios, insertando en filesToAnalize todos los archivos deseados
	 * @param file -> Directorio/fichero a analizar
	 * @param filesToAnalize -> Lista de archivos que se utilizarán para cargar las palabras
	 * @param category -> Si es spam o ham
	 */
	private static void iterateDirectories(File file) {
		// Si el archivo es un directorio, se van recorriendo todos sus hijos llamando recursivamente a este método
		
		if(file.isDirectory()){
			for(File f: file.listFiles()){
				iterateDirectories(f);
			}
		}else{ // Si es un archivo se manda a una función para que sea analizado
			
			
			if(file.getParentFile().getName().equals("spam")){
				loadWords(file,"spam");
				numeroDocumentosSpam++;
			}else
			{
				
				loadWords(file,"ham");
				numeroDocumentosHam++;
			}
		
		}
		

	}
	
	/**
	 * Método que se utiliza para cargar en el mapa todas las palabras relevantes de un fichero
	 * @param f -> Archivo que se está analizando
	 * @param result -> Mapa en el que se cargarán las palabras
	 * @throws OpenFileException -> Error al abrir el archivo
	 */
	public static void loadWords(File file , String categoria) throws OpenFileException {
		
	
		
			
			try{
				// Pasamos a una cadena de texto el correo
				FileReader fr = new FileReader (file);
				char[] chars = new char[(int) file.length()];
				fr.read(chars);
				String content = new String(chars);
				fr.close();
				if(categoria.equals("spam"))
					auxiliarSpam.concat(content+" ");
				else
					auxiliarHam.concat(content+" ");
				// Separamos las palabras y las ponemos en un array de String
				String[] words = content.split("([^a-zA-Z0-9])+");
				
				// Vamos añadiendo al mapa devuelto el número de veces que aparece esa palabra
				for(String s : words){
					
					if(categoria.equals("spam")){
						
								if(!mapspam.containsKey(s)){
									mapspam.put(s, 1);
								}else{
									mapspam.put(s, mapspam.get(s) +1);
								}
					}else{
						
						if(!mapham.containsKey(s)){
							mapham.put(s, 1);
						}else{
							mapham.put(s, mapham.get(s) +1);
						}
						
					}
				}
			}catch (IOException e){
				throw new OpenFileException(file);
			}
				
		
		
	}
	
	/**
	 * Método consistente en generar las probabilidades de que una palabra sea o no spam
	 * @param spamWords -> Palabras obtenidas de los correos spam
	 * @param hamWords -> Palabras obtenidas de los correos que no son spam
	 * @return Un mapa con la palabra como clave, y una lista de la forma [Probabilidad de que sea spam, Probabilidad de que sea ham]
	 */
	public static Map<String, List<Float>> generateProbabilities(Map<String, Integer> spamWords, Map<String, Integer> hamWords){
		
		Map<String, List<Float>> result = new HashMap<String, List<Float>>();
		List<Float> aux = new ArrayList<Float>();
		Float prob = new Float(0.0);
		Integer totalWords = 0;

		// Contamos el número de palabras existentes en los mapas que se le pasan al método
		for(Integer i :spamWords.values()){
			totalWords += i;
		}
		for(Integer i : hamWords.values()){
			totalWords += i;
		}
		
		// Se recorren los mapas para calcular las probabilidades
		for(Entry<String, Integer> entry : spamWords.entrySet()){
			// Limpiamos la lista auxiliar
			aux.clear();
			// Calculamos la probabilidad de que la palabra sea spam
			prob = entry.getValue().floatValue() / totalWords;
			aux.add(0, prob);
			// Añadimos una probabilidad 0 de que no sea spam (por si no aparece en el otro mapa)
			aux.add(1, new Float(0.0));
			result.put(entry.getKey(), aux);
		}
		
		for(Entry<String, Integer> entry : hamWords.entrySet()){
			// Limpiamos la lista auxiliar
			aux.clear();
			// Calculamos la probabilidad de que la palabra sea ham
			prob = entry.getValue().floatValue() / totalWords;
			if(result.containsKey(entry.getKey())){ // Apareció en el mapa de palabras spam
				aux = result.get(entry.getKey());
				aux.set(1, prob);
			}else{ // Palabra que no apareció en los correos spam
				// Probabilidad 0 de que sea spam
				aux.add(0, new Float(0.0));
				aux.add(1, prob);
			}
			result.put(entry.getKey(), aux);
		}
		
		return result;
	}
}
