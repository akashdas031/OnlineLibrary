package BookService.Repositories;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import BookService.Entities.Book;
import BookService.Enums.BookType;
import BookService.Enums.GENRE;

public interface BookRepository extends JpaRepository<Book, String>{

	@Query("SELECT b FROM Book b WHERE (:id is NULL OR b.id = :id) AND (:bookName IS NULL OR b.bookName= :bookName)"
			+ "AND (:authorName IS NULL OR b.authorName LIKE %:authorName%) AND "
			+ "(:bookType IS NULL OR b.bookType= :bookType) AND "
			+ "(:genre IS NULL OR b.genre= :genre)")
	List<Book> searchBooks(@Param("id") String id,
			@Param("authorName") String authorName,
			@Param("bookType") BookType bookType,
			@Param("genre") GENRE genre,
			@Param("bookName") String bookName);
	Page<Book> findByBookType(BookType bookType,Pageable pageable);
}
