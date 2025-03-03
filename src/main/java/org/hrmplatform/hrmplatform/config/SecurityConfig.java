package org.hrmplatform.hrmplatform.config;

import lombok.extern.slf4j.Slf4j;
import org.hrmplatform.hrmplatform.constant.EndPoints;
import org.hrmplatform.hrmplatform.util.JwtManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	@Bean
	public JwtTokenFilter getJwtTokenFilter() {
		return new JwtTokenFilter();  // JWT doğrulama için filter
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(req -> {
			req
					.requestMatchers(EndPoints.AUTH + "/register", EndPoints.AUTH + "/dologin", "/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Herkese açık
					.requestMatchers(EndPoints.EMPLOYEE + "/**").permitAll()  // EmployeeController'daki tüm endpointler açık
					.requestMatchers("/company/**","/shift/**").hasAnyRole("SITE_ADMIN", "COMPANY_ADMIN")  // Sadece SITE_ADMIN ve COMPANY_ADMIN erişebilir
					.requestMatchers(EndPoints.ROOT + EndPoints.DEVELOPER + "/**")
					.hasAnyAuthority("SITE_ADMIN", "COMPANY_ADMIN")  // Sadece SITE_ADMIN ve COMPANY_ADMIN erişebilir
					.anyRequest().authenticated();  // Diğer istekler kimlik doğrulama gerektirir
		});
		
		// CSRF'yi devre dışı bırak
		http.csrf(AbstractHttpConfigurer::disable);
		
		
		
		
		
		// JWT doğrulama filtresini ekle
		http.addFilterBefore(getJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}