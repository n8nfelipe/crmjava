package com.acme.crm.security;

import com.acme.crm.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final AppProperties props;
  private final SecretKey key;

  public JwtService(AppProperties props) {
    this.props = props;
    byte[] raw = props.jwt().secret().getBytes(StandardCharsets.UTF_8);
    if (raw.length < 32)
      throw new IllegalStateException("APP_JWT_SECRET deve ter pelo menos 32 bytes");
    this.key = Keys.hmacShaKeyFor(raw);
  }

  public String create(UserPrincipal u) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(u.getUsername())
        .claim("uid", u.id().toString())
        .claim("name", u.name())
        .claim("roles", u.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(props.jwt().accessTtl())))
        .signWith(key)
        .compact();
  }

  public String subject(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
  }

  public long expiresIn() {
    return props.jwt().accessTtl().toSeconds();
  }
}
