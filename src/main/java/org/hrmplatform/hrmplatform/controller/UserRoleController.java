package org.hrmplatform.hrmplatform.controller;


import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.roles.UserRoleRequestDto;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.service.UserRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Kullanıcı rolleriyle ilgili işlemleri yöneten REST API Controller sınıfıdır.
 *
 * Görevleri:
 * - Kullanıcılara belirli roller atamak
 * - Kullanıcının sahip olduğu rolleri sorgulamak
 * - Tüm kullanıcı rollerini listelemek
 * - Belirli bir kullanıcı rolünü silmek
 */
@RestController

@RequiredArgsConstructor
public class UserRoleController {
	private final UserRoleService userRoleService;
	
	
	
	
}