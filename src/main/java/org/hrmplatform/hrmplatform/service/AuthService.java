package org.hrmplatform.hrmplatform.service;

import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.response.TokenValidationResult;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.entity.Employee;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;
import org.hrmplatform.hrmplatform.util.JwtManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final JwtManager jwtManager;
    private final CompanyService companyService;
    private final UserRoleService userRoleService;
    
    // Şirket ID'sini token'dan döndüren metot
    public Long getCompanyIdFromToken(String token) {
        Optional<TokenValidationResult> tokenValidationResult = jwtManager.validateToken(token.replace("Bearer ", ""));
        if (tokenValidationResult.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz token");
        }
        
        Long companyId = tokenValidationResult.get().companyId();
        
        if (companyId == null) {
            throw new IllegalArgumentException("Bu işlemi gerçekleştirmek için bir şirkete ait olmalısınız");
        }
        
        return companyId;
    }
    
    // Şirket objesini token'dan döndüren metot
    public Company getCompanyFromToken(String token) {
        Long companyId = getCompanyIdFromToken(token);
        return companyService.findByCompanyId(companyId)
                             .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));
    }
    
    // Çalışan ID'sini token'dan döndüren metot
    public Long getEmployeeIdFromToken(String token) {
        Optional<TokenValidationResult> tokenValidationResult = jwtManager.validateToken(token.replace("Bearer ", ""));
        if (tokenValidationResult.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz token");
        }
        
        Long employeeId =
                tokenValidationResult.get().employeeId(); // TokenValidationResult içinde employeeId'yi alıyoruz
        
        if (employeeId == null) {
            throw new IllegalArgumentException("Bu işlemi gerçekleştirmek için bir çalışana ait olmalısınız");
        }
        
        return employeeId;
    }
    
    // Kullanıcının şirket yöneticisi olup olmadığını kontrol eden metot
    public boolean isCompanyAdmin(String token) {
        Long employeeId = getEmployeeIdFromToken(token);
        
        List<UserRole> userRoles = userRoleService.findRoleByUserId(employeeId);
        
        for (UserRole role : userRoles) {
            if (role.getRole() == Role.COMPANY_ADMIN) {
                return true;
            }
        }
        return false;
    }
}