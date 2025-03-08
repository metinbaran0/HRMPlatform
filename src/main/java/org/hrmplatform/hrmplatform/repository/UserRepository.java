package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(String email);
	Optional<User> findByActivationCode(String activationCode);  // Aktivasyon kodu ile kullanıcıyı bulma metodu
	List<User> findByNameContainingAndEmailContaining(String name, String email);
	Optional<User> findByName(String name);


	
	
 }