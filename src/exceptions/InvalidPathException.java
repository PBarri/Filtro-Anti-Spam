package exceptions;

public class InvalidPathException extends RuntimeException {

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
