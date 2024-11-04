package BookService.Controllers;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import BookService.Entities.Book;
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
}
