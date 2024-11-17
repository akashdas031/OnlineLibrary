package UserService.Controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import UserService.DTOS.BookUserDTO;
import UserService.DTOS.LoginRequestDto;
import UserService.Entities.BookUser;
import UserService.Exceptions.UserNotFoundException;
import UserService.Responses.ApiResponse;
import UserService.Services.UserService;
import UserService.ValidationRequests.ImageValidationRequest;
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

    @PostMapping(value = "/createUser", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestPart("user") BookUser user, @Valid @ModelAttribute ImageValidationRequest profilePicture) throws IOException {
        BookUser createdUser = this.userServ.createUser(user, profilePicture);
        if (createdUser != null) {
            ApiResponse response = ApiResponse.builder().message("Success").status(201).data(createdUser).build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            ApiResponse error = ApiResponse.builder().message("Something Went wrong!!!").status(500).data("User Creation Failed").build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint for email verification
    @GetMapping("/verify/{token}")
    public ResponseEntity<ApiResponse> processUserVerification(@PathVariable("token") String token) {
        BookUser verifiedUser = this.userServ.findUserByVerificationToken(token);
        if (verifiedUser != null) {
            ApiResponse response = ApiResponse.builder().message("Success").status(200).data(verifiedUser).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse error = ApiResponse.builder().data("Please try with a valid token").message("Token Validation Failed").status(404).build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint for phone number verification
    @GetMapping("/verifyPhone/{verificationCode}")
    public ResponseEntity<ApiResponse> processPhoneNumberVerification(@RequestParam("phoneNumber") String phoneNumber, @PathVariable("verificationCode") String verificationCode) {
        BookUser user = this.userServ.verifyPhoneNumber(verificationCode, phoneNumber);
        if (user != null) {
            ApiResponse response = ApiResponse.builder().message("Success").status(200).data(user).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse error = ApiResponse.builder().message("Failure").status(406).data("Invalid Phone number or verification code").build();
            return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // Resend verification email
    @PostMapping("/resendEmailVerification/{email}")
    public ResponseEntity<ApiResponse> resendEmailVerification(@PathVariable("email") String email) {
        try {
            BookUser user = this.userServ.resendVerificationEmail(email);
            ApiResponse response = ApiResponse.builder().message("Success").status(200).data(user).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            ApiResponse error = ApiResponse.builder().message("Failure").status(404).data("User not found with the given email").build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    // Resend phone verification code
    @PostMapping("/resendPhoneVerification/{phoneNumber}")
    public ResponseEntity<ApiResponse> resendPhoneVerification(@PathVariable("phoneNumber") String phoneNumber) {
        try {
            BookUser user = this.userServ.resendPhoneVerificationCode(phoneNumber);
            ApiResponse response = ApiResponse.builder().message("Success").status(200).data(user).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            ApiResponse error = ApiResponse.builder().message("Failure").status(404).data("User not found with the given phone number").build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    // Get all users
    @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponse> findAllUsers() {
        List<BookUserDTO> allUsers = this.userServ.findAllUsers();
        if (allUsers != null) {
            ApiResponse response = ApiResponse.builder().status(200).message("Success").data(allUsers).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse error = ApiResponse.builder().status(404).message("Failure").data(allUsers).build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    // Update user details
    @PatchMapping(value = "/updateUser/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestPart("user") BookUserDTO user, @Valid @ModelAttribute ImageValidationRequest profilePicture, @PathVariable("userId") String userId) throws IOException {
        BookUserDTO updatedUser = this.userServ.updateUser(user, userId, profilePicture);
        if (updatedUser != null) {
            ApiResponse response = ApiResponse.builder().message("Success").status(201).data(updatedUser).build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            ApiResponse error = ApiResponse.builder().message("Something went wrong!!!").status(500).data("User Update Failed").build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Find user by user id
    @GetMapping("/getSingleUser/{userId}")
    public ResponseEntity<ApiResponse> findUserByUserId(@PathVariable("userId") String userId) {
        BookUserDTO user = this.userServ.findUserByUserId(userId);
        if (user != null) {
            ApiResponse response = ApiResponse.builder().message("Success").data(user).status(200).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse error = ApiResponse.builder().message("Failure").status(404).data("Invalid User ID...User with the given ID does not exist...").build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    // Delete user by user id
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

    // Deactivate user account by user id
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

    // User login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@Valid @RequestBody LoginRequestDto loginCredentials) {
        BookUser loginUser = this.userServ.LoginUser(loginCredentials);
        if (loginUser != null) {
            ApiResponse response = ApiResponse.builder().status(200).message("Success").data(loginUser).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse error = ApiResponse.builder().status(404).message("Failure").data(loginUser).build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
}
