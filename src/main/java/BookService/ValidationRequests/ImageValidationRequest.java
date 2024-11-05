package BookService.ValidationRequests;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import BookService.Annotations.ImageValidator;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImageValidationRequest {

	@ImageValidator(message="Image Should be Of JPG,JPEG and PNG type...")
	@NotNull(message="Image File Should not be Empty...!!!")
	private MultipartFile imageFile;
	
	public String getOriginalFileName() {
		return imageFile.getOriginalFilename();
	}
	
	public InputStream getInputStream() throws IOException {
		return imageFile.getInputStream();
	}
}
