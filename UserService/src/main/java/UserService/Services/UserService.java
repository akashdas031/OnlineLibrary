package UserService.Services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import UserService.DTOS.BookUserDTO;
import UserService.Entities.BookUser;
import UserService.ValidationRequests.ImageValidationRequest;
import jakarta.validation.Valid;

public interface UserService {

	BookUser createUser(BookUser bookUser,@Valid ImageValidationRequest profilePicture) throws IOException;
	List<BookUserDTO> findAllUsers();
	BookUser findUserByVerificationToken(String token);
	BookUser verifyPhoneNumber(String verificationCode,String phoneNumber);
	BookUserDTO updateUser(BookUserDTO bookUser,String userId,ImageValidationRequest profilePicture)throws IOException;
	BookUserDTO findUserByUserId(String userId);
	void deleteUserByUserId(String userId) throws IOException;
	boolean deactivateAccount(String userId);
}
