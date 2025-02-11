package org.hrmplatform.hrmplatform.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class JwtManager {
	
	
	
		Date creationDate = new Date(System.currentTimeMillis());
		          .withIssuedAt(creationDate)
		          .withExpiresAt(expirationDate)
		          .sign(algorithm);
	}
	
		try {
			DecodedJWT decodedJWT = verifier.verify(token);
			
			}
			
		}
	}
}