package org.hrmplatform.hrmplatform.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.hrmplatform.hrmplatform.dto.response.TokenValidationResult;
import org.hrmplatform.hrmplatform.util.JwtManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
	@Autowired // ilgili değişken için nesne(bean) yaratmak için kullanırız.
	private JwtManager jwtManager;
	@Autowired
	private JwtUserDetails jwtUserDetails;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		/**
		 *Bu kısım gelen tüm isteklerin üzerinden geçtiği kısım. Burada isteklerin içerisinde bulunan TOKEN- JWT
		 * bilgisini okuyup, doğrulamasını ve kişinin kimliğini tespit ederek oturum açmasını
		 * sağlayacağız.
		 */
		final String authorizationHeader = request.getHeader("Authorization");
		if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7);
			Optional<TokenValidationResult> tokenValidationResult = jwtManager.validateToken(token); // TokenValidationResult kullan
			if (tokenValidationResult.isPresent()) {
				Long userId = tokenValidationResult.get().authId(); // authId bilgisini al
				UserDetails userDetails = jwtUserDetails.getUserById(userId);
				UsernamePasswordAuthenticationToken authenticationToken
						= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}
		filterChain.doFilter(request, response);
	}
}