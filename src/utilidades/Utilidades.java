package utilidades;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excepciones.RutaInvalidaException;

public class Utilidades {
	
	private static final String SPAM = "spam";
	private static final String HAM = "ham";

	/**
	 * 
	 * @param ruta -> Ruta padre en la que estén todos los archivos
	 * @return -> Un mapa con el número de ocurrencias que tiene cada palabra
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
		}catch(NullPointerException e){
			System.out.println("Error al cargar la ruta");
		}catch(RutaInvalidaException e){
			System.out.println(e.getMessage());
		}
		
		
				
		return result;
	}
	
	/**
	 * 
	 * @param ruta -> Ruta padre en la que estén todos los archivos
	 * @return -> Un mapa con el número de ocurrencias que tiene cada palabra
	 */
	public static Map<String, Integer> cargarCorreosEntrenamientoHam(String ruta){
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		File file;
		List<File> filesToAnalize = new ArrayList<File>();

		try{
			file = new File(ruta);
			if(file.isDirectory()){
				recorrerDirectoriosHam(file, filesToAnalize);
			}else{
				throw new RutaInvalidaException("La ruta es inválida");
			}
		}catch(NullPointerException e){
			System.out.println("Error al cargar la ruta");
		}catch(RutaInvalidaException e){
			System.out.println(e.getMessage());
		}
		
		
				
		return result;
	}

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
	
}
