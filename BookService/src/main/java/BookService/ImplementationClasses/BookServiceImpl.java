package BookService.ImplementationClasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import BookService.DTO.BookDetailsDTO;
import BookService.Entities.Book;
import BookService.Enums.BookType;
import BookService.Enums.GENRE;
import BookService.Exceptions.BookNotFoundException;
import BookService.Exceptions.BookPdfEmptyException;
import BookService.Exceptions.InvalidPageException;
import BookService.Repositories.BookRepository;
import BookService.Response.BookPageResponse;
import BookService.Services.BookService;
import BookService.ValidationRequests.ImageValidationRequest;
import BookService.ValidationRequests.PDFUploadRequest;

@Service
public class BookServiceImpl implements BookService{

	String baseUrlImage="C:\\Users\\lenovo\\Desktop\\BookInventory\\BookImages";
	String baseUrlPdf="C:\\Users\\lenovo\\Desktop\\BookInventory\\AllBooks";
	
	private BookRepository bookRepo;
	
	private Logger logger=LoggerFactory.getLogger(BookServiceImpl.class);
	@Autowired
	public BookServiceImpl(BookRepository bookRepo) {
		this.bookRepo = bookRepo;
	}

	@Override
	public Book addBook(Book book,PDFUploadRequest bookPdf,ImageValidationRequest bookImage) throws IOException {
		String bookId=UUID.randomUUID().toString().substring(0,10).replace("\s", "");
		book.setPublicstionTime(LocalDateTime.now());
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
                        .bookPdfDownloadUrl(bookPdfBaseURL+File.separator+book.getBookPdfName())
                        .bookImageUrl(bookImageBaseURL+File.separator+book.getBookImage())
                        .bookType(book.getBookType())
                        .genre(book.getGenre())
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
                .bookPdfDownloadUrl(bookPdfBaseURL+File.separator+book.getBookPdfName())
                .bookImageUrl(bookImageBaseURL+File.separator+book.getBookImage())
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
		ExistingBook.setBookType(book.getBookType());
		ExistingBook.setGenre(book.getGenre());
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
		                                       .bookImageUrl(baseUrlImage+File.separator+updatedBook.getBookImage())
		                                       .bookPdfDownloadUrl(baseUrlPdf+File.separator+updatedBook.getBookPdfName())
		                                       .bookType(updatedBook.getBookType())
		                                       .genre(updatedBook.getGenre())
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

	@Override
	public List<BookDetailsDTO> searchBook(String id, String bookName, String authorName, BookType bookType, GENRE genre) {
		
		List<Book> allBooks = this.bookRepo.searchBooks(id, authorName, bookType, genre, bookName);
		List<BookDetailsDTO> responseBookList=new ArrayList<>();
		for(Book book:allBooks) {
			BookDetailsDTO respBook = BookDetailsDTO.builder().bookId(book.getId())
			                        .bookName(book.getBookName())
			                        .authorName(book.getAuthorName())
			                        .bookDescription(book.getDescription())
			                        .bookPdfFileName(book.getBookPdfName())
			                        .bookImageName(book.getBookImage())
			                        .bookPdfDownloadUrl(baseUrlPdf+File.separator+book.getBookPdfName())
			                        .bookImageUrl(baseUrlImage+File.separator+book.getBookImage())
			                        .bookType(book.getBookType())
			                        .genre(book.getGenre())
			                        .build();
			responseBookList.add(respBook);
		}
		return responseBookList;
	}
	//Get book By book type with Pagination

	@Override
	public Page<BookDetailsDTO> getBooksByType(BookType bookType, Pageable pageable) {
		Page<Book> books = bookRepo.findByBookType(bookType, pageable);
		Page<BookDetailsDTO> booksByType = books.map(book->{
		return	BookDetailsDTO.builder().bookId(book.getId())
			                        .bookName(book.getBookName())
			                        .authorName(book.getAuthorName())
			                        .bookDescription(book.getDescription())
			                        .bookPdfFileName(book.getBookPdfName())
			                        .bookImageName(book.getBookImage())
			                        .bookPdfDownloadUrl(baseUrlPdf+File.separator+book.getBookPdfName())
			                        .bookImageUrl(baseUrlImage+File.separator+book.getBookImage())
			                        .bookType(book.getBookType())
			                        .genre(book.getGenre())
			                        .build();
		});
		return booksByType;
	}

	//Read book by page
	@Override
	public BookPageResponse getBookPage(String bookId, int page) throws IOException {
		Book book=bookRepo.findById(bookId).orElseThrow(()->new BookNotFoundException("Book you are looking for is not available..."));
		String content=extractTextFromPdf(new File(baseUrlPdf+File.separator+book.getBookPdfName()));
		int totalPages=getTotalPage(content);
		if(page<1 || page >totalPages) {
			throw new InvalidPageException("Invalid page range...");
		}
		String pageContent=getContentForPage(content,page,totalPages);
		return new BookPageResponse(page,totalPages,pageContent); 
	}
	private String extractTextFromPdf(File pdfFile)throws IOException{
		if(!pdfFile.exists()) {
			throw new BookNotFoundException("The book is not available on the server");
		}
		PDDocument document=Loader.loadPDF(pdfFile);
		PDFTextStripper stripper=new PDFTextStripper();
		String text = stripper.getText(document);
		return text;
	}
	private int getTotalPage(String content) {
		final int charsPerPage=1000;
		return (int)Math.ceil((double)content.length()/charsPerPage);
	}
	private String getContentForPage(String content,int page,int totalPages) {
		final int charsPerPage=1000;
		int start=(page-1)*charsPerPage;
		int end=Math.min(start+charsPerPage, content.length());
		return content.substring(start,end);
	}

}
