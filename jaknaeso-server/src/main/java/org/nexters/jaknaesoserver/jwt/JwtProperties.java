package org.nexters.jaknaesoserver.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private final String secret;
    private final Long accessTokenExpiration;
    private final Long refreshTokenExpiration;
    private final SecretKey secretKey;

    public JwtProperties(String secret, Long accessTokenExpiration, Long refreshTokenExpiration) {
        this.secret = secret;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }
}
