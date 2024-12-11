package UserService.Entities;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import UserService.Enums.AccountStatus;
import UserService.Enums.MembershipType;
import UserService.Enums.Role;
import UserService.Enums.SubscriptionStatus;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "UserDetails")
public class UserEntity {

    @Id
    private String id;
    @NotBlank(message = "Username must not be empty...")
    @Size(max = 15, min = 5, message = "username must be between 5 to 15 characters...")
    private String username;

    @NotBlank(message = "To create a user You must add a password for it...")
    @Size(min = 6, message = "Password must contain 6 characters or more...")
    private String password;

    @NotBlank(message="email is necessary to create an account...")
    private String email;

    private String fullName;
    private LocalDateTime dateOfBirth;

    @NotBlank(message = "Phone number should not be empty...")
    private String phoneNumber;

    
    private Role role;

    
    private AccountStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    private String profilePicture;

    @Embedded
    private Address address;


    private boolean isEmailVerified;
    private boolean isPhoneNumberVerified;

    private String verificationToken;  // Token for email verification
    private String phoneVerificationCode;  // Verification code for phone

    // Expiration times for email and phone verification tokens
    private LocalDateTime emailVerificationTokenExpirationTime;  // Expiration time for email verification token
    private LocalDateTime phoneVerificationCodeExpirationTime;  // Expiration time for phone verification code

    private Integer failedLoginAttempts;
    private LocalDateTime lockedUntill;

    private MembershipType membershipType;

    private LocalDateTime subscriptionStart;
    private LocalDateTime subscriptionEnd;
    private SubscriptionStatus subscriptionStatus;
    private LocalDateTime lastActivityAt;

    @ElementCollection
    private Set<String> bookmarkedBooks;

    @ElementCollection
    private List<String> recentlyViewedBooks;

    private boolean notificationEnabled = true;

    @ElementCollection
    private Set<String> preferredGernes;

    private String language = "EN"; 
}
