package UserService.DTOs;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityDTO implements Serializable{
	private String userActivityId;
	private String userId;
	private String action;
	private LocalDateTime timeStamp;
	 private String details;
	 private String ipAddress;
}
