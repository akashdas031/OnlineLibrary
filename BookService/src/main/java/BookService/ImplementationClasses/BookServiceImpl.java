package BookService.ImplementationClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import BookService.DTO.BookDetailsDTO;
import BookService.Entities.Book;
import BookService.Exceptions.BookNotFoundException;
import BookService.Exceptions.BookPdfEmptyException;
import BookService.Repositories.BookRepository;
import BookService.Services.BookService;
import BookService.ValidationRequests.ImageValidationRequest;
import BookService.ValidationRequests.PDFUploadRequest;

@Service
public class BookServiceImpl implements BookService{

	private BookRepository bookRepo;
	
	private Logger logger=LoggerFactory.getLogger(BookServiceImpl.class);
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
		
		return bookRepo.save(book);
	}
    //get all books
	@Override
	public List<BookDetailsDTO> getAllBooks() {
	
		String bookPdfBaseURL="C:\\Users\\lenovo\\Desktop\\BookInventory\\AllBooks";
		String bookImageBaseURL="C:\\Users\\lenovo\\Desktop\\BookInventory\\BookImages";
		List<Book> books = this.bookRepo.findAll();
		logger.info("All Books : "+books);
		List<BookDetailsDTO> allBooks=new ArrayList<>();
			for(Book book:books) {
				BookDetailsDTO singleBook = BookDetailsDTO.builder().bookId(book.getId())
                        .bookName(book.getBookName())
                        .authorName(book.getAuthorName())
                        .bookDescription(book.getDescription())
                        .bookPdfFileName(book.getBookName())
                        .bookImageName(book.getBookImage())
                        .bookPdfDownloadUrl(bookPdfBaseURL+"\\"+book.getBookPdfName())
                        .bookImageUrl(bookImageBaseURL+"\\"+book.getBookImage())
                        .build();
				allBooks.add(singleBook);
			logger.info("Single Book : "+singleBook);
			
		}
			
		return allBooks;
	}

	//get single book
	@Override
	public BookDetailsDTO getBookByBookId(String bookId) {
		String bookPdfBaseURL="C:\\Users\\lenovo\\Desktop\\BookInventory\\AllBooks";
		String bookImageBaseURL="C:\\Users\\lenovo\\Desktop\\BookInventory\\BookImages";
		Book book = this.bookRepo.findById(bookId).orElseThrow(()->new BookNotFoundException("The Book you are searching with Id is not available in the server...Please Check the ID and try Again..."));
		BookDetailsDTO singleBook = BookDetailsDTO.builder().bookId(book.getId())
                .bookName(book.getBookName())
                .authorName(book.getAuthorName())
                .bookDescription(book.getDescription())
                .bookPdfFileName(book.getBookName())
                .bookImageName(book.getBookImage())
                .bookPdfDownloadUrl(bookPdfBaseURL+"\\"+book.getBookPdfName())
                .bookImageUrl(bookImageBaseURL+"\\"+book.getBookImage())
                .build();
		return singleBook;
	}

	@Override
	public BookDetailsDTO updateBook(Book book,PDFUploadRequest bookPdf,ImageValidationRequest bookImage,String bookId) throws IOException {
		String baseUrlImage="C:\\Users\\lenovo\\Desktop\\BookInventory\\BookImages";
		String baseUrlPdf="C:\\Users\\lenovo\\Desktop\\BookInventory\\AllBooks";

		
		Book ExistingBook = this.bookRepo.findById(bookId).orElseThrow(()->new BookNotFoundException("Please Enter Correct Id to update the book..."));
		ExistingBook.setBookName(book.getBookName());
		ExistingBook.setAuthorName(book.getAuthorName());
		ExistingBook.setDescription(book.getDescription());
		if(bookPdf !=null) {
			Path pdfPath=Paths.get(baseUrlPdf,bookPdf.getOriginalFileName());
			if(Files.exists(pdfPath)) {
				Files.delete(pdfPath);
			}
			Files.copy(bookPdf.getInputStream(),pdfPath,StandardCopyOption.REPLACE_EXISTING);
			ExistingBook.setBookPdfName(bookPdf.getOriginalFileName());
		}
		if(bookImage !=null) {
			Path imagePath=Paths.get(baseUrlImage,bookImage.getOriginalFileName());
			if(Files.deleteIfExists(imagePath)) {
				Files.delete(imagePath);
			}
			Files.copy(bookImage.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
			ExistingBook.setBookImage(bookImage.getOriginalFileName());
		}
		Book updatedBook = this.bookRepo.save(ExistingBook);
		BookDetailsDTO response = BookDetailsDTO.builder().bookId(bookId).bookName(updatedBook.getBookName())
		                                       .authorName(updatedBook.getAuthorName())
		                                       .bookDescription(updatedBook.getDescription())
		                                       .bookImageName(updatedBook.getBookImage())
		                                       .bookPdfFileName(updatedBook.getBookPdfName())
		                                       .bookImageUrl(baseUrlImage+"\\"+updatedBook.getBookImage())
		                                       .bookPdfDownloadUrl(baseUrlPdf+"\\"+updatedBook.getBookPdfName())
		                                       .build();
		            
		return response;
	}

	@Override
	public String deleteBook(String bookId) throws IOException {
		String baseUrlImage="C:\\Users\\lenovo\\Desktop\\BookInventory\\BookImages";
		String baseUrlPdf="C:\\Users\\lenovo\\Desktop\\BookInventory\\AllBooks";
		Book book = this.bookRepo.findById(bookId).orElseThrow(()->new BookNotFoundException("The book you want to delete is not available on the server..check the book id"));
		
		if(book !=null) {
			Path pdfPath=Paths.get(baseUrlPdf,book.getBookPdfName());
			if(Files.exists(pdfPath)) {
				Files.delete(pdfPath);
			}
			Path imagePath=Paths.get(baseUrlImage,book.getBookImage());
			if(Files.exists(imagePath)) {
				Files.delete(imagePath);
			}
			
            this.bookRepo.deleteById(bookId);
		}
		Book CheckAvailability = this.bookRepo.findById(bookId).orElseThrow(()->new BookNotFoundException("The Book Has Successfully Being Deleted"));
		if(CheckAvailability==null) {
			return "The Book with Id : "+bookId+" Has been Deleted Successfully";
		}else {
		return "Something went Wrong while Deleting the Book...Try again!!!";
		}
	}

}
