package com.cz.platform.security;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.cz.platform.exception.AuthenticationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.exception.ValidationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {

	private static final String AUTH = "auth";

	@Value("${security.jwt.token.secret-key:secret-key}")
	private String secretKey;

	@Autowired
	private MyUserDetails myUserDetails;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String createToken(String username, List<Role> roles) {

		Claims claims = Jwts.claims().setSubject(username);
		claims.put(AUTH, roles);

		Date now = new Date();

		return Jwts.builder()//
				.setClaims(claims)//
				.setIssuedAt(now)//
				.signWith(SignatureAlgorithm.HS256, secretKey)//
				.compact();
	}

	public Authentication getAuthentication(String token) {
		String userName = getUsername(token);
		log.debug("user called : {}", userName);
		UserDetails userDetails = myUserDetails.loadUserByUsername(userName);
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public Authentication getServerAuthentication(String token) {
		String userName = getUsername(token);
		log.debug("server called : {}", userName);
		Jws<Claims> claimsWrapped = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
		Claims claims = claimsWrapped.getBody();
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (String authority : (List<String>) claims.get(AUTH)) {
			authorities.add(new SimpleGrantedAuthority(authority));
		}
		log.info("authories user having: {}", authorities);
		UserDetails userDetails = org.springframework.security.core.userdetails.User//
				.withUsername(userName)//
				.password("")//
				.authorities(authorities)//
				.accountExpired(false)//
				.accountLocked(false)//
				.credentialsExpired(false)//
				.disabled(false)//
				.build();
		return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
	}

	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateClientToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid auth creds");
		}
	}

	public boolean validateServerToken(String serverSideToken) {
		try {
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new AuthenticationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid auth creds");
		}
	}

}