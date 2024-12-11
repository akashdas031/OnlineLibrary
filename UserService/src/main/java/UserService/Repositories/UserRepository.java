package UserService.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import UserService.Entities.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity,String>{


    Optional<UserEntity> findByEmail(String email);

    // Find user by email verification token (check if token is not expired)
    @Query("SELECT u FROM UserEntity u WHERE u.verificationToken = :verificationToken AND u.emailVerificationTokenExpirationTime > CURRENT_TIMESTAMP")
    Optional<UserEntity> findByVerificationToken(@Param("verificationToken") String verificationToken);

}
