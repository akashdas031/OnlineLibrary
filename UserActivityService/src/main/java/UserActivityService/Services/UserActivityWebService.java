package UserActivityService.Services;

import java.util.List;

import UserActivityService.Entities.UserActivity;


public interface UserActivityWebService {

	List<UserActivity> findAllUserActivity(String userId);
}
