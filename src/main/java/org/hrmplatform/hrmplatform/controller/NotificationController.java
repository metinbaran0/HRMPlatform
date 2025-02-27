package org.hrmplatform.hrmplatform.controller;

import org.hrmplatform.hrmplatform.entity.Notification;
import org.hrmplatform.hrmplatform.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	
	@GetMapping("/{userId}")
	public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
		return ResponseEntity.ok(notificationService.getUserNotifications(userId));
	}
	
	@PostMapping("/mark-as-read/{id}")
	public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
		notificationService.markAsRead(id);
		return ResponseEntity.ok().build();
	}
}