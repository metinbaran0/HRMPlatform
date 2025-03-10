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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.List;

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
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000")); // ReactJS'in çalıştığı URL
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true); // withCredentials kullanıyorsanız bu gerekli
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF yapılandırmasını özelleştiriyoruz
		http.csrf(csrf -> csrf
				    .ignoringRequestMatchers(
						    EndPoints.COMPANY + EndPoints.ADDCOMPANY, // add company endpointi
						    EndPoints.AUTH + "/dologin",              // login için
						    EndPoints.AUTH + "/register" ,           // register için
						    EndPoints.COMMENT + "/getall-comment"      // yorumlar için
				    ))
		    .authorizeHttpRequests(req -> {
			    req
					    // Herkese açık endpointler
					    .requestMatchers(EndPoints.AUTH + "/register", EndPoints.AUTH + "/dologin", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
					    .requestMatchers(EndPoints.EMPLOYEE + "/**", EndPoints.EMAIL + "/**").permitAll()
					    .requestMatchers(EndPoints.COMPANY + EndPoints.ADDCOMPANY, EndPoints.COMPANY + "/verify-email").permitAll()
					    
					    // "COMPANY_ADMIN" yetkisiyle erişilebilen endpointler
					    .requestMatchers(EndPoints.LEAVE + EndPoints.PENDINGLEAVESFORMANAGER).hasAuthority("COMPANY_ADMIN")
					    .requestMatchers(EndPoints.ASSET + "/add").hasAuthority("COMPANY_ADMIN")
					    .requestMatchers(EndPoints.ASSET + "/update/**").hasRole("COMPANY_ADMIN")
					    .requestMatchers(EndPoints.ASSET + "/delete/**").hasRole("COMPANY_ADMIN")
					    
					    // "EMPLOYEE" ve "COMPANY_ADMIN" rollerinin erişebileceği endpointler
					    .requestMatchers(EndPoints.LEAVE + "/**").hasAnyAuthority("EMPLOYEE", "COMPANY_ADMIN")
					    .requestMatchers(EndPoints.ASSET + "/{id}").hasAnyRole("EMPLOYEE", "COMPANY_ADMIN")
					    .requestMatchers(EndPoints.ASSET + "/all").hasAnyRole("EMPLOYEE", "COMPANY_ADMIN")
					    .requestMatchers("/company/**", "/shift/**").hasAnyRole("SITE_ADMIN", "COMPANY_ADMIN")
					    
					    // "SITE_ADMIN" ve "COMPANY_ADMIN" erişebileceği endpointler
					    .requestMatchers(EndPoints.ROOT + EndPoints.DEVELOPER + "/**").hasAnyAuthority("SITE_ADMIN", "COMPANY_ADMIN")
					    
					    // Diğer istekler kimlik doğrulama gerektirir
					    .anyRequest().authenticated();
		    });
		
		// CSRF'yi devre dışı bırak
		http.csrf(AbstractHttpConfigurer::disable);
		// CORS yapılandırmasını ekle
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		
		// JWT doğrulama filtresini ekle
		http.addFilterBefore(getJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}