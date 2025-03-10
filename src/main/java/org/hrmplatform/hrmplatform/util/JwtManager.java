package org.hrmplatform.hrmplatform.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.hrmplatform.hrmplatform.dto.response.TokenValidationResult;
import org.hrmplatform.hrmplatform.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class JwtManager {
	/**
	 * Token oluşturmak için gerekli parametreler
	 * SecretKey -> token imzalamak için gerekli şifre
	 * Issuer -> jwt token sahibine ait bilgi
	 * IssuerAt -> token üretilme zamanı
	 * ExpiresAt -> token geçerlilik son zamanı, bitiş anı
	 * Claim -> içerisinde KEY-VALUE şeklinde değer saklayan nesneler.
	 * NOT!!!
	 * claim nesneleri içerisinde bulunan değerler açık olarak tutulur bu nedenle
	 * gizli kalması gereken değerleri buraya eklemeyiniz.
	 * Sign -> imzalama için kullanılır mutlaka bir şifreleme algoritması vermek gerekir.
	 *
	 * []  [] -> sadece rakam ->
	 * 10  10 -> 100
	 * 50  50 -> 2.500
	 * [] [] [] [] [] [] [] [] -> 50^8 -> 1_953_125_000_000_000
	 * 50^7 -> 1sn
	 * 50^43 sn
	 */
	@Value("${hrmplatform.jwt.secret-key}")
	private String SecretKey;
	@Value("${hrmplatform.jwt.issuer}")
	private String Issuer;
	private final Long ExDate = 1000L * 60 * 30; // 5dk sonra iptal olsun
	
	public String createToken(Long authId, String email, Role role, Long companyId, Boolean activated, Boolean status){
		// Breakpoint buraya koyun
		if (companyId == null) {
			log.warn("companyId is null for user: {}", authId);
		}
		Date createdDate = new Date(System.currentTimeMillis());
		Date expirationDate = new Date(System.currentTimeMillis() + ExDate);
		Algorithm algorithm = Algorithm.HMAC512(SecretKey);
		String token = JWT.create()
				.withAudience()
				.withIssuer(Issuer)
				.withIssuedAt(createdDate)
				.withExpiresAt(expirationDate)
				.withClaim("authId", authId)
				.withClaim("email", email)
				.withClaim("role", role.name())
				.withClaim("companyId", companyId)
				.withClaim("activated", activated)
				.withClaim("status", status)
				.withClaim("key", "JX_15_TJJJ")
				.sign(algorithm);
		return token;
	}
	
	public Optional<TokenValidationResult> validateToken(String token){
		// Breakpoint buraya koyun
		if (token == null || token.isEmpty()) {
			log.warn("Token is null or empty");
			return Optional.empty();
		}
		try {
			Algorithm algorithm = Algorithm.HMAC512(SecretKey);
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT decodedJWT = verifier.verify(token); // Bu token bize mi ait

			if (Objects.isNull(decodedJWT)) // Eğer Token doğrulanamaz ise null döner bizde empty olarak return ederiz.
				return Optional.empty();

			Long authId = decodedJWT.getClaim("authId").asLong();
			Long companyId = decodedJWT.getClaim("companyId").asLong(); // companyId bilgisini al

			// Eğer companyId claim'i yoksa veya null ise, companyId'yi null olarak ayarla
			if (decodedJWT.getClaim("companyId").isNull()) {
				companyId = null;
			}
			// Breakpoint buraya koyun
			log.info("Decoded token - authId: {}, companyId: {}", authId, companyId);
			// TokenValidationResult DTO'sunu oluştur ve dön
			return Optional.of(new TokenValidationResult(authId, companyId));
		} catch (Exception exception) {
			return Optional.empty();
		}
	}
	
}