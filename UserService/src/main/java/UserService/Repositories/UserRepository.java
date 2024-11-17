package UserService.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import UserService.Entities.BookUser;

public interface UserRepository extends JpaRepository<BookUser, String>{

    // Find user by username
    Optional<BookUser> findByUsername(String username);

    // Find user by email
    Optional<BookUser> findByEmail(String email);

    // Find user by email verification token (check if token is not expired)
    @Query("SELECT u FROM BookUser u WHERE u.verificationToken = :verificationToken AND u.emailVerificationTokenExpirationTime > CURRENT_TIMESTAMP")
    Optional<BookUser> findByVerificationToken(@Param("verificationToken") String verificationToken);

    // Find user by phone number verification code (check if code is not expired)
    @Query("SELECT u FROM BookUser u WHERE u.phoneVerificationCode = :phoneVerificationCode AND u.phoneVerificationCodeExpirationTime > CURRENT_TIMESTAMP AND u.phoneNumber = :phoneNumber")
    Optional<BookUser> findByPhoneVerificationCode(@Param("phoneVerificationCode") String phoneVerificationCode, @Param("phoneNumber") String phoneNumber);

    // Find user by phone number verification code regardless of expiration
    @Query("SELECT u FROM BookUser u WHERE u.phoneVerificationCode = :phoneVerificationCode AND u.phoneNumber = :phoneNumber")
    Optional<BookUser> findByPhoneVerificationCodeWithoutExpiration(@Param("phoneVerificationCode") String phoneVerificationCode, @Param("phoneNumber") String phoneNumber);
}
