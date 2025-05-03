package UserActivityService.Controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import UserActivityService.Entities.UserActivity;
import UserActivityService.Responses.ApiResponse;
import UserActivityService.Services.UserActivityWebService;

@RestController
@RequestMapping("/OnlineLibrary/UserActivity")
public class UserActivityController {
	private Logger logger=LoggerFactory.getLogger(UserActivityController.class);
	@Autowired
	private UserActivityWebService userActivityServ;
	
	@GetMapping("/viewActivity/{userId}")
	public ResponseEntity<ApiResponse> viewAllActivity(@PathVariable("userId") String userId){
		List<UserActivity> userActivity = this.userActivityServ.findAllUserActivity(userId);
		logger.info("User Activity :"+userActivity);
		if(userActivity != null || !userActivity.isEmpty()) {
			ApiResponse response = ApiResponse.builder().data(userActivity).message("Success").status(HttpStatus.OK).build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().data("No Activity Found for the user").message("Failure").status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		    return new ResponseEntity<ApiResponse>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
