package BookmarkService.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder 
@ToString
public class BookMarkDetails {

	@Id
	private String bookmarkId;
	@NotBlank(message = "Username must not be empty...")
    @Size(max = 15, min = 5, message = "username must be between 5 to 15 characters...")
	private String userId;
	@NotBlank(message="Book Id must not be empty")
	private String bookId;
	private LocalDateTime bookmarkedTime;
	private String notes;
	private int lastPage;
	private boolean favorite;	
}
