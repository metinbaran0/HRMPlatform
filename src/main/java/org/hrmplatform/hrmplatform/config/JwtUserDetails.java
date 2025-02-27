package org.hrmplatform.hrmplatform.config;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.service.UserRoleService;
import org.hrmplatform.hrmplatform.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtUserDetails implements UserDetailsService {
	private final UserService userService;
	private final UserRoleService userRoleService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return null;
	}
	public UserDetails getUserById(Long userId){
		Optional<User> userOptional = userService.findById(userId);
		if(userOptional.isEmpty()) return null;
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		List<UserRole> userRoles = userRoleService.getAllUserRoleByUserId(userId);
		userRoles.forEach(userRole -> authorities.add(new SimpleGrantedAuthority(userRole.getRole().name())));
		
		return org.springframework.security.core.userdetails.User.builder()
		                                                         .username(userOptional.get().getEmail())
		                                                         .password(userOptional.get().getPassword())
		                                                         .accountExpired(false)
		                                                         .accountLocked(false)
		                                                         .credentialsExpired(false)
		                                                         .disabled(false)
		                                                         .authorities(authorities)
		                                                         .build();
	}
}