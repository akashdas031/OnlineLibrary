package BookmarkService.Controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import BookmarkService.Entities.BookMarkDetails;
import BookmarkService.Response.ApiResponse;
import BookmarkService.Response.BookResponseDTO;
import BookmarkService.Services.BookmarkService;

@RestController
@RequestMapping("onlinelibrary/bookmark/v1/")
public class BookmarkController{
	
	private BookmarkService bookmarkService;
	private Logger logger=LoggerFactory.getLogger(BookmarkController.class);
	private BookmarkController(BookmarkService bookmarkService) {
		this.bookmarkService=bookmarkService;
	}
	
	@PostMapping("/createBookmark")
	public ResponseEntity<ApiResponse> createBookMark(@RequestBody BookMarkDetails bookmarkDetails){
		logger.info("isFavorite in controller : "+bookmarkDetails.isFavorite());
		logger.info("Bookmark in controller : "+bookmarkDetails);
		BookMarkDetails createdBookMark = this.bookmarkService.createBookMark(bookmarkDetails);
		if(createdBookMark != null) {
			ApiResponse response = ApiResponse.builder().data(createdBookMark)
			                     .message("Bookmark Created")
			                     .status("Success")
			                     .httpStatus(HttpStatus.CREATED)
			                     .build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.CREATED);
		}else {
			ApiResponse error = ApiResponse.builder().data("Something Went Wrong!!!")
                    .message("Bookmark creation error...")
                    .status("Failure")
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//get All bookmark details
	@GetMapping("/getAllBookmarkedList")
	public ResponseEntity<ApiResponse> getAllBookmarkDetails(){
		List<BookMarkDetails> allBookmarkDetails = this.bookmarkService.getAllBookmarkDetails();
		if(allBookmarkDetails != null) {
			ApiResponse response = ApiResponse.builder().data(allBookmarkDetails)
                    .message("All Bookmarked details")
                    .status("Success")
                    .httpStatus(HttpStatus.OK)
                    .build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().data("No data Found in the Server")
                    .message("Something went wrong")
                    .status("Failure")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/getSingleBookmarkDetails")
	public ResponseEntity<ApiResponse> getSingleBookmarkDetails(@RequestParam("bookmarkDetails") String bookmarkDetails){
		BookMarkDetails bookMarkDetailsByBookmarkId = this.bookmarkService.getBookMarkDetailsByBookmarkId(bookmarkDetails);
		if(bookMarkDetailsByBookmarkId != null) {
			ApiResponse response = ApiResponse.builder().data(bookMarkDetailsByBookmarkId)
                    .message("Single bookmark details")
                    .status("Success")
                    .httpStatus(HttpStatus.OK)
                    .build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().data("No data Found in the Server")
                    .message("Something went wrong")
                    .status("Failure")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/user/getUsersBookmark/{userId}")
	public ResponseEntity<ApiResponse> getBookmarksByUserId(@PathVariable("userId") String userId){
		List<BookResponseDTO> allBookmarksByUserId = this.bookmarkService.getAllBookmarksByUserId(userId);
		if(allBookmarksByUserId != null) {
			ApiResponse response = ApiResponse.builder().data(allBookmarksByUserId)
                    .message("Bookmark Details of the user : "+userId)
                    .status("Success")
                    .httpStatus(HttpStatus.OK)
                    .build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().data("The user has not bookmarked yet...")
                    .message("No bookmark record found in the server")
                    .status("Failure")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping("/books/getBookmarkedBooks/{bookId}")
	public ResponseEntity<ApiResponse> getBookmarkByBookId(@PathVariable("bookId") String bookId){
		List<BookResponseDTO> bookmark = this.bookmarkService.getAllBookmarksByBookId(bookId);
		if(bookmark != null) {
			ApiResponse response = ApiResponse.builder().data(bookmark)
                    .message("Bookmark Details of the Book : "+bookId)
                    .status("Success")
                    .httpStatus(HttpStatus.OK)
                    .build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().data("The book has not bookmarked yet...")
                    .message("No bookmark record found in the server")
                    .status("Failure")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping("/user/getFavoriteBooks/{userId}")
	public ResponseEntity<ApiResponse> getFavoriteBooks(@PathVariable("userId") String userId){
		List<BookMarkDetails> favoriteBooks = this.bookmarkService.getFavoriteBooks(userId);
		if(!favoriteBooks.isEmpty()) {
			ApiResponse response = ApiResponse.builder().data(favoriteBooks)
                    .message("List of Favorite Books...")
                    .status("Success")
                    .httpStatus(HttpStatus.OK)
                    .build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().data("You have not marked any book as your favorite book yet...")
                    .message("Haven't feel in love with any book ??? Read some books and fall in love with the interesting chapters...")
                    .status("Failure")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);
		}
	}

}
