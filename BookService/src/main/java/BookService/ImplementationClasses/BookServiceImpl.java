package BookService.ImplementationClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import BookService.Entities.Book;
import BookService.Exceptions.BookPdfEmptyException;
import BookService.Repositories.BookRepository;
import BookService.Services.BookService;
import BookService.ValidationRequests.ImageValidationRequest;
import BookService.ValidationRequests.PDFUploadRequest;

@Service
public class BookServiceImpl implements BookService{

	private BookRepository bookRepo;
	
	@Autowired
	public BookServiceImpl(BookRepository bookRepo) {
		this.bookRepo = bookRepo;
	}

	@Override
	public Book addBook(Book book,PDFUploadRequest bookPdf,ImageValidationRequest bookImage) throws IOException {
		String bookId=UUID.randomUUID().toString().substring(0,10).replace("\s", "");
		
		String bookPdfPath="C:\\Users\\lenovo\\Desktop\\BookInventory\\AllBooks";
		String bookImagePath="C:\\Users\\lenovo\\Desktop\\BookInventory\\BookImages";
		Path pdfPath=Paths.get(bookPdfPath, bookPdf.getOriginalFileName());
		Path imagePath=Paths.get(bookImagePath,bookImage.getOriginalFileName());
		
		
		
		book.setBookPdfName(bookPdf.getOriginalFileName());
		Files.copy(bookPdf.getInputStream(),pdfPath);
		
		  
		book.setBookImage(bookImage.getOriginalFileName());
		Files.copy(bookImage.getInputStream(), imagePath);
		
		
		book.setId(bookId);
		
		return book;
	}

	@Override
	public List<Book> getAllBooks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Book getBookByBookId(String bookId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Book updateBook(Book book) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteBook(String bookId) {
		// TODO Auto-generated method stub
		return null;
	}

}
