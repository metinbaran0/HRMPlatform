package org.hrmplatform.hrmplatform.repository;


import org.hrmplatform.hrmplatform.dto.response.UserRoleResponseDto;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;
import org.hrmplatform.hrmplatform.view.VwUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
	List<UserRole> findAllByUserId(Long userId);
	
	void deleteByUserId(Long userId);
	
	void deleteByUserIdAndRole(Long userId, Role role);
	
	@Query("SELECT new org.hrmplatform.hrmplatform.view.VwUserRole(u.name, r.role) " +
			"FROM User u JOIN UserRole r ON u.id = r.userId " +
			"WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
	List<VwUserRole> findByName(@Param("name") String name);
	
	
	@Query("SELECT new org.hrmplatform.hrmplatform.view.VwUserRole(u.name, r.role) " +
			"FROM User u JOIN UserRole r ON u.id = r.userId " +
			"WHERE r.role = :role ")
	List<VwUserRole> findByRole(@Param("role") Role role);

	List<UserRole> findByUserId(Long userId);
	
	
	@Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId")
	Optional<UserRole> findUserRoleByUserId(@Param("userId") Long userId);
	
	
	
	
	
	
	@Query("SELECT COUNT(u) FROM UserRole u WHERE u.role = :role")
	int countByUserRoleRole(@Param("role") Role role);
	
	
	
	Optional<UserRole> findByUserIdAndRole(Long userId, Role role);
	
	
	
}