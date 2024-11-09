package UserService.Exceptions;

public class UserNotFoundException extends RuntimeException{

	public UserNotFoundException() {
		super("User You are searching for is not available on the server");
	}
	public UserNotFoundException(String message) {
		super(message);
	}
}
