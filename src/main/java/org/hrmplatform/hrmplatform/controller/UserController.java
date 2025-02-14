package org.hrmplatform.hrmplatform.controller;

import jakarta.validation.Valid;
import org.hrmplatform.hrmplatform.dto.request.LoginRequestDto;
import org.hrmplatform.hrmplatform.dto.request.RegisterRequestDto;
import org.hrmplatform.hrmplatform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(USER)
@CrossOrigin("*")
public class UserController {
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping(REGISTER)
	public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDto request) {
		String response = userService.register(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping(DOLOGIN)
	public ResponseEntity<String> doLogin(@RequestBody @Valid LoginRequestDto request) {
		String token = userService.doLogin(request);
		return ResponseEntity.ok(token);
	}
	
	

}