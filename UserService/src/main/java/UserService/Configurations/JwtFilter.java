package UserService.Configurations;


import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import UserService.Services.BlackListedTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{

	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;
	private BlackListedTokenService blackListedTokenService;
	public JwtFilter(JwtUtil jwtUtil,UserDetailsService userDetailsService,BlackListedTokenService blackListedTokenService){
		this.jwtUtil=jwtUtil;
		this.userDetailsService=userDetailsService;
		this.blackListedTokenService=blackListedTokenService;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authorizationHeader=request.getHeader("Authorization");
		String email=null;
		String jwtToken=null;
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwtToken=authorizationHeader.substring(7);
			if(this.blackListedTokenService.isBlackListed(jwtToken)) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token is Expired");
				return;
			}
			email=jwtUtil.ExtractEmail(jwtToken);
		}
		
		if(email !=null && SecurityContextHolder.getContext().getAuthentication()==null) {
			UserDetails userDetails=userDetailsService.loadUserByUsername(email);
			if(jwtUtil.validateToken(jwtToken, userDetails)) {
				var authentication=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
		
	}

	
}
