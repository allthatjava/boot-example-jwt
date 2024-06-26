package brian.example.boot.jwt.controller;

import brian.example.boot.jwt.model.RefreshRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import brian.example.boot.jwt.config.JwtTokenUtil;
import brian.example.boot.jwt.model.AuthRequest;
import brian.example.boot.jwt.model.AuthResponse;
import brian.example.boot.jwt.service.JwtUserDetailsService;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private JwtUserDetailsService userDetailService;

	@PostMapping("/authenticate")
	public ResponseEntity<AuthResponse> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception{
	
		// test credentials
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken( authRequest.getUsername(), authRequest.getPassword()));
		} catch (BadCredentialsException ex) {
			throw new Exception("Bad Credentials");
		} catch (AuthenticationException e) {
			throw new Exception("Other Credentials error");
		}
		
		// If reached here, credential test has been passed
		final UserDetails userDetails = userDetailService.loadUserByUsername(authRequest.getUsername());
		
		// Generate token with user name
		final String token = jwtTokenUtil.generateToken(userDetails, "ACCESS");
		final String refreshToken = jwtTokenUtil.generateToken(userDetails, "REFRESH");
		
		// Return 
		return ResponseEntity.ok(new AuthResponse(token,refreshToken));
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAuthToken(@RequestBody RefreshRequest refreshRequest) {
		UserDetails userDetails = userDetailService.loadUserByUsername(jwtTokenUtil.getUsernameFromToken(refreshRequest.getRefreshToken()));

		if (jwtTokenUtil.validateToken(refreshRequest.getRefreshToken(), userDetails)) {
			String newAccessToken = jwtTokenUtil.generateToken(userDetails, "ACCESS");
			String newRefreshToken = jwtTokenUtil.generateToken(userDetails, "REFRESH");
			return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
		} else {
			return ResponseEntity.status(403).body("Invalid refresh token");
		}
	}
}
