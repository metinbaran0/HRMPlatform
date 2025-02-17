package org.hrmplatform.hrmplatform.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hrmplatform.hrmplatform.util.JwtManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtManager jwtManager;
	
	@Autowired
	private JwtUserDetails jwtUserDetails;
	
	private String secretKey = "yourSecretKey";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("JwtTokenFilter doFilterInternal çalıştı...");
		String authorizationHeader = request.getHeader("Authorization");
		log.warn("Gelen Token: " + authorizationHeader);
		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7);
			log.warn("Gelen Substring Yapılmış Token: " + token);
			
			try {
				// JWT'yi doğrulama işlemi
				Claims claims = Jwts.parser()
				                    .setSigningKey(secretKey)
				                    .parseClaimsJws(token)
				                    .getBody();
				
				// JWT geçerliyse, kullanıcıyı güvenli bağlama
				Optional<Long> optionalUserId = jwtManager.validateJWT(token);
				
				if (optionalUserId.isPresent()) {
					UserDetails userDetails = jwtUserDetails.loadUserById(optionalUserId.get());
					UsernamePasswordAuthenticationToken upaToken =
							new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(upaToken);
				}
			} catch (Exception e) {
				// Hata durumunda token geçersiz
				log.warn("Geçersiz token: {}", token);
			}
		}
		
		filterChain.doFilter(request, response);
	}
}