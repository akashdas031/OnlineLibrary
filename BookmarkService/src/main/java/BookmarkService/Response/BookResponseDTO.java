package BookmarkService.Response;

import java.time.LocalDateTime;

import BookmarkService.ClientServiceDTOs.Book;
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
public class BookResponseDTO {

	private String bookmarkId;
	private Object userId;
	private Object book;
	private LocalDateTime bookmarkedTime;
	private String notes;
	private int lastPage;
	private boolean isFavorite;	
}
