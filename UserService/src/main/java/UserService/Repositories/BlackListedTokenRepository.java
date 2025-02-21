package UserService.Repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import UserService.Entities.BlackListedToken;


public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken, String>{

	Optional<BlackListedToken> findByToken(String token);
	boolean existsByToken(String token);
	void deleteByExpiryDate(LocalDateTime now);
}
