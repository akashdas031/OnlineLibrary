package BookmarkService.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import BookmarkService.ClientServiceDTOs.Book;
import BookmarkService.Response.ResponseApi;

@FeignClient(name="Book-Service",url="localhost:6574/book/v1/")
public interface BookmarkServiceClient {

	@GetMapping("/getSingleBook/{bookId}")
	ResponseApi getBookById(@PathVariable("bookId") String bookId);
}
