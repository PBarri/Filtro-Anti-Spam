package exceptions;

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
