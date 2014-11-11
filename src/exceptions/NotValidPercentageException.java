package exceptions;

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
