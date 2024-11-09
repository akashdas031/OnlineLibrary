package UserService.Entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import UserService.Enums.AccountStatus;
import UserService.Enums.MembershipType;
import UserService.Enums.Permission;
import UserService.Enums.Role;
import UserService.Enums.SubscriptionStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookUser {

	@Id
	private String id;
	@NotBlank(message = "Username must not be empty...")
	@Size(max = 15,min = 5,message = "username must be between 5 to 15 characters...")
	private String username;
	@NotBlank(message = "To create a user You must add a password for it...")
	@Size(min = 6,message = "Password must contain 6 characters or more...")
	private String password;
	@NotBlank(message="email is necessary to create a account...")
	private String email;
	private String fullName;
	private LocalDateTime dateOfBirth;
	@NotBlank(message = "Phone number should not be empty...")
	private String phoneNumber;
	@ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();
	@Enumerated(EnumType.STRING)
	private AccountStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime lastLogin;
	private String profilePicture;
	@Embedded
	private Address address;
	@ElementCollection
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="user_permissions",joinColumns = @JoinColumn(name="user_id"))
	@Column(name="permission")
	private Set<Permission> permissions=new HashSet<>();
	private boolean isEmailVerified;
	private boolean isPhoneNumberVerified;
	private String verificationToken;
	private String phoneVerificationCode;
	private Integer failedLoginAttempts;
	private LocalDateTime lockedUntill;
	@Enumerated(EnumType.STRING)
	private MembershipType membershipType;
	private LocalDateTime subscriptionStart;
	private LocalDateTime subscriptionEnd;
	private SubscriptionStatus subscriptionStatus;
	private LocalDateTime lastActivityAt;
	@ElementCollection
	private Set<String> bookmarkedBooks;
	@ElementCollection
	private List<String> recentlyViewedBooks;
	private boolean notificationEnabled=true;
	@ElementCollection
	private Set<String> preferredGernes;
	private String language="EN";
	
	public void addRole(Role role) {
        roles.add(role);
        permissions.addAll(role.getPermissions());  // Automatically add permissions from the role
    }

    public void removeRole(Role role) {
        roles.remove(role);
        permissions.removeAll(role.getPermissions());  // Automatically remove permissions from the role
    }
	
}
