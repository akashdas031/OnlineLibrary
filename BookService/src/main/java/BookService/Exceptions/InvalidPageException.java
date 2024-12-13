package BookService.Exceptions;

public class InvalidPageException extends RuntimeException{

	public InvalidPageException(){
		super("Invalid page number");
	}
	public InvalidPageException(String message) {
		super(message);
	}
}
