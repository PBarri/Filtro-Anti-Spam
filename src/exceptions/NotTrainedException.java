package exceptions;
/**
 * Excepción que ocurre cuando se intenta predecir sin haber entrenado antes
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class NotTrainedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NotTrainedException(){
		super();
	}
	
	public NotTrainedException(String msg){
		super(msg);
	}

}
