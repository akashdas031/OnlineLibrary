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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import UserService.DTOS.BookUserDTO;
import UserService.DTOS.LoginRequestDto;
import UserService.Entities.BookUser;
import UserService.Enums.AccountStatus;
import UserService.Enums.MembershipType;
import UserService.Enums.Role;
import UserService.Enums.SubscriptionStatus;
import UserService.Exceptions.InvalidFileFormatException;
import UserService.Exceptions.ProfilePictureNotFoundException;
import UserService.Exceptions.UserNotFoundException;
import UserService.Helper.VerificationCodeGenerator;
import UserService.Repositories.UserRepository;
import UserService.Responses.ApiResponse;
import UserService.Services.MailService;
import UserService.Services.SMSService;
import UserService.Services.UserService;
import UserService.ValidationRequests.ImageValidationRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class UserServiceImpl implements UserService {

    private String baseUrl = "C:\\Users\\lenovo\\Desktop\\BookInventory\\ProfilePictures";
    private UserRepository userRepo;
    private MailService mailServ;
    private SMSService smsServ;
    @PersistenceContext
    private EntityManager entityManager;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepo, MailService mailServ, SMSService smsServ, EntityManager entityManager) {
        this.userRepo = userRepo;
        this.mailServ = mailServ;
        this.smsServ = smsServ;
        this.entityManager = entityManager;
    }

    @Override
    public BookUser createUser(BookUser bookUser, ImageValidationRequest userProfile) throws IOException {
        logger.info(bookUser + " ");
        String userId = UUID.randomUUID().toString().substring(0, 10).replace("-", "");
        bookUser.setId(userId);
        bookUser.setCreatedAt(LocalDateTime.now());
        bookUser.setStatus(AccountStatus.ACTIVE);
        bookUser.setEmailVerified(false);
        bookUser.setPhoneNumberVerified(false);
        String token = UUID.randomUUID().toString().substring(0, 15).replace("\s", "");
        bookUser.setVerificationToken(token);

        // Set expiration time for the email verification token (24 hours from now)
        bookUser.setEmailVerificationTokenExpirationTime(LocalDateTime.now().plusHours(24));
        bookUser.setPhoneVerificationCodeExpirationTime(LocalDateTime.now().plusHours(24));

        bookUser.addRole(Role.USER);
        bookUser.setMembershipType(MembershipType.FREE);
        bookUser.setSubscriptionStatus(SubscriptionStatus.NONE);

        if (userProfile == null) {
            throw new InvalidFileFormatException("Profile Picture Should not be Empty...");
        } else {
            Path path = Paths.get(baseUrl, userProfile.getOriginalFileName());
            Files.copy(userProfile.getInputStream(), path);
            bookUser.setProfilePicture(userProfile.getOriginalFileName());
        }

        this.mailServ.sendVerificationMail(bookUser.getEmail(), token);
        String verificationCode = VerificationCodeGenerator.generateVerificationCode();
        bookUser.setPhoneVerificationCode(verificationCode);

        // Send SMS
        this.smsServ.sendVerificationCode(bookUser.getPhoneNumber(), verificationCode);

        BookUser user = userRepo.save(bookUser);
        return user;
    }

    @Override
    public BookUser resendVerificationEmail(String userId) {
        BookUser user = this.userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with the given ID"));

        // Check if the token has expired
        if (user.getEmailVerificationTokenExpirationTime().isBefore(LocalDateTime.now())) {
            // Token has expired, generate a new token
            String newToken = UUID.randomUUID().toString().substring(0, 15).replace("\s", "");
            user.setVerificationToken(newToken);

            // Set the new expiration time
            user.setEmailVerificationTokenExpirationTime(LocalDateTime.now().plusHours(24));

            // Send new verification email
            this.mailServ.sendVerificationMail(user.getEmail(), newToken);
        }

        return userRepo.save(user);
    }

    @Override
    public BookUser resendPhoneVerificationCode(String userId) {
        BookUser user = this.userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with the given ID"));

        // Check if the phone verification code has expired
        if (user.getPhoneVerificationCodeExpirationTime().isBefore(LocalDateTime.now())) {
            // Code has expired, generate a new verification code
            String newVerificationCode = VerificationCodeGenerator.generateVerificationCode();
            user.setPhoneVerificationCode(newVerificationCode);

            // Set the new expiration time for the phone verification code
            user.setPhoneVerificationCodeExpirationTime(LocalDateTime.now().plusHours(24));

            // Send new verification SMS
            this.smsServ.sendVerificationCode(user.getPhoneNumber(), newVerificationCode);
        }

        return userRepo.save(user);
    }

    @Override
    public BookUser findUserByVerificationToken(String token) {
        BookUser user = this.userRepo.findByVerificationToken(token)
                .orElseThrow(() -> new UserNotFoundException("Invalid Token...Please Enter a valid token to verify the user"));

        // Check if the token has expired
        if (user.getEmailVerificationTokenExpirationTime().isBefore(LocalDateTime.now())) {
            throw new UserNotFoundException("Token has expired. Please request a new verification email.");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        return userRepo.save(user);
    }

    @Override
    public BookUser verifyPhoneNumber(String verificationCode, String phoneNumber) {
        BookUser user = this.userRepo.findByPhoneVerificationCode(verificationCode, phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User with the Phone number is not available..."));

        // Check if the phone verification code has expired
        if (user.getPhoneVerificationCodeExpirationTime().isBefore(LocalDateTime.now())) {
            throw new UserNotFoundException("Verification code has expired. Please request a new one.");
        }

        user.setPhoneNumberVerified(true);
        user.setPhoneVerificationCode(null);
        return userRepo.save(user);
    }

    @Override
    public List<BookUserDTO> findAllUsers() {
        List<BookUser> allUsers = this.userRepo.findAll();
        List<BookUserDTO> allUserResponse = new ArrayList<>();

        if (allUsers != null || !allUsers.isEmpty()) {
            for (BookUser user : allUsers) {
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
                        .profilePictureDownloadUrl(baseUrl + File.separator + user.getProfilePicture())
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

    @Override
    public BookUserDTO updateUser(BookUserDTO bookUser, String userId, ImageValidationRequest profilePicture) throws IOException {
        BookUser existingUser = this.userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("Please Enter Valid userId for Updation.."));
        
        if (profilePicture != null) {
            Path path = Paths.get(baseUrl, existingUser.getProfilePicture());
            if (Files.exists(path)) {
                Files.delete(path);
                existingUser.setProfilePicture(profilePicture.getOriginalFileName());
                Path newImage = Paths.get(baseUrl, profilePicture.getOriginalFileName());
                Files.copy(profilePicture.getInputStream(), newImage, StandardCopyOption.REPLACE_EXISTING);
            } else {
                existingUser.setProfilePicture(profilePicture.getOriginalFileName());
                Path newImagePath = Paths.get(baseUrl, profilePicture.getOriginalFileName());
                Files.copy(profilePicture.getInputStream(), newImagePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            throw new InvalidFileFormatException("Profile Picture should not be empty.");
        }

        existingUser.setEmail(bookUser.getEmail());
        existingUser.setFullName(bookUser.getFullName());
        existingUser.setPhoneNumber(bookUser.getPhoneNumber());
        existingUser.setDateOfBirth(bookUser.getDateOfBirth());
        existingUser.setUpdatedAt(LocalDateTime.now());

        existingUser = userRepo.save(existingUser);
        return BookUserDTO.builder().id(existingUser.getId())
                .email(existingUser.getEmail())
                .fullName(existingUser.getFullName())
                .phoneNumber(existingUser.getPhoneNumber())
                .profilePicture(existingUser.getProfilePicture()).build();
    }


	@Override
	public BookUserDTO findUserByUserId(String userId) {
		BookUser user = this.userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("User with given id is not available on the server..."));
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
		return userDto;
	}
	//delete user by it's id from the server

	@Override
	public void deleteUserByUserId(String userId) throws IOException {
		if(this.userRepo.existsById(userId)) {
			BookUser user = this.userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("User Does Not Exist..."));
			Path profilePicturePath=Paths.get(baseUrl,user.getProfilePicture());
			if(Files.exists(profilePicturePath)) {
				Files.delete(profilePicturePath);
			}else {
				throw new ProfilePictureNotFoundException("Something Went wrong While deleting the Profile picture");
			}
			this.userRepo.deleteById(userId);
		}else {
			throw new UserNotFoundException("User With the ID :" +userId+ " Doesn't Exist on the Server...");
		}
		
	}

	@Override
	public boolean deactivateAccount(String userId) {
		if(this.userRepo.existsById(userId)) {
			BookUser user = this.userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("User with the id Is not Available on the server"));
			user.setStatus(AccountStatus.INACTIVE);
			BookUser deactivatedUser = this.userRepo.save(user);
			return deactivatedUser.getStatus()==AccountStatus.INACTIVE;
		}
		return false;
	}

	@Override
	public boolean LockUser(String userId) {
		//need to be implemented after the login functionality completed
		return false;
	}

	@Override
	public BookUser LoginUser(LoginRequestDto userCredentials) {
		BookUser user = this.userRepo.findByEmail(userCredentials.getEmail()).orElseThrow(()-> new UserNotFoundException("Please Enter valid Email address..."));
		
		logger.info("input password :"+userCredentials.getPassword());
		logger.info("password in db :"+user.getPassword());
		
		if(!user.getPassword().equals(userCredentials.getPassword())) {
			
				if(user.getFailedLoginAttempts()==5) {
					throw new UserNotFoundException("You have Exceed maximum Number of Attempts...");
				}
				int failedAttempt=user.getFailedLoginAttempts()+1;
				user.setFailedLoginAttempts(failedAttempt);
				this.userRepo.save(user);
				throw new UserNotFoundException("Password Doesn't match..."+"You have "+(5-user.getFailedLoginAttempts())+" attempt left.."+" Please...Check Password Again");
			}
		
			 if(user.getFailedLoginAttempts()==5) {
				 throw new UserNotFoundException("You Have Exceed Maximum Number of Attempts...Can Not Log in...");
			 }else {
		return user;
			 
		 }
	}
	

}
