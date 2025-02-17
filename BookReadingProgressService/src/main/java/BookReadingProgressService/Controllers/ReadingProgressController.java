package BookReadingProgressService.Controllers;

import java.net.http.HttpRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import BookReadingProgressService.Entities.ReadingProgress;
import BookReadingProgressService.Responses.ApiResponse;
import BookReadingProgressService.Services.ReadingProgressService;

@RestController
@RequestMapping("OnlineLibrary/progress/v1/")
public class ReadingProgressController {

	private ReadingProgressService readingProgress;
	
	private ReadingProgressController(ReadingProgressService readingProgress) {
		this.readingProgress=readingProgress;
	}
	
	//create progress when user start reading the book
	
	@PostMapping("/startReading/{userId}/{bookId}/{totalPages}")
	public ResponseEntity<ApiResponse> startReading(@PathVariable("userId") String userId,@PathVariable("bookId") String bookId,@PathVariable
			("totalPages")int totalPages){
		ReadingProgress createdReading = this.readingProgress.startReading(userId, bookId, totalPages);
		if(createdReading != null) {
			ApiResponse response = ApiResponse.builder().data(createdReading)
								 .message("Reading Started...Keep reading and track your progress...")
								 .status("Success")
								 .build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.CREATED);
		}else {
			ApiResponse error = ApiResponse.builder().data("Something went wrong on the server...")
					 .message("Please Check the details and try after sometime...")
					 .status("Failure")
					 .build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//update the progress when user changes the page 
	@PutMapping("/updateProgress/{userId}/{bookId}/{currentPage}")
	public ResponseEntity<ApiResponse> updateProgress(@PathVariable("userId")String userId,@PathVariable("bookId")String bookId,@PathVariable("currentPage") int currentPage){
		ReadingProgress updatedStatus = this.readingProgress.updateProgress(userId, bookId, currentPage);
		if(updatedStatus != null) {
			ApiResponse response = ApiResponse.builder().data(updatedStatus)
					 .message("Great work You have completed "+updatedStatus.getProgressPercentage()+"% of the book...")
					 .status("Success")
					 .build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().data("Something went wrong on the server...")
					 .message("Please Check the details and try after sometime...")
					 .status("Failure")
					 .build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//View the progress that the user made
	@GetMapping("/getProgress/{userId}/{bookId}")
	public ResponseEntity<ApiResponse> getProgress(@PathVariable("userId")String userId,@PathVariable("bookId")String bookId){
		ReadingProgress progress = this.readingProgress.getProgress(userId, bookId);
		if(progress != null) {
			ApiResponse response = ApiResponse.builder().data(progress)
					 .message("Great work You have completed "+progress.getProgressPercentage()+"% of the book...")
					 .status("Success")
					 .build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().data("Something went wrong on the server...")
					 .message("Please Check the details and try after sometime...")
					 .status("Failure")
					 .build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
