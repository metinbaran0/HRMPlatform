package org.hrmplatform.hrmplatform.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.LeaveRequestDto;
import org.hrmplatform.hrmplatform.entity.Employee;
import org.hrmplatform.hrmplatform.entity.LeaveRequest;
import org.hrmplatform.hrmplatform.service.EmployeeService;
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
	private final AuthService authService;// AuthService sınıfını kullanıyorsanız ekleyin
	private final EmployeeService employeeService;
	
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
	
	
	
	// Şirket yöneticisinin izin talebi oluşturma
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@PostMapping(LEAVEREQUEST_MANAGER)
	public ResponseEntity<BaseResponse<LeaveRequest>> requestLeaveByAdmin(
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
		
		// Company ID'yi token'dan alıyoruz (employeeId'ye gerek yok)
		Long companyId = authService.getCompanyIdFromToken(token);
		
		// Token doğrulama ve companyId kontrolünü gözden geçirin
		if (companyId == null) {
			return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
			                                     .code(400)
			                                     .success(false)
			                                     .message("Hata: Token geçersiz veya kullanıcı bilgileri eksik.")
			                                     .build());
		}
		
		// Şirket yöneticisi izin talebini oluşturuyor
		LeaveRequest createdLeave = leaveService.requestLeave(dto, companyId, null); // employeeId null, çünkü admin için geçerli değil
		return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(createdLeave)
		                                     .message("İzin talebi başarıyla oluşturuldu.")
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
			@PathVariable Long employeeId) {
		
		// Çalışanın şirketine ait olup olmadığını kontrol et
		Employee employee = employeeService.findById(employeeId)
		                                   .orElseThrow(() -> new EntityNotFoundException("Çalışan bulunamadı."));
		
		// Yöneticinin şirket ID'sini al
		Long managerCompanyId = employee.getCompanyId();  // Çalışanın şirketi ile yöneticinin şirketi aynı olmalı
		
		if (!employee.getCompanyId().equals(managerCompanyId)) {
			throw new SecurityException("Bu çalışanın izin talebini onaylama yetkiniz yok.");
		}
		
		// İzni onayla
		LeaveRequest acceptedLeave = leaveService.acceptLeaveRequest(employeeId);
		
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
			
			
			@PathVariable Long employeeId) {
		
		
		
		
		
		// İzni reddet
		LeaveRequest rejectedLeave = leaveService.rejectLeaveRequest(employeeId);
		
		return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(rejectedLeave)
		                                     .message("İzin talebi başarıyla reddedildi.")
		                                     .build());
	}
	
}