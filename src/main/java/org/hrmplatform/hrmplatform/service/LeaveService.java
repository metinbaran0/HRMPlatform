package org.hrmplatform.hrmplatform.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.LeaveRequestDto;
import org.hrmplatform.hrmplatform.entity.LeaveRequest;
import org.hrmplatform.hrmplatform.enums.Status;
import org.hrmplatform.hrmplatform.repository.LeaveRepository;
import org.hrmplatform.hrmplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {
	private final LeaveRepository leaveRepository;
	private final UserRepository userRepository;
	
	// Yeni izin talebi oluşturma
	public LeaveRequest requestLeave(@Valid LeaveRequestDto leaveRequestDto) {
		LocalDateTime startDateTime = leaveRequestDto.startDate().atStartOfDay();
		LocalDateTime endDateTime = leaveRequestDto.endDate().atStartOfDay();
		
		// Aynı tarih aralığında onaylı izin talebi olup olmadığını kontrol et
		List<LeaveRequest> overlappingRequests =
				leaveRepository.findByEmployeeIdAndStatus(leaveRequestDto.employeeId(), Status.APPROVED);
		
		for (LeaveRequest existingRequest : overlappingRequests) {
			if ((startDateTime.isBefore(existingRequest.getEndDate()) && endDateTime.isAfter(existingRequest.getStartDate()))) {
				throw new IllegalArgumentException("Hata: Bu tarihlerde zaten bir izin talebiniz var.");
			}
		}
		
		LeaveRequest leaveRequest = LeaveRequest.builder()
		                                        .startDate(startDateTime)
		                                        .endDate(endDateTime)
		                                        .leaveType(leaveRequestDto.leaveType())
		                                        .status(Status.PENDING)  // İzin talebi başlangıçta beklemede
		                                        .createdAt(LocalDateTime.now())
		                                        .updatedAt(LocalDateTime.now())
		                                        .build();
		
		return leaveRepository.save(leaveRequest);
	}
	
	// Kullanıcıya ait izin taleplerini getirme (Sadece APPROVED)
	public List<LeaveRequest> getLeaveRequestsByUserId(Long userId) {
		return leaveRepository.findByEmployeeIdAndStatus(userId, Status.APPROVED);
	}
	
	// Yöneticinin tüm bekleyen izin taleplerini getirme
	public List<LeaveRequest> getAllPendingLeaveRequests() {
		return leaveRepository.findByStatus(Status.PENDING);
	}
	
	// İzin talebini kabul etme
	public LeaveRequest acceptLeaveRequest(Long managerId, Long employeeId) {
		LeaveRequest leaveRequest = leaveRepository.findById(employeeId)
		                                           .orElseThrow(() -> new EntityNotFoundException("Hata: İzin talebi bulunamadı."));
		
		// Yalnızca bekleyen izin taleplerini kabul edebilir
		if (!leaveRequest.getStatus().equals(Status.PENDING)) {
			throw new IllegalArgumentException("Hata: Yalnızca bekleyen izin taleplerini onaylayabilirsiniz.");
		}
		
		leaveRequest.setStatus(Status.APPROVED);
		return leaveRepository.save(leaveRequest);
	}
	
	// İzin talebini reddetme
	public LeaveRequest rejectLeaveRequest(Long managerId, Long employeeId) {
		LeaveRequest leaveRequest = leaveRepository.findById(employeeId)
		                                           .orElseThrow(() -> new EntityNotFoundException("Hata: İzin talebi bulunamadı."));
		
		// Yalnızca bekleyen izin taleplerini reddedebilir
		if (!leaveRequest.getStatus().equals(Status.PENDING)) {
			throw new IllegalArgumentException("Hata: Yalnızca bekleyen izin taleplerini reddedebilirsiniz.");
		}
		
		leaveRequest.setStatus(Status.REJECTED);
		return leaveRepository.save(leaveRequest);
	}
	
	// Kullanıcı var mı?
	public boolean isUserExists(Long employeeId) {
		return userRepository.existsById(employeeId);
	}
}