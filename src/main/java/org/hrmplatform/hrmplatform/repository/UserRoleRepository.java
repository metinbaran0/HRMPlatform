package org.hrmplatform.hrmplatform.repository;


import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
	List<UserRole> findByUserId(Long userId);
	
	void deleteByUserId(Long userId);
	
	Optional<UserRole> findByUserIdAndRole(Long userId, Role role);
	
	void deleteByUserIdAndRole(Long userId, Role role);
	
}