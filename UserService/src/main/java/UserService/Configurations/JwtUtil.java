package UserService.Configurations;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private final Key secret=Keys.secretKeyFor(SignatureAlgorithm.HS512);
	public String generateToken(String email) {
		return Jwts.builder()
				   .setSubject(email)
				   .setIssuedAt(new Date())
				   .setExpiration(new Date(System.currentTimeMillis()+86400000))
				   .signWith(SignatureAlgorithm.HS512,secret)
				   .compact();
	}
	public String ExtractEmail(String token) {
		return extractClaim(token,Claims::getSubject);
	}
	public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
		final Claims claims=extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	private Claims extractAllClaims(String token) {
		// TODO Auto-generated method stub
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
	
	public boolean validateToken(String token,UserDetails userDetails) {
		final String email=ExtractEmail(token);
		return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	private boolean isTokenExpired(String token) {
		
		return extractClaim(token,Claims::getExpiration).before(new Date());
	}
}
