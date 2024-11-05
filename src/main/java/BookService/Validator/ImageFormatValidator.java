package BookService.Validator;

import org.springframework.web.multipart.MultipartFile;

import BookService.Annotations.ImageValidator;
import BookService.Exceptions.BookPdfEmptyException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageFormatValidator implements ConstraintValidator<ImageValidator, MultipartFile>{

	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
		if (file == null || file.isEmpty()) {
			throw new BookPdfEmptyException("Choose the Cover Image for your book...");
             // This can be handled by @NotNull as well
        }
		 // Check for Image content type and file extension
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        boolean isValidImage = "image/jpeg".equals(contentType) || 
                "image/png".equals(contentType) || 
                (originalFilename != null && 
                (originalFilename.endsWith(".jpg") || 
                 originalFilename.endsWith(".jpeg") || 
                 originalFilename.endsWith(".png")));
        if(!isValidImage) {
        	throw new BookPdfEmptyException("Image With JPG,JPEG and PNG format are allowed...");
        }
		return isValidImage;
	}

}
