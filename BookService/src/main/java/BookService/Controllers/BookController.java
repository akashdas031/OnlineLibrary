package BookService.Controllers;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import BookService.DTO.BookDetailsDTO;
import BookService.Entities.Book;
import BookService.Enums.BookType;
import BookService.Enums.GENRE;
import BookService.Exceptions.InvalidBookDetailsException;
import BookService.Response.ApiResponse;
import BookService.Services.BookService;
import BookService.ValidationRequests.ImageValidationRequest;
import BookService.ValidationRequests.PDFUploadRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/book/v1/")
@Validated
public class BookController {

	private BookService bookServ;
	
	private Logger logger=LoggerFactory.getLogger(BookController.class);
	
	public BookController(BookService bookServ) {
		this.bookServ = bookServ;
	}

	@PostMapping(value="/addBook",consumes={"multipart/form-data"})
	public ResponseEntity<ApiResponse> addBook(@Valid @RequestPart("book") Book book,@Valid @ModelAttribute PDFUploadRequest pdfBook,@Valid @ModelAttribute ImageValidationRequest imageBook){
		
		Book addBook=null;
		try {
			 addBook = this.bookServ.addBook(book, pdfBook, imageBook);
				ApiResponse resp = ApiResponse.builder().status(200).data(addBook).message("Hoorayy...You have added a book successfully").build();

			return new ResponseEntity<ApiResponse>(resp,HttpStatus.CREATED);
		} catch (IOException e) {
			e.printStackTrace();
			ApiResponse resp = ApiResponse.builder().status(500).data("Something Went Wrong...").message("Check The details carefully and try again...").build();

			return new ResponseEntity<ApiResponse>(resp,HttpStatus.NOT_FOUND);
		}
		
	}
	
	//get book details 
	@GetMapping("/getAllBooks")
	public ResponseEntity<ApiResponse> getAllBooks(){
		List<BookDetailsDTO> allBooks = this.bookServ.getAllBooks();
		logger.info("All Books : "+allBooks);
		ApiResponse response = ApiResponse.builder().message("Success").data(allBooks).status(200).build();
		return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
	}
	
	//get Single Book with book Id
	@GetMapping("/getSingleBook/{bookId}")
	public ResponseEntity<ApiResponse> getBookByBookId(@PathVariable("bookId") String bookId){
		BookDetailsDTO book = this.bookServ.getBookByBookId(bookId);
		if(book!=null) {
			ApiResponse response = ApiResponse.builder().status(200).message("Success").data(book).build();
            return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().status(404).message("Failure").data("Oops...The Id you are looking for is not available..Check the bookID and try again...").build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);

		}
	}
	
	//Update the book
	@PutMapping("/updateBook/{bookId}")
	public ResponseEntity<ApiResponse> updateBook(@Valid @RequestPart("book") Book book,@Valid @ModelAttribute PDFUploadRequest pdfBook,@Valid @ModelAttribute ImageValidationRequest imageBook,@PathVariable("bookId") String bookId) throws IOException{
		BookDetailsDTO updatedBook = this.bookServ.updateBook(book, pdfBook, imageBook, bookId);
		ApiResponse response = ApiResponse.builder().status(200).message("Success").data(updatedBook).build();
		return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
	}
	
	//Delete the book
	@DeleteMapping("/deleteBook/{bookId}")
	public ResponseEntity<ApiResponse> deleteBook(@PathVariable("bookId") String bookId) throws IOException{
		String deletedBook = this.bookServ.deleteBook(bookId);
		ApiResponse response = ApiResponse.builder().status(200).message("Success").data(deletedBook).build();
		return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
	}
	
	//Search book using bookId,BookName,AuthorName,BookType,Genre or any keyword related to book
	@GetMapping("/searchBook")
	public ResponseEntity<ApiResponse> searchBook(@Valid @RequestParam(required = false) String id,
			                                      @RequestParam(required=false) String bookName,
			                                      @RequestParam(required=false) String authorName,
			                                      @RequestParam(required=false) String bookType,
			                                      @RequestParam(required = false) String genre){
		GENRE genreD=null;
		if(genre !=null) {
			 if(!GENRE.isValidGenre(genre)) {
		        	throw new InvalidBookDetailsException("Please Enter Valid GENRE which must be [FICTION,NON_FICTION,SCIENCE,HISTORY,FANTACY,MYSTERY,BIOGRAPHY,SELF_HELP,TECHNOLOGY]");
		        }else {
		        	genreD=GENRE.valueOf(genre);
		        }
			 
		}
		BookType bookt=null;
        if(bookType !=null) {
        	 if(!BookType.isValidBookType(bookType)) {
             	throw new InvalidBookDetailsException("Book Type Must be [EBOOK,AUDIO_BOOK,HARD_COVER,PAPER_BACK,MAGAZINE ]");
             }else {
        	 bookt=BookType.valueOf(bookType);
             }
        }
       
       
		List<BookDetailsDTO> books = this.bookServ.searchBook(id, bookName, authorName, bookt, genreD);
		if(books==null || books.isEmpty()) {
			ApiResponse error = ApiResponse.builder().status(404).message("Failure").data(books).build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);

		}else {
			ApiResponse response = ApiResponse.builder().status(200).message("Success").data(books).build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}
		
		
	}
	//get books by book type with pagination
	@GetMapping("/booksByType")
	public ResponseEntity<ApiResponse> getBooksByBookType(@RequestParam String bookType,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "2 ") int pageSize){
		Pageable pageable = PageRequest.of(page, pageSize);
		BookType type=null;
		if(bookType !=null) {
       	 if(!BookType.isValidBookType(bookType)) {
            	throw new InvalidBookDetailsException("Book Type Must be [EBOOK,AUDIO_BOOK,HARD_COVER,PAPER_BACK,MAGAZINE ]");
            }else {
       	 type=BookType.valueOf(bookType);
            }
       }
		Page<BookDetailsDTO> allBooks = this.bookServ.getBooksByType(type, pageable);
		if(allBooks==null || allBooks.isEmpty()) {
			ApiResponse error = ApiResponse.builder().status(404).message("Failure").data("No Books Found For the Type...").build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);
		}else {
			ApiResponse response = ApiResponse.builder().status(200).message("Success").data(allBooks).build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}
	}
}
