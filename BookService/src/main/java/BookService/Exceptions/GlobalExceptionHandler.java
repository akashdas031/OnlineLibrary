package BookService.Exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	 @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
	        Map<String, String> errors = new HashMap<>();
	        ex.getBindingResult().getAllErrors().forEach((error) -> {
	            String fieldName = ((FieldError) error).getField();
	            String errorMessage = error.getDefaultMessage();
	            errors.put(fieldName, errorMessage);
	        });

	        errors.put("Status", "Failure");
	        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	    }
	
	@ExceptionHandler(BookPdfEmptyException.class)
	public ResponseEntity<Map<String,String>> handlerBookPdfEmptyException(BookPdfEmptyException ex){
		String message=ex.getMessage();
		Map<String,String> map=new HashMap<>();
		map.put("message", message);
		map.put("Status", "failure");
		return new ResponseEntity<Map<String,String>>(map,HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(InvalidBookDetailsException.class)
	public ResponseEntity<Map<String,String>> handlerBookFormatException(InvalidBookDetailsException ex){
		String message=ex.getMessage();
		Map<String,String> map=new HashMap<>();
		map.put("message", message);
		map.put("Status", "Failuer");
		return new ResponseEntity<Map<String,String>>(map,HttpStatus.NOT_ACCEPTABLE);
	}
	//Book not found exception
	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<Map<String,String>> handlerBookNotFoundException(BookNotFoundException ex){
		String message=ex.getMessage();
		Map<String,String> map=new HashMap<>();
		map.put("message", message);
		map.put("Status", "Failure");
		return new ResponseEntity<Map<String,String>>(map,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InvalidPageException.class)
	public ResponseEntity<Map<String,String>> handlerInvalidPageException(InvalidPageException ex){
		String message=ex.getMessage();
		Map<String,String> map=new HashMap<>();
		map.put("message", message);
		map.put("Status", "Failure");
		return new ResponseEntity<Map<String,String>>(map,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
