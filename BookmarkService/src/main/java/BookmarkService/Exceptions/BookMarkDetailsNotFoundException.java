package BookmarkService.Exceptions;

public class BookMarkDetailsNotFoundException extends RuntimeException{

	public BookMarkDetailsNotFoundException() {
		super("Bookmark details with given id is not available in the server");
	}
	public BookMarkDetailsNotFoundException(String message) {
		super(message);
	}
}
