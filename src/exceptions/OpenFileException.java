package exceptions;

import java.io.File;
/**
 * Excepción que se muestra cuando hay un problema al abrir un archivo
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class OpenFileException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private File file;

	public OpenFileException(File file){
		super();
		this.file = file;
	}
	
	public OpenFileException(File file, String msg){
		super(msg);
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	@Override
	public String getMessage(){
		return "Ha ocurrido un error al abrir el fichero " + getFile().getName();
	}
	
}
