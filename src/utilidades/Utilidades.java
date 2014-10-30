package utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excepciones.OpenFileException;
import excepciones.RutaInvalidaException;

public class Utilidades {
	
	private static final String SPAM = "spam";
	private static final String HAM = "ham";

	/**
	 * Método para cargar en un Map los correos de entrenamiento que son spam
	 * @param ruta -> Ruta padre en la que estén todos los archivos
	 * @return Un mapa con el número de ocurrencias que tiene cada palabra
	 */
	public static Map<String, Integer> cargarCorreosEntrenamientoSpam(String ruta){
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		File file;
		List<File> filesToAnalize = new ArrayList<File>();

		try{
			file = new File(ruta);
			if(file.isDirectory()){
				recorrerDirectoriosSpam(file, filesToAnalize);
			}else{
				throw new RutaInvalidaException("La ruta es inválida");
			}
			
			if(!filesToAnalize.isEmpty()){
				for(File f : filesToAnalize){
					cargarPalabras(f, result);
				}
			}
			
		} catch (NullPointerException e) {
			System.out.println("Error al cargar la ruta");
		} catch (RutaInvalidaException e) {
			System.out.println(e.getMessage());
		} catch (OpenFileException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Ha ocurrido un error: " + e.getMessage());
		}
				
		return result;
	}

	/**
	 * Método para cargar en un Map los correos de entrenamiento que NO son spam
	 * @param ruta -> Ruta padre en la que estén todos los archivos
	 * @return Un mapa con el número de ocurrencias que tiene cada palabra
	 */
	public static Map<String, Integer> cargarCorreosEntrenamientoHam(String ruta){
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		File file;
		List<File> filesToAnalize = new ArrayList<File>();

		try{
			file = new File(ruta);
			if(file.isDirectory()){
				recorrerDirectoriosHam(file, filesToAnalize);
				if(!filesToAnalize.isEmpty()){
					for(File f : filesToAnalize){
						cargarPalabras(f, result);
					}
				}
			}else{
				throw new RutaInvalidaException();
			}
		} catch (NullPointerException e) {
			System.out.println("Error al cargar la ruta");
		} catch (RutaInvalidaException e) {
			System.out.println("La ruta indicada es inválida");
		} catch (OpenFileException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Ha ocurrido un error: " + e.getMessage());
		}
				
		return result;
	}

	/**
	 * 
	 * @param file -> Directorio/fichero a analizar
	 * @param filesToAnalize -> Lista de archivos que se utilizarán para cargar las palabras
	 */
	private static void recorrerDirectoriosSpam(File file, List<File> filesToAnalize) {
		if(file.isDirectory()){
			for(File f: file.listFiles()){
				recorrerDirectoriosSpam(f, filesToAnalize);
			}
		}else{
			if(file.getParent().equals(SPAM)){
				filesToAnalize.add(file);
			}
		}
	}
	
	/**
	 * 
	 * @param file -> Directorio/fichero a analizar
	 * @param filesToAnalize -> Lista de archivos que se utilizarán para cargar las palabras
	 */
	private static void recorrerDirectoriosHam(File file, List<File> filesToAnalize) {
		if(file.isDirectory()){
			for(File f: file.listFiles()){
				recorrerDirectoriosHam(f, filesToAnalize);
			}
		}else{
			if(file.getParent().equals(HAM)){
				filesToAnalize.add(file);
			}
		}
	}
	
	/**
	 * Método que se utiliza para cargar en el mapa todas las palabras relevantes de un fichero
	 * @param f -> Archivo que se está analizando
	 * @param result -> Mapa en el que se cargarán las palabras
	 * @throws OpenFileException 
	 */
	private static void cargarPalabras(File f, Map<String, Integer> result) throws OpenFileException {
		try{
			String text = Files.lines(f.toPath()).toString().toLowerCase();
			String[] words = text.split("([^a-zA-Z0-9])+");
			for(String s : words){
				if(!s.equals("subject")){
					if(!result.containsKey(s)){
						result.put(s, 1);
					}else{
						result.put(s, result.getOrDefault(s, 0) + 1);
					}
				}
			}
		}catch (IOException e){
			throw new OpenFileException(f);
		}
	}
	
}
