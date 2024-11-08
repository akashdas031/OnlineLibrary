package UserService.Validators;

import org.springframework.web.multipart.MultipartFile;


import UserService.Annotations.ImageTypeValidator;
import UserService.Exceptions.InvalidFileFormatException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<ImageTypeValidator, MultipartFile>{

	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
		if(file==null || file.isEmpty()) {
			throw new InvalidFileFormatException( "Please Enter the image of type JPG,PNG,JFIF,JPEG");
		}
		
		String contentType=file.getContentType();
		String fileName=file.getOriginalFilename();
		boolean isValidImage = "image/jpeg".equals(contentType) || 
                "image/png".equals(contentType) ||
                (fileName != null && 
                (fileName.endsWith(".jpg") || 
                 fileName.endsWith(".jpeg") || 
                 fileName.endsWith(".png")));
		if(!isValidImage) {
        	throw new InvalidFileFormatException("Image With JPG,JPEG and PNG format are allowed...");
        }
		return isValidImage;
	}

}
