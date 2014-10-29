package excepciones;

public class RutaInvalidaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RutaInvalidaException(){
		super();
	}
	
	public RutaInvalidaException(String msg){
		super(msg);
	}
	
}
