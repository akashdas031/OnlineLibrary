package BookmarkService.ServiceImplementations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import BookmarkService.Entities.BookMarkDetails;
import BookmarkService.Exceptions.BookMarkDetailsNotFoundException;
import BookmarkService.Repositories.BookmarkRepository;
import BookmarkService.Services.BookmarkService;

@Service
public class BookmarkServiceImpl implements BookmarkService{
	
	private BookmarkRepository bookmarkRepo;
	
	public BookmarkServiceImpl(BookmarkRepository bookmarkRepo) {
		this.bookmarkRepo=bookmarkRepo;
	}

	@Override
	public BookMarkDetails createBookMark(BookMarkDetails bookmarkDetails) {
		String bookmarkId = UUID.randomUUID().toString().replace("-","").substring(10);
		bookmarkDetails.setBookmarkId(bookmarkId);
		bookmarkDetails.setBookmarkedTime(LocalDateTime.now());
		BookMarkDetails bookmark = this.bookmarkRepo.save(bookmarkDetails);
		return bookmark;
	}

	@Override
	public BookMarkDetails getBookMarkDetailsByBookmarkId(String bookmarkId) {
		BookMarkDetails singleBookmark = this.bookmarkRepo.findById(bookmarkId).orElseThrow(()-> new BookMarkDetailsNotFoundException("Bookmark with given details is not available on the server"));
		return singleBookmark;
	}

	@Override
	public List<BookMarkDetails> getAllBookmarkDetails() {
		List<BookMarkDetails> allBookmarks = this.bookmarkRepo.findAll();
		return allBookmarks;
	}

	@Override
	public List<BookMarkDetails> getAllBookmarksByUserId(String userId) {
		return null;
	}

	@Override
	public List<BookMarkDetails> getAllBookmarksByBookId(String bookId) {
		// TODO Auto-generated method stub
		return null;
	}

}
