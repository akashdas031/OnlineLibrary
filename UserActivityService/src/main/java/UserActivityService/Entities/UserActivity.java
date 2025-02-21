package UserActivityService.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserActivity {

	@Id
	private String userActivityId;
	private String userId;
	private String action;
	private LocalDateTime timeStamp;
	private String details;
    private String ipAddress;
}
