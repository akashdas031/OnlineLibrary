package BookmarkService.Services;

import java.util.List;

import BookmarkService.Entities.BookMarkDetails;
import BookmarkService.Response.BookResponseDTO;

public interface BookmarkService {

	BookMarkDetails createBookMark(BookMarkDetails bookmarkDetails);
	BookMarkDetails getBookMarkDetailsByBookmarkId(String bookmarkId);
	List<BookMarkDetails> getAllBookmarkDetails();
	List<BookResponseDTO> getAllBookmarksByUserId(String userId);
	List<BookResponseDTO> getAllBookmarksByBookId(String bookId);
	
}
