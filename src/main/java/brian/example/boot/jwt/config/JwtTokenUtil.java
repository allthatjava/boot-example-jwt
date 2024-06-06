package brian.example.boot.jwt.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtTokenUtil {
	
	@Value("${jwt.secret}")
	private String secret;
	
	// Util method to get data out of Token
	private <T> T getClaimsFromToken(String token, Function<Claims, T> claimResolver) {
		final Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			System.out.println("Token has been expired");
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			System.out.println("Token is not valid");
			throw new RuntimeException(e);
		}

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
	public String generateToken(UserDetails userDetails, String type) {
		Map<String, Object> claims = new HashMap<>();
		int expiryTime = "ACCESS".equals(type)?5:24;
		
		return Jwts.builder()
				.setClaims(claims)
				.setSubject( userDetails.getUsername() )
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration( new Date(System.currentTimeMillis() + (expiryTime * 60 * 60 ) * 1000) ) 	// 5 hours
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
