package UserService.ValidationRequests;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import UserService.Annotations.ImageTypeValidator;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImageValidationRequest {

    @ImageTypeValidator(message = "Profile picture should be of type JPG,JPEG,JFIF,PNG")
	@NotNull(message = "Profile Picture Should not be Empty...")
	private MultipartFile file;
	
	public String getOriginalFileName() {
		return file.getOriginalFilename();
	}
	public InputStream getInputStream() throws IOException {
		return file.getInputStream();
	}
	
}
