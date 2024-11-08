package UserService.ImplementationClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import UserService.Entities.BookUser;
import UserService.Enums.AccountStatus;
import UserService.Enums.MembershipType;
import UserService.Enums.Role;
import UserService.Enums.SubscriptionStatus;
import UserService.Exceptions.InvalidFileFormatException;
import UserService.Repositories.UserRepository;
import UserService.Services.UserService;
import UserService.ValidationRequests.ImageValidationRequest;

@Service
public class UserServiceImpl implements UserService{

	private UserRepository userRepo;
	
	
	
	public UserServiceImpl(UserRepository userRepo) {
		this.userRepo = userRepo;
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
		BookUser user = userRepo.save(bookUser);
		return user;
	}

	@Override
	public List<BookUser> findAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

}
