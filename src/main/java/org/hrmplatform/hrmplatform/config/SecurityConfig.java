package org.hrmplatform.hrmplatform.config;

import lombok.extern.slf4j.Slf4j;
import org.hrmplatform.hrmplatform.constant.EndPoints;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
//@EnableGlobalMethodSecurity(prePostEnabled = true) // Spring Security 6 ile birlikte @EnableGlobalMethodSecurity(prePostEnabled = true) artık kullanılmıyor.
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public JwtTokenFilter getJwtTokenFilter() {
		return new JwtTokenFilter();  // JWT doğrulama için filter
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(req -> {
			// İzin verilen public API ve authentication endpointler
			req

					// İzin verilen public API ve authentication endpointler
					.requestMatchers(EndPoints.AUTH + "/register", EndPoints.AUTH + "/dologin", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

					// Rol kontrolüne dayalı endpointler

					.requestMatchers(EndPoints.AUTH + "/register", EndPoints.AUTH + "/dologin", "/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Bu yollar herkese açık
					.requestMatchers(EndPoints.EMPLOYEE + "/**").permitAll()  // EmployeeController'daki tüm endpointler herkese açık
					.requestMatchers(EndPoints.COMPANY + "/**").permitAll()  // CompanyController'daki tüm endpointler
					// herkese açık

					.requestMatchers(EndPoints.ROOT + EndPoints.DEVELOPER + "/**")
					.hasAnyAuthority("SITE_ADMIN", "COMPANY_ADMIN")  // Sadece SITE_ADMIN ve COMPANY_ADMIN erişebilir
					.anyRequest().authenticated();  // Diğer tüm istekler için kimlik doğrulama gereksinimi olacak
		});


		// CSRF'yi devre dışı bırakma
		http.csrf(AbstractHttpConfigurer::disable);

		// JWT doğrulama için filter ekle

		
		// CSRF'yi devre dışı bırak
		http.csrf(AbstractHttpConfigurer::disable);
		
		// JWT doğrulama filtresini ekle

		http.addFilterBefore(getJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
	
}