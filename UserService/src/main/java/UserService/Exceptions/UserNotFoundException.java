package UserService.Exceptions;

public class UserNotFoundException extends RuntimeException{

	public UserNotFoundException(){
		super("User you are looking for is not available on the server...");
	}
	public UserNotFoundException(String message){
		super(message);
	}
}