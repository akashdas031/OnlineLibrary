package UserService.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import UserService.Entities.BookUser;

public interface UserRepository extends JpaRepository<BookUser, String>{

	Optional<BookUser> findByUsername(String username);
	Optional<BookUser> findByEmail(String email);
	Optional<BookUser> findByVerificationToken(String verificationToken);
	@Query("SELECT u FROM BookUser u WHERE u.phoneVerificationCode = :phoneVerificationCode AND u.phoneNumber = :phoneNumber")
	Optional<BookUser> findByPhoneVerificationCode(@Param("phoneVerificationCode") String phoneVerificationCode,@Param("phoneNumber") String phoneNumber);
}
