package UserService.DTOS;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import UserService.Entities.Address;
import UserService.Enums.AccountStatus;
import UserService.Enums.MembershipType;
import UserService.Enums.Permission;
import UserService.Enums.Role;
import UserService.Enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookUserDTO {

	private String id;
	private String username;
	private String password;
	private String email;
	private String fullName;
	private LocalDateTime dateOfBirth;
	private String phoneNumber;
    private Set<Role> roles = new HashSet<>();
	private AccountStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime lastLogin;
	private String profilePicture;
	private String profilePictureDownloadUrl;
	private Address address;
	private Set<Permission> permissions;
	private boolean isEmailVerified;
	private boolean isPhoneNumberVerified;
	private String verificationToken;
	private String phoneVerificationCode;
	private Integer failedLoginAttempts;
	private LocalDateTime lockedUntill;
	private MembershipType membershipType;
	private LocalDateTime subscriptionStart;
	private LocalDateTime subscriptionEnd;
	private SubscriptionStatus subscriptionStatus;
	private LocalDateTime lastActivityAt;
	private Set<String> bookmarkedBooks;
	private List<String> recentlyViewedBooks;
	private boolean notificationEnabled=true;
	private Set<String> preferredGernes;
	private String language="EN";
	
	
}
