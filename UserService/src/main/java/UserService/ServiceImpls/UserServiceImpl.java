package UserService.ServiceImpls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import UserService.Configurations.JwtUtil;
import UserService.DTOs.LoginRequestDto;
import UserService.DTOs.UserDTO;
import UserService.Entities.UserEntity;
import UserService.Enums.AccountStatus;
import UserService.Enums.MembershipType;
import UserService.Enums.Role;
import UserService.Enums.SubscriptionStatus;
import UserService.Exceptions.InvalidFileFormatException;
import UserService.Exceptions.ProfilePictureNotFoundException;
import UserService.Exceptions.UserNotFoundException;
import UserService.Helper.VerificationCodeGenerator;
import UserService.Repositories.UserRepository;
import UserService.Responses.LoginResponse;
import UserService.Services.MailService;
import UserService.Services.UserService;
import UserService.ValidationRequests.ImageValidationRequest;
import jakarta.validation.Valid;

@Service
public class UserServiceImpl implements UserService{

    private String baseUrl = "C:\\Users\\lenovo\\Desktop\\BookInventory\\ProfilePictures";
    private UserRepository userRepo;
    private MailService mailServ;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    public UserServiceImpl(UserRepository userRepo, MailService mailServ,PasswordEncoder passwordEncoder,JwtUtil jwtUtil){
        this.userRepo = userRepo;
        this.mailServ = mailServ;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtil=jwtUtil;
    }


    @Override
    public UserEntity createUser(UserEntity user, @Valid ImageValidationRequest profilePicture) throws IOException {
        String uid=UUID.randomUUID().toString().substring(0,10).replace('-', ' ');
        
        String userId = UUID.randomUUID().toString().substring(0, 10).replace("-", "");
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setPhoneNumberVerified(false);
        String token = UUID.randomUUID().toString().substring(0, 15).replace("\s", "");
        user.setVerificationToken(token);
       // String encryptedPassword=passwordEncoder.encode(bookUser.getPassword());
       // bookUser.setPassword(encryptedPassword);
        //logger.info(passwordEncoder.matches(bookUser.getPassword(), encryptedPassword)+"");
        // Set expiration time for the email verification token (24 hours from now)
        user.setEmailVerificationTokenExpirationTime(LocalDateTime.now().plusHours(24));
        user.setPhoneVerificationCodeExpirationTime(LocalDateTime.now().plusHours(24));

        user.setRole(Role.USER);
        user.setMembershipType(MembershipType.FREE);
        user.setSubscriptionStatus(SubscriptionStatus.NONE);

        if (profilePicture == null) {
            throw new InvalidFileFormatException("Profile Picture Should not be Empty...");
        } else {
            Path path = Paths.get(baseUrl, profilePicture.getOriginalFileName());
            Files.copy(profilePicture.getInputStream(), path);
            user.setProfilePicture(profilePicture.getOriginalFileName());
        }

        this.mailServ.sendVerificationMail(user.getEmail(), token);
        String verificationCode = VerificationCodeGenerator.generateVerificationCode();
        user.setPhoneVerificationCode(verificationCode);

        // Send SMS
        //this.smsServ.sendVerificationCode(bookUser.getPhoneNumber(), verificationCode);

        UserEntity users = userRepo.save(user);
        return users;
    }

