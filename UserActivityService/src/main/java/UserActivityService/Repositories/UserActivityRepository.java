package UserActivityService.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import UserActivityService.Entities.UserActivity;

public interface UserActivityRepository extends JpaRepository<UserActivity, String>{

	 @Query("SELECT ua FROM UserActivity ua WHERE ua.userId = :userId ORDER BY ua.timeStamp DESC")
	    List<UserActivity> findAllActivitiesOrderedByTimestampDesc(@Param("userId") String userId);
}
