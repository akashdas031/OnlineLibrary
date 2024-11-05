package BookService.ValidationRequests;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import BookService.Annotations.PDFfile;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PDFUploadRequest {

	@PDFfile(message = "Books Can be Uploaded on PDF format only...")
	@NotNull(message="Book Field Should not be empty!!!")
	private MultipartFile file;
	
	public String getOriginalFileName() {
		return file.getOriginalFilename();
	}
	
	public InputStream getInputStream() throws IOException {
		return file.getInputStream();
	}
	
}
