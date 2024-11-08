package UserService.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import UserService.Entities.BookUser;

public interface UserRepository extends JpaRepository<BookUser, String>{

	Optional<BookUser> findByUsername(String username);
	Optional<BookUser> findByEmail(String email); 
}
