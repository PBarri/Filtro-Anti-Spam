package exceptions;
/**
 * Excepci�n de ruta inv�lida 
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class InvalidPathException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidPathException(){
		super();
	}
	
	public InvalidPathException(String msg){
		super(msg);
	}
	
}
