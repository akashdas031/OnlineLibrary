package BookService.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import BookService.Entities.Book;

public interface BookRepository extends JpaRepository<Book, String>{

}
