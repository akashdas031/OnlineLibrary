package UserService.Controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import UserService.Configurations.JwtUtil;
import UserService.DTOs.LoginRequestDto;
import UserService.DTOs.UserActivityDTO;
import UserService.DTOs.UserDTO;
import UserService.Entities.UserEntity;
import UserService.Exceptions.UserNotFoundException;
import UserService.RabbitMQConfigurations.UserActivityProducer;
import UserService.Repositories.BlackListedTokenRepository;
import UserService.Repositories.UserRepository;
import UserService.Responses.ApiResponse;
import UserService.Responses.LoginResponse;
import UserService.Services.UserService;
import UserService.ValidationRequests.ImageValidationRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/userService/api/v2/")
@Validated
public class UserController {

	private Logger logger=LoggerFactory.getLogger(UserController.class);
    private UserService userServ;
    private BlackListedTokenRepository blackRepository;
    private UserActivityProducer userActivityProducer;
    private JwtUtil jwtUtil;
    private UserRepository userRepo;
    @Autowired
    public UserController(UserService userServ,BlackListedTokenRepository blackRepository,UserActivityProducer userActivityProducer,JwtUtil jwtUtil,UserRepository userRepo) {
    	
        this.userServ = userServ;
        this.blackRepository=blackRepository;
        this.userActivityProducer=userActivityProducer;
        this.jwtUtil=jwtUtil;
        this.userRepo=userRepo;
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
		String userActivityId = UUID.randomUUID().toString().substring(10).replaceAll("-", "").trim();
		UserActivityDTO activity = UserActivityDTO.builder().userActivityId(userActivityId).userId(user.getUserEntity().getId()).action("LOGIN").details("User Logged in To the Application..")
								 .timeStamp(LocalDateTime.now()).ipAddress("127.0.0.0").build();
		this.userActivityProducer.sendActivity(activity);
		return new ResponseEntity<LoginResponse>(user,HttpStatus.OK);
	}

