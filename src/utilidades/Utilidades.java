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

@SuppressWarnings("unused")
public class Utilidades {

	/**
	 * Método para cargar los correos de entrenamiento
	 * @param ruta -> Ruta padre en la que estén todos los archivos
	 * @param categoria -> Si un correo es spam o ham
	 * @return Una lista con todos los archivos que hay que analizar
	 */
	public static List<File> cargarCorreosEntrenamiento(String ruta, String categoria){
		
		File file;
		List<File> filesToAnalize = new ArrayList<File>();

		try{
			file = new File(ruta);
			if(file.isDirectory()){
				recorrerDirectorios(file, filesToAnalize, categoria);
			}else{
				throw new RutaInvalidaException("La ruta es inválida");
			}			
		} catch (NullPointerException e) {
			System.out.println("Error al cargar la ruta");
		} catch (RutaInvalidaException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Ha ocurrido un error: " + e.getMessage());
		}
				
		return filesToAnalize;
	}

	/**
	 * Método que recorre los directorios, insertando en filesToAnalize todos los archivos deseados
	 * @param file -> Directorio/fichero a analizar
	 * @param filesToAnalize -> Lista de archivos que se utilizarán para cargar las palabras
	 * @param categoria -> Si es spam o ham
	 */
	private static void recorrerDirectorios(File file, List<File> filesToAnalize, String categoria) {
		if(file.isDirectory()){
			for(File f: file.listFiles()){
				recorrerDirectorios(f, filesToAnalize, categoria);
			}
		}else{
			if(file.getParentFile().getName().equals(categoria)){
				filesToAnalize.add(file);
			}
		}
	}
	
	/**
	 * Método que se utiliza para cargar en el mapa todas las palabras relevantes de un fichero
	 * @param f -> Archivo que se está analizando
	 * @param result -> Mapa en el que se cargarán las palabras
	 * @throws OpenFileException -> Error al abrir el archivo
	 */
	private static Map<String, Integer> cargarPalabras(List<File> filesToAnalize) throws OpenFileException {
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		for(File f : filesToAnalize){
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
		
		return result;
	}	
}
