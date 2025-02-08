package BookmarkService.Services;

import java.util.List;

import BookmarkService.Entities.BookMarkDetails;

public interface BookmarkService {

	BookMarkDetails createBookMark(BookMarkDetails bookmarkDetails);
	BookMarkDetails getBookMarkDetailsByBookmarkId(String bookmarkId);
	List<BookMarkDetails> getAllBookmarkDetails();
	List<BookMarkDetails> getAllBookmarksByUserId(String userId);
	List<BookMarkDetails> getAllBookmarksByBookId(String bookId);
	
}
