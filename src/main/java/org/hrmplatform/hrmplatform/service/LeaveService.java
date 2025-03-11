package org.hrmplatform.hrmplatform.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.LeaveRequestDto;
import org.hrmplatform.hrmplatform.entity.LeaveRequest;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;
import org.hrmplatform.hrmplatform.enums.Status;
import org.hrmplatform.hrmplatform.repository.LeaveRepository;
import org.hrmplatform.hrmplatform.repository.UserRepository;
import org.hrmplatform.hrmplatform.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {
	private final LeaveRepository leaveRepository;
	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;
	
	// Yeni izin talebi oluşturma
	public LeaveRequest requestLeave(@Valid LeaveRequestDto leaveRequestDto, Long employeeId, Long companyId) {
		LocalDateTime startDateTime = leaveRequestDto.startDate().atStartOfDay();
		LocalDateTime endDateTime = leaveRequestDto.endDate().atStartOfDay();
		
		// Aynı tarih aralığında onaylı izin talebi olup olmadığını kontrol et
		List<LeaveRequest> overlappingRequests =
				leaveRepository.findByEmployeeIdAndStatus(employeeId, Status.APPROVED);
		
		for (LeaveRequest existingRequest : overlappingRequests) {
			if ((startDateTime.isBefore(existingRequest.getEndDate()) && endDateTime.isAfter(existingRequest.getStartDate()))) {
				throw new IllegalArgumentException("Hata: Bu tarihlerde zaten bir izin talebiniz var.");
			}
		}
		
		// Yeni LeaveRequest oluşturuluyor
		LeaveRequest leaveRequest = LeaveRequest.builder()
		                                        .startDate(startDateTime)
		                                        .endDate(endDateTime)
		                                        .leaveType(leaveRequestDto.leaveType())
		                                        .status(Status.PENDING) // İzin talebi başlangıçta beklemede
		                                        .createdAt(LocalDateTime.now())
		                                        .updatedAt(LocalDateTime.now())
		                                        .employeeId(employeeId)  // Çalışan ID'si ekleniyor
		                                        .companyId(companyId)    // Şirket ID'si ekleniyor
		                                        .build();
		
		return leaveRepository.save(leaveRequest);  // İzin talebi kaydediliyor
	}

	
	// Kullanıcıya ait izin taleplerini getirme (Sadece APPROVED)
	public List<LeaveRequest> getLeaveRequestsByUserId(Long userId) {
		return leaveRepository.findByEmployeeIdAndStatus(userId, Status.APPROVED);
	}
	
	// Yöneticinin tüm bekleyen izin taleplerini getirme
	public List<LeaveRequest> getAllPendingLeaveRequests() {
		return leaveRepository.findByStatus(Status.PENDING);
	}
	
	// İzin talebini kabul etme (Yönetici tarafından)
	public LeaveRequest acceptLeaveRequest(Long managerId, Long employeeId) {
		UserRole manager = userRoleRepository.findById(managerId)
		                                     .orElseThrow(() -> new EntityNotFoundException("Yönetici bulunamadı."));
		
		if (manager.getRole() == null || !manager.getRole().equals(Role.COMPANY_ADMIN)) {
			throw new SecurityException("Yalnızca şirket yöneticileri izin taleplerini onaylayabilir.");
		}
		
		// Bekleyen izin talebini al
		LeaveRequest leaveRequest = leaveRepository.findByEmployeeIdAndStatus(employeeId, Status.PENDING)
		                                           .stream()
		                                           .findFirst()
		                                           .orElseThrow(() -> new EntityNotFoundException("Bekleyen izin talebi bulunamadı."));
		
		leaveRequest.setStatus(Status.APPROVED);
		leaveRequest.setUpdatedAt(LocalDateTime.now());
		return leaveRepository.save(leaveRequest);
	}
	
	// İzin talebini reddetme (Yönetici tarafından)
	public LeaveRequest rejectLeaveRequest(Long managerId, Long employeeId) {
		UserRole manager = userRoleRepository.findById(managerId)
		                                     .orElseThrow(() -> new EntityNotFoundException("Yönetici bulunamadı."));
		
		if (manager.getRole() == null || !manager.getRole().equals(Role.COMPANY_ADMIN)) {
			throw new SecurityException("Yalnızca şirket yöneticileri izin taleplerini reddedebilir.");
		}
		
		// Bekleyen izin talebini al
		LeaveRequest leaveRequest = leaveRepository.findByEmployeeIdAndStatus(employeeId, Status.PENDING)
		                                           .stream()
		                                           .findFirst()
		                                           .orElseThrow(() -> new EntityNotFoundException("Bekleyen izin talebi bulunamadı."));
		
		leaveRequest.setStatus(Status.REJECTED);
		leaveRequest.setUpdatedAt(LocalDateTime.now());
		return leaveRepository.save(leaveRequest);
	}
	
	// Kullanıcı var mı?
	public boolean isUserExists(Long employeeId) {
		return userRepository.existsById(employeeId);
	}
}