    //get all users 
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponse> findAllUsers(@RequestHeader("Authorization") String token) {
    	 token=token.substring(7);
        String email = this.jwtUtil.ExtractEmail(token);
        logger.info("Email :"+email);
        UserEntity user=this.userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("Invalid Token...."));
        List<UserDTO> allUsers = this.userServ.findAllUsers();
        if (allUsers != null) {
        	String userActivityId = UUID.randomUUID().toString().substring(10).replaceAll("-", "").trim();
    		UserActivityDTO activity = UserActivityDTO.builder().userActivityId(userActivityId).userId(user.getId()).action("FETCH_ALL_USER_DETAILS").details("User Fetch the api to view all user details..")
    								 .timeStamp(LocalDateTime.now()).ipAddress("127.0.0.0").build();
    		this.userActivityProducer.sendActivity(activity);
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
    public ResponseEntity<ApiResponse> updateUser(@RequestHeader("Authorization") String token,@Valid @RequestPart("user") UserDTO user, @Valid @ModelAttribute ImageValidationRequest profilePicture, @PathVariable("userId") String userId) throws IOException {
        UserDTO updatedUser = this.userServ.updateUser(user, userId, profilePicture);
        if (updatedUser != null) {
        	token=token.substring(7);
            String email = this.jwtUtil.ExtractEmail(token);
            logger.info("Email :"+email);
            UserEntity userFromToken=this.userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("Invalid Token...."));
        	String userActivityId = UUID.randomUUID().toString().substring(10).replaceAll("-", "").trim();
    		UserActivityDTO activity = UserActivityDTO.builder().userActivityId(userActivityId).userId(userFromToken.getId()).action("UPDATE_ACCOUNT").details("User Update the profile...")
    								 .timeStamp(LocalDateTime.now()).ipAddress("127.0.0.0").build();
    		this.userActivityProducer.sendActivity(activity);
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
        	String userActivityId = UUID.randomUUID().toString().substring(10).replaceAll("-", "").trim();
    		UserActivityDTO activity = UserActivityDTO.builder().userActivityId(userActivityId).userId(user.getId()).action("PROFILE_VIEW").details("User Visit his profile...")
    								 .timeStamp(LocalDateTime.now()).ipAddress("127.0.0.0").build();
    		this.userActivityProducer.sendActivity(activity);
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
    public ResponseEntity<ApiResponse> deleteUserByUserId(@RequestHeader("Authorization") String token, @PathVariable("userId") String userId) throws IOException {
        try {
        	token=token.substring(7);
            String email = this.jwtUtil.ExtractEmail(token);
            logger.info("Email :"+email);
            UserEntity user=this.userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("Invalid Token...."));
        	String userActivityId = UUID.randomUUID().toString().substring(10).replaceAll("-", "").trim();
    		UserActivityDTO activity = UserActivityDTO.builder().userActivityId(userActivityId).userId(user.getId()).action("DELETE_ACCOUNT").details("User Delete a account if Id : "+userId+" from the Application..")
    								 .timeStamp(LocalDateTime.now()).ipAddress("127.0.0.0").build();
    		this.userActivityProducer.sendActivity(activity);
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
    public ResponseEntity<ApiResponse> deactivateAccount(@RequestHeader("Authorization") String token ,@PathVariable("userId") String userId) {
        boolean isActive = this.userServ.deactivateAccount(userId);
        if (isActive) {
        	token=token.substring(7);
            String email = this.jwtUtil.ExtractEmail(token);
            logger.info("Email :"+email);
            UserEntity user=this.userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("Invalid Token...."));
        	String userActivityId = UUID.randomUUID().toString().substring(10).replaceAll("-", "").trim();
    		UserActivityDTO activity = UserActivityDTO.builder().userActivityId(userActivityId).userId(user.getId()).action("DEACTIVATE_ACCOUNT").details("User Deactivate a account with id"+userId+" from the Application..")
    								 .timeStamp(LocalDateTime.now()).ipAddress("127.0.0.0").build();
    		this.userActivityProducer.sendActivity(activity);
    		
            ApiResponse response = ApiResponse.builder().status(200).message("Success").data("User deactivated successfully...").build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse error = ApiResponse.builder().status(500).message("Failure").data("Something went wrong...").build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //logout user
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader(name="Authorization",required = false) String authHeader){
    	if(authHeader==null || !authHeader.startsWith("Bearer ")) {
    		ApiResponse error = ApiResponse.builder().message("Invalid Token").status(403).data(authHeader).build();
    		return new ResponseEntity<ApiResponse>(error,HttpStatus.BAD_REQUEST);
    	}
    	String token=authHeader.substring(7);
        String email = this.jwtUtil.ExtractEmail(token);
        logger.info("Email :"+email);
        UserEntity user=this.userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("Invalid Token...."));
    	String userActivityId = UUID.randomUUID().toString().substring(10).replaceAll("-", "").trim();
		UserActivityDTO activity = UserActivityDTO.builder().userActivityId(userActivityId).userId(user.getId()).action("LOGOUT").details("User Logged out from the Application..")
								 .timeStamp(LocalDateTime.now()).ipAddress("127.0.0.0").build();
		
    	if(this.blackRepository.existsByToken(token)) {
    		ApiResponse error = ApiResponse.builder().message("Token has been Expired...").status(403).data("Login Again to perform this activity...").build();
    		return new ResponseEntity<ApiResponse>(error,HttpStatus.BAD_REQUEST);
    	}
    	this.userServ.logoutUser(token);
    	this.userActivityProducer.sendActivity(activity);
    	//send user activity
    	ApiResponse response = ApiResponse.builder().message("Logout Successful!!!").status(200).data("You have been Loggedout succesfully!!! Visit again and gain knowledge!!!").build();
        return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
    }

}
