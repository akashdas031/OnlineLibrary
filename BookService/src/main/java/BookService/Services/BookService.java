package BookService.Services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import BookService.Entities.Book;
import BookService.ValidationRequests.ImageValidationRequest;
import BookService.ValidationRequests.PDFUploadRequest;
import jakarta.validation.Valid;

public interface BookService {

	public Book addBook(Book book,@Valid PDFUploadRequest pdfBook,@Valid ImageValidationRequest imageBook) throws IOException;
	public List<Book> getAllBooks();
	public Book getBookByBookId(String bookId);
	public Book updateBook(Book book);
	public String deleteBook(String bookId);
}
