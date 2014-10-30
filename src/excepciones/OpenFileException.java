package excepciones;

import java.io.File;

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
