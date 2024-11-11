package UserService.ImplementationClasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import UserService.DTOS.BookUserDTO;
import UserService.Entities.BookUser;
import UserService.Enums.AccountStatus;
import UserService.Enums.MembershipType;
import UserService.Enums.Role;
import UserService.Enums.SubscriptionStatus;
import UserService.Exceptions.InvalidFileFormatException;
import UserService.Exceptions.UserNotFoundException;
import UserService.Helper.VerificationCodeGenerator;
import UserService.Repositories.UserRepository;
import UserService.Responses.ApiResponse;
import UserService.Services.MailService;
import UserService.Services.SMSService;
import UserService.Services.UserService;
import UserService.ValidationRequests.ImageValidationRequest;

@Service
public class UserServiceImpl implements UserService{

	private UserRepository userRepo; 
	private MailService mailServ;
	private SMSService smsServ;
	
	
	public UserServiceImpl(UserRepository userRepo,MailService mailServ,SMSService smsServ) {
		this.userRepo = userRepo;
		this.mailServ=mailServ;
		this.smsServ=smsServ;
	}

	@Override
	public BookUser createUser(BookUser bookUser,ImageValidationRequest userProfile) throws IOException {
		String baseUrl="C:\\Users\\lenovo\\Desktop\\BookInventory\\ProfilePictures";
		String userId=UUID.randomUUID().toString().substring(0,10).replace("-", "");
		bookUser.setId(userId);
		bookUser.setCreatedAt(LocalDateTime.now());
		bookUser.setStatus(AccountStatus.ACTIVE);
		bookUser.setEmailVerified(false);
		bookUser.setPhoneNumberVerified(false);
		String token=UUID.randomUUID().toString().substring(0, 15).replace("\s","");
		bookUser.setVerificationToken(token);
		bookUser.addRole(Role.USER);
		bookUser.setMembershipType(MembershipType.FREE);
		bookUser.setSubscriptionStatus(SubscriptionStatus.NONE);
		if( userProfile==null) {
			throw new InvalidFileFormatException("Profile Picture Should not be Empty...");
		}else {
			Path path = Paths.get(baseUrl,userProfile.getOriginalFileName());
			Files.copy(userProfile.getInputStream(),path);
			bookUser.setProfilePicture(userProfile.getOriginalFileName());
		}
		this.mailServ.sendVerificationMail(bookUser.getEmail(), token);
		String verificationCode=VerificationCodeGenerator.generateVerificationCode();
		bookUser.setPhoneVerificationCode(verificationCode);
		//this.smsServ.sendVerificationCode(bookUser.getPhoneNumber(), verificationCode);
		BookUser user = userRepo.save(bookUser);
		return user;
	}

	@Override
	public List<BookUserDTO> findAllUsers() {
		List<BookUser> allUsers = this.userRepo.findAll();
		List<BookUserDTO> allUserResponse=new ArrayList<>();
		String baseUrl="C:\\Users\\lenovo\\Desktop\\BookInventory\\ProfilePictures";
		
		if(allUsers!=null || !allUsers.isEmpty()) {
			for(BookUser user:allUsers) {
				BookUserDTO userDto = BookUserDTO.builder().id(user.getId())
				                     .username(user.getUsername())
				                     .password(user.getPassword())
				                     .email(user.getEmail())
				                     .fullName(user.getFullName())
				                     .dateOfBirth(user.getDateOfBirth())
				                     .phoneNumber(user.getPhoneNumber())
				                     .isEmailVerified(user.isEmailVerified())
				                     .isPhoneNumberVerified(user.isPhoneNumberVerified())
				                     .roles(user.getRoles())
				                     .status(user.getStatus())
				                     .createdAt(user.getCreatedAt())
				                     .updatedAt(user.getUpdatedAt())
				                     .lastLogin(user.getLastLogin())
				                     .profilePicture(user.getProfilePicture())
				                     .profilePictureDownloadUrl(baseUrl+File.separator+user.getProfilePicture())
				                     .address(user.getAddress())
				                     .permissions(user.getPermissions())
				                     .failedLoginAttempts(user.getFailedLoginAttempts())
				                     .lockedUntill(user.getLockedUntill())
				                     .membershipType(user.getMembershipType())
				                     .subscriptionStatus(user.getSubscriptionStatus())
				                     .subscriptionStart(user.getSubscriptionEnd())
				                     .subscriptionEnd(user.getSubscriptionEnd())
				                     .lastActivityAt(user.getLastActivityAt())
				                     .bookmarkedBooks(user.getBookmarkedBooks())
				                     .recentlyViewedBooks(user.getRecentlyViewedBooks())
				                     .preferredGernes(user.getPreferredGernes())
				                     .language(user.getLanguage()).build();
				allUserResponse.add(userDto);
				                     
			}
			
		}
		return allUserResponse;
	}

