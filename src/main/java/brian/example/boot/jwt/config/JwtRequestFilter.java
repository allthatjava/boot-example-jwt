package brian.example.boot.jwt.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import brian.example.boot.jwt.service.JwtUserDetailsService;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private JwtUserDetailsService service;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String jwtToken = null;
		String username = null;
		
		final String requestTokenHeader = request.getHeader("Authorization");
		
		if( requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			
			username = jwtTokenUtil.getUsernameFromToken(jwtToken);
		}else {
			logger.warn("This token doesn't start with Bearer string");
		}
		
		// username is there and not authenticated yet
		if( username != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
			
			UserDetails userDetails = service.loadUserByUsername(username);
			
			// Validate the token
			if( jwtTokenUtil.validateToken(jwtToken, userDetails) ) {
				UsernamePasswordAuthenticationToken upaToken 
						= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				upaToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(upaToken);
			}
		}
		
		super.doFilter(request, response, filterChain);
		
	}

}
