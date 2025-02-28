package org.hrmplatform.hrmplatform.service;

import org.hrmplatform.hrmplatform.entity.Notification;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.enums.Status;
import org.hrmplatform.hrmplatform.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	public void sendNotification(User receiver, Status type, String message) {
		Notification notification = new Notification();
		notification.setReceiver(receiver);
		notification.setType(type);
		notification.setMessage(message);
		notificationRepository.save(notification);
	}
	
	public List<Notification> getUserNotifications(Long userId) {
		return notificationRepository.findByReceiverIdAndIsReadFalse(userId);
	}
	
	public void markAsRead(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
		                                                  .orElseThrow(() -> new RuntimeException("Notification not found"));
		notification.setRead(true);
		notificationRepository.save(notification);
	}
}