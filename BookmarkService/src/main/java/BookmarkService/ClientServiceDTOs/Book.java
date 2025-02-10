package BookmarkService.ClientServiceDTOs;


import java.security.Timestamp;
import java.time.LocalDateTime;

import BookmarkService.ClientServiceDTOs.Enums.BookType;
import BookmarkService.ClientServiceDTOs.Enums.GENRE;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

	
	private String id;
	@NotNull(message="Book Name Should not be Empty...")
	@Size(min=5,max = 200,message="Book Name should be between 5 to 200 character...")
	private String bookName;
	@NotNull(message="Description Should Not be Blank...Add something about your book in short...")
	@Size(max=250,message="Book Description Should not Exceed 250 Characters...")
	private String description;
	@NotNull(message="Please provide the author's name of your book...")
	@Size(min=5,max = 150,message="Author's name Must be between 5 to 150 characters...")
	private String authorName;
	private String bookImage;
	private String bookPdfName;
	private LocalDateTime publicstionTime;
	
	@Enumerated(EnumType.STRING)
	private BookType bookType;
	
	@Enumerated(EnumType.STRING)
	private GENRE genre;
	
}
