package UserService.Exceptions;

public class InvalidFileFormatException extends RuntimeException{

    public InvalidFileFormatException() {
		super("The File Format is Not Valid...Insert a Valid File Format");
	}
	public InvalidFileFormatException(String message) {
		super(message);
	}
}
