package BookService.Exceptions;

public class BookPdfEmptyException extends RuntimeException{

	public BookPdfEmptyException(String message){
		super(message);
	}
	public BookPdfEmptyException(){
		super("BookPDF is empty...Please select the book");
	}
}
