package UserService.Exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidFileFormatException.class)
	public ResponseEntity<Map<String,String>> handlerInvalidFileFormatException(InvalidFileFormatException ex){
		String message=ex.getMessage();
		Map<String,String> map=new HashMap<>();
		map.put("message", message);
		map.put("Status", "Failure");
		return new ResponseEntity<Map<String,String>>(map,HttpStatus.NOT_FOUND);
	}
	//user not found Exception
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Map<String,String>> handlerUserNotFoundException(UserNotFoundException ex){
		String message=ex.getMessage();
		Map<String,String> map=new HashMap<>();
		map.put("message", message);
		map.put("status", "Failure");
		return new ResponseEntity<Map<String,String>>(map,HttpStatus.NOT_FOUND);
	}
	//Profile Picture Not Found Exception
	public ResponseEntity<Map<String,String>> handlerProfilePictureNotFoundException(ProfilePictureNotFoundException ex){
		String message=ex.getMessage();
		Map<String,String> map=new HashMap<>();
		map.put("message", message);
		map.put("status", "Failure");
		return new ResponseEntity<Map<String,String>>(map,HttpStatus.NOT_FOUND);
	}
}
