package exceptions;

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
