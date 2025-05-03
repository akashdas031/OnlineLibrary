package BookmarkService.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import BookmarkService.Entities.BookMarkDetails;


@Repository
public interface BookmarkRepository extends JpaRepository<BookMarkDetails, String>{

	List<BookMarkDetails> findByUserId(String userId);
	List<BookMarkDetails> findByBookId(String bookId);
	@Query("SELECT b FROM BookMarkDetails b WHERE b.userId = :userId AND b.favorite = true")
	List<BookMarkDetails> findFavoriteBookByUserId(@Param("userId") String userId);
}
