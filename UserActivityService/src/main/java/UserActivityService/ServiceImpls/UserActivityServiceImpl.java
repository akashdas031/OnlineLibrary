package UserActivityService.ServiceImpls;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import UserActivityService.Entities.UserActivity;
import UserActivityService.Repositories.UserActivityRepository;
import UserActivityService.Services.UserActivityWebService;

@Service
public class UserActivityServiceImpl implements UserActivityWebService{
	
	private Logger logger=LoggerFactory.getLogger(UserActivityWebService.class);
	
	@Autowired
	private UserActivityRepository userActivityRepo;

	@Override
	public List<UserActivity> findAllUserActivity(String userId) {
		List<UserActivity> userActivity = this.userActivityRepo.findAllActivitiesOrderedByTimestampDesc(userId);
		logger.info("User Activity Service :"+userActivity);
		return userActivity;
	}

}
