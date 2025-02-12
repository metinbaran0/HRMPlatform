package org.hrmplatform.hrmplatform.service;


import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.roles.UserRoleRequestDto;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {
	private final UserRoleRepository userRoleRepository;
	
}