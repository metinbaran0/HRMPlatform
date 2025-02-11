package org.hrmplatform.hrmplatform.controller;

import org.hrmplatform.hrmplatform.dto.request.LoginRequestDto;
import org.hrmplatform.hrmplatform.dto.request.RegisterRequestDto;
import org.hrmplatform.hrmplatform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(USER)
public class UserController {
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping(REGISTER)
	public ResponseEntity<String> register(@RequestBody RegisterRequestDto request) {
		String response = userService.register(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping(DOLOGIN)
	public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
		String response = userService.doLogin(request);
		return ResponseEntity.ok(response);
	}
}