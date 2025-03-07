package org.hrmplatform.hrmplatform.controller;

import jakarta.persistence.EntityNotFoundException;
import org.hrmplatform.hrmplatform.dto.request.LeaveRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.LeaveRequest;
import org.hrmplatform.hrmplatform.service.LeaveService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(LEAVE)
@CrossOrigin("*")
public class LeaveController {
	private final LeaveService leaveService;
	
	public LeaveController(LeaveService leaveService) {
		this.leaveService = leaveService;
	}
	
	// Yeni izin talebi oluşturma (Kullanıcı yapacak)
	@PreAuthorize("hasRole('EMPLOYEE')")
	@PostMapping(LEAVEREQUEST)
	public ResponseEntity<BaseResponse<LeaveRequest>> requestLeave(@RequestBody LeaveRequestDto dto) {
		if (!leaveService.isUserExists(dto.employeeId())) {
			return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
			                                     .code(400)
			                                     .success(false)
			                                     .message("Hata: Belirtilen kullanıcı mevcut değil.")
			                                     .build());
		}
		LeaveRequest createdLeave = leaveService.requestLeave(dto);
		return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(createdLeave)
		                                     .message("İzin isteği başarıyla oluşturuldu.")
		                                     .build());
	}
	
	// Kullanıcıya ait izin taleplerini getirme
	@PreAuthorize("hasRole('EMPLOYEE,COMPANY_ADMIN')")
	@GetMapping(LEAVEBYUSERID)
	public ResponseEntity<BaseResponse<List<LeaveRequest>>> getUserLeaves(@PathVariable Long employeeId) {
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
	
	// Yöneticinin tüm bekleyen izin taleplerini getirme
	@PreAuthorize("hasRole('COMPANY_ADMIN')")
	@GetMapping(PENDINGLEAVESFORMANAGER)
	public ResponseEntity<BaseResponse<List<LeaveRequest>>> getPendingLeavesForManager(@PathVariable Long managerId) {
		// Yönetici sadece bekleyen izin taleplerini görmeli
		List<LeaveRequest> leaveRequests = leaveService.getAllPendingLeaveRequests();
		return ResponseEntity.ok(BaseResponse.<List<LeaveRequest>>builder()
		                                     .code(200)
		                                     .success(true)
		                                     .data(leaveRequests)
		                                     .message("Bekleyen izin talepleri getirildi.")
		                                     .build());
	}
	
	// İzin talebini kabul etme (Yönetici tarafından)
	@PreAuthorize("hasRole('COMPANY_ADMIN')")
	@PutMapping(ACCEPTLEAVE)
	public ResponseEntity<BaseResponse<LeaveRequest>> acceptLeaveRequest(@PathVariable Long managerId, @PathVariable Long employeeId) {
		try {
			LeaveRequest acceptedLeaveRequest = leaveService.acceptLeaveRequest(managerId, employeeId);
			return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
			                                     .code(200)
			                                     .success(true)
			                                     .data(acceptedLeaveRequest)
			                                     .message("İzin talebi başarıyla kabul edildi.")
			                                     .build());
		} catch (EntityNotFoundException | SecurityException e) {
			return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
			                                     .code(400)
			                                     .success(false)
			                                     .message(e.getMessage())
			                                     .build());
		}
	}
	
	// İzin talebini reddetme (Yönetici tarafından)
	@PreAuthorize("hasRole('COMPANY_ADMIN')")
	@PutMapping(REJECTLEAVE)
	public ResponseEntity<BaseResponse<LeaveRequest>> rejectLeaveRequest(@PathVariable Long managerId, @PathVariable Long employeeId) {
		try {
			LeaveRequest rejectedLeaveRequest = leaveService.rejectLeaveRequest(managerId, employeeId);
			return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
			                                     .code(200)
			                                     .success(true)
			                                     .data(rejectedLeaveRequest)
			                                     .message("İzin talebi başarıyla reddedildi.")
			                                     .build());
		} catch (EntityNotFoundException | SecurityException e) {
			return ResponseEntity.ok(BaseResponse.<LeaveRequest>builder()
			                                     .code(400)
			                                     .success(false)
			                                     .message(e.getMessage())
			                                     .build());
		}
	}
	
	
}