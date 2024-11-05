package BookService.Validator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import BookService.Annotations.PDFfile;
import BookService.Exceptions.BookPdfEmptyException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PDFFileValidator implements ConstraintValidator<PDFfile, MultipartFile>{

	 @Override
	    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
	        if (file == null || file.isEmpty()) {
				throw new BookPdfEmptyException("To Add a Book You Must Choose a Book File in PDF format...");
	             // This can be handled by @NotNull as well
	        }

	        // Check for PDF content type and file extension
	        String contentType = file.getContentType();
	        String originalFilename = file.getOriginalFilename();

	        boolean isValidPdf = "application/pdf".equals(contentType) ||
	                             "application/x-pdf".equals(contentType) ||
	                             (originalFilename != null && originalFilename.endsWith(".pdf"));

	        if (!isValidPdf) {
	           throw new BookPdfEmptyException("Please upload a Book file with PDF format...!!!");
	        }

	        return isValidPdf;
	    }
	 

}
