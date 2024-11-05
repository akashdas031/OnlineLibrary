package BookService.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookDetailsDTO {

	private String bookId;
	private String bookName;
	private String authorName;
	private String bookDescription;
	private String bookPdfFileName;
	private String bookImageName;
	private String bookPdfDownloadUrl;
	private String bookImageUrl;
}
