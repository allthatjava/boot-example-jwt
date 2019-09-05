package brian.example.boot.jwt.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenUtil {
	
	@Value("@{jwt.secret}")
	private String secret;
	
	// Util method to get data out of Token
	private <T> T getClaimsFromToken(String token, Function<Claims, T> claimResolver) {
		final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		return claimResolver.apply(claims);
	}
	
	// Get User name from token
	public String getUsernameFromToken(String token) {
		return getClaimsFromToken(token, Claims::getSubject);
	}
	// Get Expiration from token
	public Date getExpirationDate(String token) {
		return getClaimsFromToken( token, Claims::getExpiration);
	}
	// Check if token expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDate(token);
		return expiration.before(new Date());
	}
	
	
	
	// Generate Token
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		
		return Jwts.builder()
				.setClaims(claims)
				.setSubject( userDetails.getUsername() )
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration( new Date(System.currentTimeMillis() + (5 * 60 * 60 ) * 1000) ) 	// 5 hours
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}
	
	
	
	// Validate Token
	public Boolean validateToken(String token, UserDetails userDetails)
	{
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
}
