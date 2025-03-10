package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.LeaveRequestDto;
import org.hrmplatform.hrmplatform.entity.LeaveRequest;
import org.hrmplatform.hrmplatform.service.LeaveService;
import org.hrmplatform.hrmplatform.service.AuthService; // Eğer AuthService kullanıyorsanız
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(LEAVE)
@RequiredArgsConstructor
public class LeaveController {
	
	private final LeaveService leaveService;
	private final AuthService authService; // AuthService sınıfını kullanıyorsanız ekleyin
	
	// Kullanıcı izin talebi oluşturma
	@PreAuthorize("hasRole('EMPLOYEE')")
	@PostMapping(LEAVEREQUEST)
	public ResponseEntity<BaseResponse<LeaveRequest>> requestLeave(
			@RequestHeader("Authorization") String token,
			@RequestBody LeaveRequestDto dto) {
		Long employeeId = authService.getEmployeeIdFromToken(token);
		if (!leaveService.isUserExists(employeeId)) {
			return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
			                                     .code(400)
			                                     .success(false)
			                                     .message("Hata: Belirtilen kullanıcı mevcut değil.")
			                                     .build());
		}
		LeaveRequest createdLeave = leaveService.requestLeave(dto, employeeId);
		return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(createdLeave)
		                                     .message("İzin isteği başarıyla oluşturuldu.")
		                                     .build());
	}
	
	// Kullanıcıya ait izin taleplerini listeleme
	@PreAuthorize("hasRole('EMPLOYEE')")
	@GetMapping(LEAVEBYUSERID)
	public ResponseEntity<BaseResponse<List<LeaveRequest>>> getUserLeaves(
			@RequestHeader("Authorization") String token) {
		Long employeeId = authService.getEmployeeIdFromToken(token);
		if (!leaveService.isUserExists(employeeId)) {
			return ResponseEntity.ok(BaseResponse.<List<LeaveRequest>>builder()
			                                     .code(404)
			                                     .success(false)
			                                     .message("Hata: Kullanıcı bulunamadı.")
			                                     .build());
		}
		List<LeaveRequest> leaveRequests = leaveService.getLeaveRequestsByUserId(employeeId);
		return ResponseEntity.ok(BaseResponse.<List<LeaveRequest>>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(leaveRequests)
		                                     .message("Kullanıcının izin talepleri getirildi.")
		                                     .build());
	}
	
	// Yönetici tüm bekleyen izin taleplerini görme
	@PreAuthorize("hasRole('COMPANY_ADMIN')")
	@GetMapping("/pending")
	public ResponseEntity<BaseResponse<List<LeaveRequest>>> getAllPendingLeaveRequests() {
		List<LeaveRequest> pendingRequests = leaveService.getAllPendingLeaveRequests();
		return ResponseEntity.ok(BaseResponse.<List<LeaveRequest>>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(pendingRequests)
		                                     .message("Bekleyen izin talepleri getirildi.")
		                                     .build());
	}
	
	// Yönetici izin talebini onaylama
	@PreAuthorize("hasRole('COMPANY_ADMIN')")
	@PostMapping("/accept/{employeeId}")
	public ResponseEntity<BaseResponse<LeaveRequest>> acceptLeaveRequest(
			@RequestHeader("Authorization") String token,
			@PathVariable Long employeeId) {
		Long managerId = authService.getEmployeeIdFromToken(token);
		LeaveRequest acceptedLeave = leaveService.acceptLeaveRequest(managerId, employeeId);
		return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(acceptedLeave)
		                                     .message("İzin talebi başarıyla onaylandı.")
		                                     .build());
	}
	
	// Yönetici izin talebini reddetme
	@PreAuthorize("hasRole('COMPANY_ADMIN')")
	@PostMapping("/reject/{employeeId}")
	public ResponseEntity<BaseResponse<LeaveRequest>> rejectLeaveRequest(
			@RequestHeader("Authorization") String token,
			@PathVariable Long employeeId) {
		Long managerId = authService.getEmployeeIdFromToken(token);
		LeaveRequest rejectedLeave = leaveService.rejectLeaveRequest(managerId, employeeId);
		return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(rejectedLeave)
		                                     .message("İzin talebi başarıyla reddedildi.")
		                                     .build());
	}
}