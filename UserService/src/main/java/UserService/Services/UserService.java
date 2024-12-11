package UserService.Services;

import java.io.IOException;
import java.util.List;

import UserService.DTOs.LoginRequestDto;
import UserService.DTOs.UserDTO;
import UserService.Entities.UserEntity;
import UserService.Responses.LoginResponse;
import UserService.ValidationRequests.ImageValidationRequest;
import jakarta.validation.Valid;

public interface UserService {

    UserEntity createUser(UserEntity user,@Valid ImageValidationRequest profilePicture) throws IOException;
    UserEntity findUserByVerificationToken(String token);
    UserEntity resendVerificationEmail(String userId);
    LoginResponse LoginUser(LoginRequestDto userCredentials);
    List<UserDTO> findAllUsers();
    //Update User Details
    UserDTO updateUser(UserDTO bookUser,String userId,ImageValidationRequest profilePicture)throws IOException;
    //get single user by user id
    UserDTO findUserByUserId(String userId);
    //delete user by user id service method
    void deleteUserByUserId(String userId) throws IOException;
    //deactivate user account 
    boolean deactivateAccount(String userId);
    //lock user
    //boolean LockUser(String userId);
}
