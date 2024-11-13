package UserService.Exceptions;

public class ProfilePictureNotFoundException extends RuntimeException{

	public ProfilePictureNotFoundException() {
		super("Profile Picture Not Found");
	}
	public ProfilePictureNotFoundException(String message) {
		super(message);
	}
}
