package UserService.Exceptions;

public class ProfilePictureNotFoundException extends RuntimeException{

    public ProfilePictureNotFoundException(){
        super("Profile picture not found for the user ");
    }
    public ProfilePictureNotFoundException(String message){
        super(message);
    }
}
