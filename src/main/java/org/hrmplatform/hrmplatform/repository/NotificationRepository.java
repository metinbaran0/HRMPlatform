package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByReceiverIdAndIsReadFalse(Long receiverId);
}