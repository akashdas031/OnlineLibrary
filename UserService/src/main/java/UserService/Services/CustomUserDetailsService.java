package UserService.Services;



import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import UserService.Entities.UserEntity;
import UserService.Exceptions.UserNotFoundException;
import UserService.Repositories.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService{

	private final UserRepository userRepo;
	public CustomUserDetailsService(UserRepository userRepo){
		this.userRepo=userRepo;
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity user = userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("No user Exist for the email"+email));
		String role="ROLE_"+user.getRole().name();
		return org.springframework.security.core.userdetails.User.withUsername(email)
				                                                  .password(user.getPassword())
		                                                          .authorities(role).build();
	}

}
