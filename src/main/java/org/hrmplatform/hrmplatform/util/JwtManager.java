package org.hrmplatform.hrmplatform.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;


import org.hrmplatform.hrmplatform.exception.CustomErrorType;
import org.hrmplatform.hrmplatform.exception.InvalidArgumentException;
import org.hrmplatform.hrmplatform.exception.InvalidJWTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * -> com.auth0.jwt.JWT: JWT oluşturma ve doğrulama işlemleri için kullanılan Auth0 JWT kütüphanesinin JWT sınıfını
 * dahil eder.
 *-> JWTVerifier: JWT token'ını doğrulamak için kullanılan sınıf.
 * -> Algorithm: JWT token'ını imzalamak için kullanılan algoritmalar (HMAC, RSA, vb.) sınıfıdır.
 * -> DecodedJWT: JWT token'ını çözmek (decode etmek) için kullanılan sınıf.
 */
@Service
public class JwtManager {
	
	@Value("${HRM_JWT_SECRETKEY}")
	private String SECRETKEY;
	@Value("${HRM_JWT_ISSUER}")
	private String ISSUER;
	private final Long EXPIRATION_TIME = 1000L * 60*20;
	
	
	
	public String createJWT(Long hrmplatformId) {
		Algorithm algorithm = Algorithm.HMAC512(SECRETKEY);
		Date creationDate = new Date(System.currentTimeMillis());
		Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
		String token =
				JWT.create().withAudience()
				   .withIssuer(ISSUER)
				   .withIssuedAt(creationDate)
				   .withExpiresAt(expirationDate)
				   .withClaim("hrmplatformId", hrmplatformId)
				   .withClaim("key", "value123")
				   .sign(algorithm);
		return token;
	}
	
	public Optional<Long> validateJWT(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC512(SECRETKEY);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
			DecodedJWT decodedJWT = verifier.verify(token);
			
			
			Long hrmplatformId = decodedJWT.getClaim("hrmplatformId").asLong();
			
			if (Objects.isNull(hrmplatformId)) {
				throw new InvalidJWTException(CustomErrorType.HRMPLATFORM_ID_MISSING);
			}
			
			return Optional.of(hrmplatformId);
		}
		catch (IllegalArgumentException e) {
			throw new InvalidArgumentException(CustomErrorType.INVALID_ARGUMENT);
		}
		catch (JWTVerificationException e) {
			throw new InvalidJWTException(CustomErrorType.INVALID_JWT);
		}
		
	}
}