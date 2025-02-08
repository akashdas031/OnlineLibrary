package BookmarkService.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import BookmarkService.Entities.BookMarkDetails;
import BookmarkService.Response.ApiResponse;
import BookmarkService.Services.BookmarkService;

@RestController
@RequestMapping("onlinelibrary/bookmark/v1/")
public class BookmarkController{
	
	private BookmarkService bookmarkService;
	
	private BookmarkController(BookmarkService bookmarkService) {
		this.bookmarkService=bookmarkService;
	}
	
	@PostMapping("/createBookmark")
	public ResponseEntity<ApiResponse> createBookMark(@RequestBody BookMarkDetails bookmarkDetails){
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

}
