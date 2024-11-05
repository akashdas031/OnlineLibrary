package BookService.Exceptions;

public class InvalidBookDetailsException extends RuntimeException{

	public InvalidBookDetailsException() {
		super("Please Provide Valid DataType for the field...");
	}
	
	public InvalidBookDetailsException(String message) {
		super(message);
	}
}
