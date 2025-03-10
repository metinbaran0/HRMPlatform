package org.hrmplatform.hrmplatform.service;


import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.response.TokenValidationResult;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.util.JwtManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final JwtManager jwtManager;
    private final CompanyService companyService;

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

    public Company getCompanyFromToken(String token) {
        Long companyId = getCompanyIdFromToken(token);
        return companyService.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));
    }
}
