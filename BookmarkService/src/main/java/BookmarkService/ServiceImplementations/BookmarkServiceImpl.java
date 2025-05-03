package BookmarkService.ServiceImplementations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import BookmarkService.ClientServiceDTOs.Book;
import BookmarkService.Entities.BookMarkDetails;
import BookmarkService.Exceptions.BookMarkDetailsNotFoundException;
import BookmarkService.FeignClient.BookmarkServiceClient;
import BookmarkService.FeignClient.UserServiceClient;
import BookmarkService.Repositories.BookmarkRepository;
import BookmarkService.Response.BookResponseDTO;
import BookmarkService.Response.ResponseApi;
import BookmarkService.Services.BookmarkService;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class BookmarkServiceImpl implements BookmarkService{
	
	private BookmarkRepository bookmarkRepo;
	private BookmarkServiceClient client;
	private UserServiceClient userClient;
	
	private Logger logger=LoggerFactory.getLogger(BookmarkServiceImpl.class);
	
	public BookmarkServiceImpl(BookmarkRepository bookmarkRepo,BookmarkServiceClient client,UserServiceClient userClient) {
		this.bookmarkRepo=bookmarkRepo;
		this.client=client;
		this.userClient=userClient;
	}

	@Override
	public BookMarkDetails createBookMark(BookMarkDetails bookmarkDetails) {
		String bookmarkId = UUID.randomUUID().toString().replace("-","").substring(10);
		bookmarkDetails.setBookmarkId(bookmarkId);
		bookmarkDetails.setBookmarkedTime(LocalDateTime.now());
		logger.info("Favorite Status : "+bookmarkDetails.isFavorite());
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
	public List<BookResponseDTO> getAllBookmarksByUserId(String userId) {
			List<BookMarkDetails> user = this.bookmarkRepo.findByUserId(userId);
			List<BookResponseDTO> response=new ArrayList<>();
			for(BookMarkDetails singleUser:user) {
				String bookId=singleUser.getBookId();
				logger.info("Book Id : "+bookId);
				ResponseApi bookById = this.client.getBookById(bookId);
				logger.info("Book from client : "+bookById.getData());
				logger.info("Book from client : "+bookById.getMessage());
				logger.info("Book from client : "+bookById.getStatus());
				BookResponseDTO singleBookmark = BookResponseDTO.builder().bookmarkId(singleUser.getBookmarkId())
				                         .userId(singleUser.getUserId())
				                         .book(bookById)
				                         .bookmarkedTime(singleUser.getBookmarkedTime())
				                         .notes(singleUser.getNotes())
				                         .lastPage(singleUser.getLastPage())
				                         .isFavorite(singleUser.isFavorite()).build();
				response.add(singleBookmark);
			}
			
			return response;
	}

	@Override
	public List<BookResponseDTO> getAllBookmarksByBookId(String bookId) {
		List<BookMarkDetails> bookmark = this.bookmarkRepo.findByBookId(bookId);
		logger.info("Bookmark by book Id : "+bookmark );
	    List<BookResponseDTO> response=new ArrayList<>();	
		for(BookMarkDetails book:bookmark) {
			String userId=book.getUserId();
			logger.info("forloop executed and user id is : "+userId);
			logger.info("token : "+getCurrentUserToken());
			ResponseApi user = this.userClient.getUserByUserId(getCurrentUserToken(),userId);
			logger.info("User from the userservice by Feign : "+user);
			BookResponseDTO singleBookmark = BookResponseDTO.builder().bookmarkId(book.getBookmarkId())
                    .userId(user.getData())
                    .book(book.getBookId())
                    .bookmarkedTime(book.getBookmarkedTime())
                    .notes(book.getNotes())
                    .lastPage(book.getLastPage())
                    .isFavorite(book.isFavorite()).build();
            response.add(singleBookmark);
			                     
		}
		return response;
	}
	private String getCurrentUserToken() {
		ServletRequestAttributes attributes=(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if(attributes != null) {
			HttpServletRequest request=attributes.getRequest();
			return request.getHeader("Authorization");
		}
		return null;
	}

	@Override
	public List<BookMarkDetails> getFavoriteBooks(String userId) {
		List<BookMarkDetails> favoriteBooks = this.bookmarkRepo.findFavoriteBookByUserId(userId);
		logger.info("Favorite Books : "+favoriteBooks);
		return favoriteBooks;
	}
	

}
