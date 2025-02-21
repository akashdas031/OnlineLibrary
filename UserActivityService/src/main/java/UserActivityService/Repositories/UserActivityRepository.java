package UserActivityService.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import UserActivityService.Entities.UserActivity;

public interface UserActivityRepository extends JpaRepository<UserActivity, String>{

}
