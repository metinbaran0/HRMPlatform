package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.LeaveRequestDto;
import org.hrmplatform.hrmplatform.entity.LeaveRequest;
import org.hrmplatform.hrmplatform.service.LeaveService;
import org.hrmplatform.hrmplatform.service.AuthService; // Eğer AuthService kullanıyorsanız
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(LEAVE)
@RequiredArgsConstructor
@CrossOrigin("*")
public class LeaveController {
	
	private final LeaveService leaveService;
	private final AuthService authService; // AuthService sınıfını kullanıyorsanız ekleyin
	
	// Kullanıcı izin talebi oluşturma
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@PostMapping(LEAVEREQUEST)
	public ResponseEntity<BaseResponse<LeaveRequest>> requestLeave(
			@RequestHeader("Authorization") String token,
			@RequestBody LeaveRequestDto dto) {
		// Authorization başlığını kontrol edin
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
			                                     .code(400)
			                                     .success(false)
			                                     .message("Hata: Geçersiz veya eksik token.")
			                                     .build());
		}
		
		// Employee ID ve Company ID'yi token'dan alıyoruz
		Long employeeId = authService.getEmployeeIdFromToken(token);
		Long companyId = authService.getCompanyIdFromToken(token);
		
		// Token doğrulama ve employeeId kontrolünü gözden geçirin
		if (employeeId == null || companyId == null) {
			return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
			                                     .code(400)
			                                     .success(false)
			                                     .message("Hata: Token geçersiz veya kullanıcı bilgileri eksik.")
			                                     .build());
		}
		


		

		
		// İzin talebini oluşturuyoruz
		LeaveRequest createdLeave = leaveService.requestLeave(dto, employeeId, companyId);
		return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(createdLeave)
		                                     .message("İzin isteği başarıyla oluşturuldu.")
		                                     .build());
	}
	
	// Kullanıcıya ait izin taleplerini listeleme
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@GetMapping(LEAVEBYUSERID)
	public ResponseEntity<BaseResponse<List<LeaveRequest>>> getUserLeaves(
			@RequestHeader("Authorization") String token) {
		Long employeeId = authService.getEmployeeIdFromToken(token);
		List<LeaveRequest> leaveRequests = leaveService.getLeaveRequestsByUserId(employeeId);
		return ResponseEntity.ok(BaseResponse.<List<LeaveRequest>>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(leaveRequests)
		                                     .message("Kullanıcının izin talepleri getirildi.")
		                                     .build());
	}
	
	// Yönetici tüm bekleyen izin taleplerini görme
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@GetMapping(PENDINGLEAVESFORMANAGER)
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
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@PostMapping(ACCEPTLEAVE)
	public ResponseEntity<BaseResponse<LeaveRequest>> acceptLeaveRequest(
			@RequestHeader("Authorization") String token,
			@PathVariable Long employeeId) {
		
		// Token'dan yönetici ID'sini al
		Long managerId = authService.getEmployeeIdFromToken(token);
		
		
		// İzni onayla
		LeaveRequest acceptedLeave = leaveService.acceptLeaveRequest(managerId, employeeId);
		
		return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(acceptedLeave)
		                                     .message("İzin talebi başarıyla onaylandı.")
		                                     .build());
	}
	
	// Yönetici izin talebini reddetme
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@PostMapping(REJECTLEAVE)
	public ResponseEntity<BaseResponse<LeaveRequest>> rejectLeaveRequest(
			@RequestHeader("Authorization") String token,
			@PathVariable Long employeeId) {
		
		// Token'dan yönetici ID'sini al
		Long managerId = authService.getEmployeeIdFromToken(token);
		
		
		// İzni reddet
		LeaveRequest rejectedLeave = leaveService.rejectLeaveRequest(managerId, employeeId);
		
		return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(rejectedLeave)
		                                     .message("İzin talebi başarıyla reddedildi.")
		                                     .build());
	}
	
}