    //verify user with token 
    @Override
    public UserEntity findUserByVerificationToken(String token) {
        UserEntity user = this.userRepo.findByVerificationToken(token)
                .orElseThrow(() -> new UserNotFoundException("Invalid Token...Please Enter a valid token to verify the user"));

        // Check if the token has expired
        if (user.getEmailVerificationTokenExpirationTime().isBefore(LocalDateTime.now())) {
            throw new UserNotFoundException("Token has expired. Please request a new verification email.");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        return userRepo.save(user);
    }

    //resend User verification mail 

    @Override
    public UserEntity resendVerificationEmail(String userId) {
        UserEntity user = this.userRepo.findById(userId)
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

   //Login user Service implementation
    @Override
    public LoginResponse LoginUser(LoginRequestDto userCredentials) {
        UserEntity user = userRepo.findByEmail(userCredentials.getEmail()).orElseThrow(()->new UserNotFoundException("User with Given Email Doesn't Exist...Please check the Email and try again"));
		
        if(user.getStatus().equals(AccountStatus.LOCKED)){
             long lockDuration=Duration.between(user.getLockedUntill(), LocalDateTime.now()).getSeconds();
             if(lockDuration<30){
                throw new UserNotFoundException("Your Account Has been locked...Please try again after some time");
             }else{
                user.setStatus(AccountStatus.ACTIVE);
                user.setFailedLoginAttempts(0);
                user.setLockedUntill(null);
                this.userRepo.save(user);
             }
        }
        
        if(!passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())) {
			if(user.getFailedLoginAttempts()>=5) {
                user.setStatus(AccountStatus.LOCKED);
                user.setLockedUntill(LocalDateTime.now());
                this.userRepo.save(user);
                throw new UserNotFoundException("You have Exceed maximum Number of Attempts...");
            }
            int failedAttempt=user.getFailedLoginAttempts()+1;
            user.setFailedLoginAttempts(failedAttempt);
            this.userRepo.save(user);
            throw new UserNotFoundException("Password Doesn't match..."+"You have "+(5-user.getFailedLoginAttempts())+" attempt left.."+" Please...Check Password Again");
		}
        if(user.getFailedLoginAttempts()>=5) {
            throw new UserNotFoundException("Your Account has been Locked...Can Not Log in...Please contact the Admi");
        }else{
            user.setFailedLoginAttempts(0);
            this.userRepo.save(user);
		String token=jwtUtil.generateToken(userCredentials.getEmail());
		return LoginResponse.builder().userEntity(user).token(token).build();
        }
    }


    //find all users 
    @Override
    public List<UserDTO> findAllUsers() {
        List<UserEntity> allUsers = this.userRepo.findAll();
        List<UserDTO> allUserResponse = new ArrayList<>();

        if (allUsers != null || !allUsers.isEmpty()) {
            for (UserEntity user : allUsers) {
                UserDTO userDto = UserDTO.builder().id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .dateOfBirth(user.getDateOfBirth())
                        .phoneNumber(user.getPhoneNumber())
                        .isEmailVerified(user.isEmailVerified())
                        .isPhoneNumberVerified(user.isPhoneNumberVerified())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .lastLogin(user.getLastLogin())
                        .profilePicture(user.getProfilePicture())
                        .profilePictureDownloadUrl(baseUrl + File.separator + user.getProfilePicture())
                        .address(user.getAddress())
                        .failedLoginAttempts(user.getFailedLoginAttempts())
                        .lockedUntill(user.getLockedUntill())
                        .isEmailVerified(user.isEmailVerified())
                        .isPhoneNumberVerified(user.isPhoneNumberVerified())
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
    public UserDTO updateUser(UserDTO bookUser, String userId, ImageValidationRequest profilePicture)
            throws IOException {
        UserEntity existingUser = this.userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("Please Enter Valid userId for Updation.."));
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
        return UserDTO.builder().id(existingUser.getId())
        .username(existingUser.getUsername())
        .email(existingUser.getEmail())
        .fullName(existingUser.getFullName())
        .dateOfBirth(existingUser.getDateOfBirth())
        .phoneNumber(existingUser.getPhoneNumber())
        .isEmailVerified(existingUser.isEmailVerified())
        .isPhoneNumberVerified(existingUser.isPhoneNumberVerified())
        .role(existingUser.getRole())
        .status(existingUser.getStatus())
        .createdAt(existingUser.getCreatedAt())
        .updatedAt(existingUser.getUpdatedAt())
        .lastLogin(existingUser.getLastLogin())
        .profilePicture(existingUser.getProfilePicture())
        .profilePictureDownloadUrl(baseUrl + File.separator + existingUser.getProfilePicture())
        .address(existingUser.getAddress())
        .failedLoginAttempts(existingUser.getFailedLoginAttempts())
        .lockedUntill(existingUser.getLockedUntill())
        .isEmailVerified(existingUser.isEmailVerified())
        .isPhoneNumberVerified(existingUser.isPhoneNumberVerified())
        .membershipType(existingUser.getMembershipType())
        .subscriptionStatus(existingUser.getSubscriptionStatus())
        .subscriptionStart(existingUser.getSubscriptionEnd())
        .subscriptionEnd(existingUser.getSubscriptionEnd())
        .lastActivityAt(existingUser.getLastActivityAt())
        .bookmarkedBooks(existingUser.getBookmarkedBooks())
        .recentlyViewedBooks(existingUser.getRecentlyViewedBooks())
        .preferredGernes(existingUser.getPreferredGernes())
        .language(existingUser.getLanguage()).build();
    }


    @Override
    public UserDTO findUserByUserId(String userId) {
        UserEntity user = this.userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("User with given id is not available on the server..."));
		UserDTO userDto = UserDTO.builder().id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .phoneNumber(user.getPhoneNumber())
                .isEmailVerified(user.isEmailVerified())
                .isPhoneNumberVerified(user.isPhoneNumberVerified())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .profilePicture(user.getProfilePicture())
                .profilePictureDownloadUrl(baseUrl+File.separator+user.getProfilePicture())
                .address(user.getAddress())
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


    //delete user with user id service impl method
    @Override
    public void deleteUserByUserId(String userId) throws IOException {
        if(this.userRepo.existsById(userId)) {
			UserEntity user = this.userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("User Does Not Exist..."));
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
    //Deactivate user account 

    @Override
    public boolean deactivateAccount(String userId) {
        if(this.userRepo.existsById(userId)) {
			UserEntity user = this.userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("User with the id Is not Available on the server"));
			user.setStatus(AccountStatus.INACTIVE);
			UserEntity deactivatedUser = this.userRepo.save(user);
			return deactivatedUser.getStatus()==AccountStatus.INACTIVE;
		}
		return false;
    }


    
    

}
