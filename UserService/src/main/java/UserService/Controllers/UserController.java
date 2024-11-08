package UserService.Controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import UserService.Entities.BookUser;
import UserService.Exceptions.InvalidFileFormatException;
import UserService.Responses.ApiResponse;
import UserService.Services.UserService;
import UserService.ValidationRequests.ImageValidationRequest;
import UserService.Validators.ImageValidator;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/onlineLibrary/user/v1")
@Validated
public class UserController {

	private UserService userServ;

	@Autowired
	public UserController(UserService userServ) {
		this.userServ = userServ;
	}
	@PostMapping(value="/createUser",consumes = {"multipart/form-data"})
	
	public ResponseEntity<ApiResponse> createUser(@Valid @RequestPart("user") BookUser user,@Valid @ModelAttribute ImageValidationRequest profilePicture) throws IOException{
		BookUser createdUser = this.userServ.createUser(user, profilePicture);
		
		if(createdUser != null) {
			ApiResponse response = ApiResponse.builder().message("Success").status(201).data(createdUser).build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.CREATED);
		}else {
			ApiResponse error = ApiResponse.builder().message("Something Went wrong!!!").status(500).data("User Creation Failed").build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
