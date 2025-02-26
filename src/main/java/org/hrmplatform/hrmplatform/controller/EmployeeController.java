package org.hrmplatform.hrmplatform.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.EmployeeRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Employee;
import org.hrmplatform.hrmplatform.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(EMPLOYEE)
@AllArgsConstructor
@CrossOrigin("*")
public class EmployeeController {
        private final EmployeeService employeeService;

    /**
     * Tüm çalışanları getirir. (Sadece ADMIN)
     */
    @GetMapping(GET_ALL_EMPLOYEES)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<List<Employee>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Employee> employees = employeeService.getAllEmployees(page, size);

        if (employees.isEmpty()) {
            return ResponseEntity.ok(new BaseResponse<>(false, "No employees found", 404, employees));
        }

        return ResponseEntity.ok(new BaseResponse<>(true, "Employees retrieved successfully", 200, employees));
    }

    /**
     * Yeni bir çalışan ekler. (Sadece ADMIN)
     */
    @PostMapping(CREATE_EMPLOYEE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<Employee>> createEmployee(@RequestBody @Valid EmployeeRequestDto dto) {
        Employee createdEmployee = employeeService.createEmployee(dto);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Employee created successfully",
                201,
                createdEmployee));
    }

    /**
     * Var olan bir çalışanı günceller. (Sadece ADMIN)
     */
    @PutMapping(UPDATE_EMPLOYEE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<Employee>> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(new BaseResponse<>(true, "Employee updated successfully", 200, updatedEmployee));
    }

    /**
     * Çalışanı siler. (Sadece ADMIN)
     */
    @DeleteMapping(DELETE_EMPLOYEE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new BaseResponse<>(true, "Employee deleted successfully", 200, null));
    }

    /**
     * Çalışanın aktif/pasif durumunu değiştirir. (Sadece ADMIN)
     */
    @PutMapping(CHANGE_EMPLOYEE_STATUS)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<Employee>> changeEmployeeStatus(@PathVariable Long id) {
        Employee employee = employeeService.changeEmployeeStatus(id);
        return ResponseEntity.ok(new BaseResponse<>(true, "Employee status updated successfully", 200, employee));
    }
}
