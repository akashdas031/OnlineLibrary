package BookService.Exceptions;

public class BookNotFoundException extends RuntimeException{

	public BookNotFoundException() {
		super("Book with given id is not available in the server...");
	}
	public BookNotFoundException(String message) {
		super(message);
	}
}
