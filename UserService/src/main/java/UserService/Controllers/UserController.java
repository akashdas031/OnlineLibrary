package UserService.Controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import UserService.DTOs.LoginRequestDto;
import UserService.DTOs.UserDTO;
import UserService.Entities.UserEntity;
import UserService.Exceptions.UserNotFoundException;
import UserService.Responses.ApiResponse;
import UserService.Responses.LoginResponse;
import UserService.Services.UserService;
import UserService.ValidationRequests.ImageValidationRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/userService/api/v2/")
@Validated
public class UserController {


    private UserService userServ;
    
    @Autowired
    public UserController(UserService userServ) {
    	
        this.userServ = userServ;
        
    }

    @PreAuthorize("permitAll()")
    @PostMapping(value = "/createUser", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestPart("user") UserEntity user, @Valid @ModelAttribute ImageValidationRequest profilePicture) throws IOException {
        UserEntity createdUser = this.userServ.createUser(user, profilePicture);
        if (createdUser != null) {
            ApiResponse response = ApiResponse.builder().message("Success").status(201).data(createdUser).build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            ApiResponse error = ApiResponse.builder().message("Something Went wrong!!!").status(500).data("User Creation Failed").build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PreAuthorize("permitAll()")
    @GetMapping("/verify/{token}")
    public ResponseEntity<ApiResponse> processUserVerification(@PathVariable("token") String token) {
        UserEntity verifiedUser = this.userServ.findUserByVerificationToken(token);
        if (verifiedUser != null) {
            ApiResponse response = ApiResponse.builder().message("Success").status(200).data(verifiedUser).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse error = ApiResponse.builder().data("Please try with a valid token").message("Token Validation Failed").status(404).build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
    //resend verification mail 
    @PreAuthorize("permitAll()")
    @PostMapping("/resendEmailVerification/{email}")
    public ResponseEntity<ApiResponse> resendEmailVerification(@PathVariable("email") String email) {
        try {
            UserEntity user = this.userServ.resendVerificationEmail(email);
            ApiResponse response = ApiResponse.builder().message("Success").status(200).data(user).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            ApiResponse error = ApiResponse.builder().message("Failure").status(404).data("User not found with the given email").build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    //login user api
    @PreAuthorize("permitAll()")
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequestDto credentails){
		LoginResponse user = this.userServ.LoginUser(credentails);
		return new ResponseEntity<LoginResponse>(user,HttpStatus.OK);
	}

    //get all users 
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponse> findAllUsers() {
        List<UserDTO> allUsers = this.userServ.findAllUsers();
        if (allUsers != null) {
            ApiResponse response = ApiResponse.builder().status(200).message("Success").data(allUsers).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse error = ApiResponse.builder().status(404).message("Failure").data(allUsers).build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
    //Update User Details api
     @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LIBRARIAN','ROLE_USER')")
    @PatchMapping(value = "/updateUser/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestPart("user") UserDTO user, @Valid @ModelAttribute ImageValidationRequest profilePicture, @PathVariable("userId") String userId) throws IOException {
        UserDTO updatedUser = this.userServ.updateUser(user, userId, profilePicture);
        if (updatedUser != null) {
            ApiResponse response = ApiResponse.builder().message("Success").status(201).data(updatedUser).build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            ApiResponse error = ApiResponse.builder().message("Something went wrong!!!").status(500).data("User Update Failed").build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //get single user with user Id 
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LIBRARIAN') or @userSecurity.isCurrentUserId(authentication, #userId)")
    @GetMapping("/getSingleUser/{userId}")
    public ResponseEntity<ApiResponse> findUserByUserId(@PathVariable("userId") String userId) {
        UserDTO user = this.userServ.findUserByUserId(userId);
        if (user != null) {
            ApiResponse response = ApiResponse.builder().message("Success").data(user).status(200).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse error = ApiResponse.builder().message("Failure").status(404).data("Invalid User ID...User with the given ID does not exist...").build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
    //delete user by user id but only admin can be able to perform this action as it will have only Access to the user
    //having role as ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<ApiResponse> deleteUserByUserId(@PathVariable("userId") String userId) throws IOException {
        try {
            this.userServ.deleteUserByUserId(userId);
            ApiResponse response = ApiResponse.builder().message("Success").status(200).data("User removed from the server successfully!!!").build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            ApiResponse error = ApiResponse.builder().message("Failure").status(404).data(ex.getMessage()).build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
    //deactivate user account 
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    @PatchMapping("/deactivateAccount/{userId}")
    public ResponseEntity<ApiResponse> deactivateAccount(@PathVariable("userId") String userId) {
        boolean isActive = this.userServ.deactivateAccount(userId);
        if (isActive) {
            ApiResponse response = ApiResponse.builder().status(200).message("Success").data("User deactivated successfully...").build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse error = ApiResponse.builder().status(500).message("Failure").data("Something went wrong...").build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
