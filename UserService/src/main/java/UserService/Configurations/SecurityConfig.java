package UserService.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import UserService.Services.CustomUserDetailsService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
	private final CustomUserDetailsService customUserDetailsService;
	public SecurityConfig(JwtFilter jwtFilter,CustomUserDetailsService customUserDetailsService) {
		this.jwtFilter=jwtFilter;
		this.customUserDetailsService=customUserDetailsService;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity security) throws Exception {
		return security.getSharedObject(AuthenticationManagerBuilder.class)
				       .userDetailsService(customUserDetailsService)
				       .passwordEncoder(passwordEncoder())
				       .and()
				       .build();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
		security.csrf().disable()
		        .authorizeRequests(auth->auth.requestMatchers("/userService/api/v2/**","/swagger-ui/index.html/","/swagger-ui/*","/v3/api-docs/**").permitAll().anyRequest().authenticated())
		        		
		        .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		 security.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); 
		 return security.build();
	}

}
