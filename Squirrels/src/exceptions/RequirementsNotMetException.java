package exceptions;

public class RequirementsNotMetException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequirementsNotMetException(String message){
		super(message);
	}
}
