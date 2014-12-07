package exceptions;
/**
 * Excepci�n que se lanza cuando se intenta entrenar escogiendo un porcentaje inv�lido
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class NotValidPercentageException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NotValidPercentageException(){
		super();
	}
	
	public NotValidPercentageException(String msg){
		super(msg);
	}

}
