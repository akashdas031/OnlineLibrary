package UserService.Controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import UserService.DTOS.BookUserDTO;
import UserService.Entities.BookUser;
import UserService.Exceptions.InvalidFileFormatException;
import UserService.Exceptions.UserNotFoundException;
import UserService.Responses.ApiResponse;
import UserService.Responses.ApiResponse.ApiResponseBuilder;
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
	@GetMapping("/verify/{token}")
	public ResponseEntity<ApiResponse> processUserVerification(@PathVariable("token") String token){
		BookUser verifiedUser = this.userServ.findUserByVerificationToken(token);
		 if(verifiedUser !=null) {
			 ApiResponse response = ApiResponse.builder().message("Success").status(200).data(verifiedUser).build();
			 return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		 }else {
			 ApiResponse error = ApiResponse.builder().data("Please Try with Valid Token").message("Token Validation Failed").status(404).build();
			 return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);
		 }
	}
	
	@GetMapping("/verifyPhone/{verificationCode}")
	public ResponseEntity<ApiResponse> processPhoneNumberVerification(@RequestParam("phoneNumber") String phoneNumber,@PathVariable("verificationCode") String verificationCode){
		BookUser user = this.userServ.verifyPhoneNumber(verificationCode, phoneNumber);
		if(user!=null) {
			ApiResponse response = ApiResponse.builder().message("Success").status(200).data(user).build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().message("Failure").status(406).data("Invalid Phonenumber or verification Code").build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_ACCEPTABLE);
		}
	}
	@GetMapping("/getAllUsers")
	public ResponseEntity<ApiResponse> findAllUsers(){
		List<BookUserDTO> allUsers = this.userServ.findAllUsers();
		if(allUsers != null) {
			ApiResponse response = ApiResponse.builder().status(200).message("Success").data(allUsers).build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().status(404).message("Failure").data(allUsers).build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);
		}
	}
	@PatchMapping(value="/updateUser/{userId}",consumes={"multipart/form-data"})
	public ResponseEntity<ApiResponse> updateUser(@Valid @RequestPart("user") BookUserDTO user,@Valid @ModelAttribute ImageValidationRequest profilePicture,@PathVariable("userId") String userId) throws IOException{
		BookUserDTO updatedUser = this.userServ.updateUser(user,userId, profilePicture);
		if(updatedUser != null) {
			ApiResponse response = ApiResponse.builder().message("Success").status(201).data(updatedUser).build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.CREATED);
		}else {
			ApiResponse error = ApiResponse.builder().message("Something Went wrong!!!").status(500).data("User Creation Failed").build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//find user by user id
	@GetMapping("/getSingleUser/{userId}")
	public ResponseEntity<ApiResponse> findUserByUserId(@PathVariable("userId") String useerId){
		BookUserDTO user = this.userServ.findUserByUserId(useerId);
		if(user!=null) {
			ApiResponse response = ApiResponse.builder().message("Success").data(user).status(200).build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().message("Failure").status(404).data("Invalid User Id...User with given id Does not exist...").build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);
			
		}
	}
	//delete user from the server by user id 
	@DeleteMapping("/deleteUser/{userId}")
	public ResponseEntity<ApiResponse> deleteUserByUserId(@PathVariable("userId") String userId) throws IOException{
		try {
			this.userServ.deleteUserByUserId(userId);
			ApiResponse response = ApiResponse.builder().message("Success").status(200).data("User Removed From the Server Successfully!!!").build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}catch(UserNotFoundException ex) {
			ApiResponse error = ApiResponse.builder().message("Failure").status(404).data(ex.getMessage()).build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.NOT_FOUND);
		}
	}
	//Deactivate USer Account by user Id
	@PatchMapping("/deactivateAccount/{userId}")
	public ResponseEntity<ApiResponse> deactivateAccount(@PathVariable("userId") String userId){
		boolean isActive = this.userServ.deactivateAccount(userId);
		if(isActive) {
			ApiResponse response = ApiResponse.builder().status(200).message("Success").data("User Deactivated Successfully...").build();
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}else {
			ApiResponse error = ApiResponse.builder().status(500).message("Failure").data("Something Went Wrong...").build();
			return new ResponseEntity<ApiResponse>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