	//verify Email address by sending the token
	@Override
	public BookUser findUserByVerificationToken(String token) {
	BookUser user = this.userRepo.findByVerificationToken(token).orElseThrow(()->new UserNotFoundException("Invalid Token...Please Enter a validtoken to verify the user"));
	user.setEmailVerified(true);
	user.setVerificationToken(null);
	
	BookUser verifiedUser = this.userRepo.save(user);
		return verifiedUser;
	}
	
	@Override
	public BookUser verifyPhoneNumber(String verificationCode,String phoneNumber) {
		BookUser verifiedUser = this.userRepo.findByPhoneVerificationCode(verificationCode, phoneNumber).orElseThrow(()->new UserNotFoundException("User with the Phone number is not available..."));
		verifiedUser.setPhoneNumberVerified(true);
		verifiedUser.setPhoneVerificationCode(null);
		BookUser user = this.userRepo.save(verifiedUser);
		return user;
	}

	@Override
	public BookUserDTO updateUser(BookUserDTO bookUser,String userId, ImageValidationRequest profilePicture) throws IOException {
        BookUser ExistingUser=this.userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("Please Enter Valid userId for Updation.."));
        String baseUrl="C:\\Users\\lenovo\\Desktop\\BookInventory\\ProfilePictures";
        if(profilePicture != null) {
        	//String existingProfilePicture=baseUrl+File.separator+ExistingUser.getProfilePicture();
        	Path path = Paths.get(baseUrl,ExistingUser.getProfilePicture());
        	if(Files.exists(path)) {
        		Files.delete(path);
        		ExistingUser.setProfilePicture(profilePicture.getOriginalFileName());
        		Path newImage=Paths.get(baseUrl,profilePicture.getOriginalFileName());
        		Files.copy(profilePicture.getInputStream(),newImage,StandardCopyOption.REPLACE_EXISTING);
        	}else {
        		ExistingUser.setProfilePicture(profilePicture.getOriginalFileName());
        		Path newImagePath=Paths.get(baseUrl,profilePicture.getOriginalFileName());
        		Files.copy(profilePicture.getInputStream(),newImagePath,StandardCopyOption.REPLACE_EXISTING);	
        	}
        }else {
        	//Here We can add a default image instead of that and will be implemented after that
        	throw new InvalidFileFormatException("Profile Picture should not be empty...");
        }
        if(bookUser.getUsername() !=null)ExistingUser.setUsername(bookUser.getUsername());
        if(bookUser.getEmail() !=null)ExistingUser.setEmail(bookUser.getEmail());
        if(bookUser.getFullName() !=null)ExistingUser.setFullName(bookUser.getFullName());
        if(bookUser.getDateOfBirth() != null)ExistingUser.setDateOfBirth(bookUser.getDateOfBirth());
        if(bookUser.getPhoneNumber() != null)ExistingUser.setPhoneNumber(bookUser.getPhoneNumber());
        //Account Status functionality will be updated later after login module completed
        //if user perform some activity which doesn't meet the condition then it will be locked 
        //or some other status will be added after completion of login module
        if(bookUser.getStatus() != null)ExistingUser.setStatus(bookUser.getStatus());
        ExistingUser.setUpdatedAt(LocalDateTime.now());
        //to be implement after login module completed ..based on the logout time it will be updated 
        //like at which time the user was online last
        if(bookUser.getAddress() != null)ExistingUser.setAddress(bookUser.getAddress());
        	BookUser user = this.userRepo.save(ExistingUser);	
        	BookUserDTO userDto =null;
        if(user !=null) {
        	 userDto = BookUserDTO.builder().id(user.getId())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .dateOfBirth(user.getDateOfBirth())
                    .phoneNumber(user.getPhoneNumber())
                    .isEmailVerified(user.isEmailVerified())
                    .isPhoneNumberVerified(user.isPhoneNumberVerified())
                    .roles(user.getRoles())
                    .status(user.getStatus())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .lastLogin(user.getLastLogin())
                    .profilePicture(user.getProfilePicture())
                    .profilePictureDownloadUrl(baseUrl+File.separator+user.getProfilePicture())
                    .address(user.getAddress())
                    .permissions(user.getPermissions())
                    .failedLoginAttempts(user.getFailedLoginAttempts())
                    .lockedUntill(user.getLockedUntill())
                    .membershipType(user.getMembershipType())
                    .subscriptionStatus(user.getSubscriptionStatus())
                    .subscriptionStart(user.getSubscriptionEnd())
                    .subscriptionEnd(user.getSubscriptionEnd())
                    .lastActivityAt(user.getLastActivityAt())
                    .bookmarkedBooks(user.getBookmarkedBooks())
                    .recentlyViewedBooks(user.getRecentlyViewedBooks())
                    .preferredGernes(user.getPreferredGernes())
                    .language(user.getLanguage()).build();
        }           
        
		return userDto;
	}
	

}
