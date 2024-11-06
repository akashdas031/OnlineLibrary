package BookService.Services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import BookService.DTO.BookDetailsDTO;
import BookService.Entities.Book;
import BookService.Enums.BookType;
import BookService.Enums.GENRE;
import BookService.ValidationRequests.ImageValidationRequest;
import BookService.ValidationRequests.PDFUploadRequest;
import jakarta.validation.Valid;

public interface BookService {

	public Book addBook(Book book,@Valid PDFUploadRequest pdfBook,@Valid ImageValidationRequest imageBook) throws IOException;
	public List<BookDetailsDTO> getAllBooks();
	public BookDetailsDTO getBookByBookId(String bookId);
	public BookDetailsDTO updateBook(Book book,PDFUploadRequest bookPdf,ImageValidationRequest bookImage,String bookId) throws IOException;
	public String deleteBook(String bookId) throws IOException;
	public List<BookDetailsDTO> searchBook(String id,String bookName,String authorName, BookType bookType,GENRE genre);
}
