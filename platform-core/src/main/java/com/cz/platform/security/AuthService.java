package com.cz.platform.security;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cz.platform.PlatformConstants;
import com.cz.platform.clients.UrlConfig;
import com.cz.platform.enums.LogInFrom;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.AuthenticationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.utility.CommonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthService {

	private static final String AUTH = "auth";

	@Autowired
	private SecurityConfigProps props;

	@Autowired
	private UrlConfig urlConfig;

	@Deprecated
	private String oldSecretKey;
	private SecretKey secretKey;

	@Autowired
	private RestTemplate template;

	@Autowired
	private ObjectMapper mapper;

	@PostConstruct
	protected void init() {
		oldSecretKey = Base64.getEncoder().encodeToString(props.getJwtSecretKey().getBytes());
		secretKey = Keys.hmacShaKeyFor(props.getNewJwtSecretKey().getBytes());
	}

	public String createToken(String clientId, List<String> roles) {
		Instant now = Instant.now();
		return Jwts.builder().subject(clientId).claim(AUTH, roles).issuedAt(Date.from(now)).signWith(secretKey)
				.compact();
	}

	public Authentication getClientAuthentication(String token, Optional<String> chargePointOperatorId)
			throws ApplicationException {
		LoggedInUser user = validateClientToken(token);
		if (ObjectUtils.isEmpty(user.getChargePointOperatorId()) && chargePointOperatorId.isPresent()) {
			user.setChargePointOperatorId(chargePointOperatorId.get());
		}
		Set<Permission> permissions = CommonUtility.getPermissions(user.getRoles());
		return new UsernamePasswordAuthenticationToken(user, "", permissions);
	}

	private LoggedInUser validateClientToken(String token) throws ApplicationException {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		TokenRequest requets = new TokenRequest(token);
		HttpEntity<TokenRequest> entity = new HttpEntity<>(requets, headers);
		try {
			String url = MessageFormat.format("{0}/auth-service/validate-token/", urlConfig.getBaseUrl());
			log.trace("url: {} token request : {} headers : {}", url, requets, headers);
			HttpEntity<JsonNode> response = template.exchange(url, HttpMethod.POST, entity, JsonNode.class);
			log.trace("response from the server : {}", response.getBody());
			return mapper.convertValue(response.getBody(), LoggedInUser.class);
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from  the server : {}", exeption.getResponseBodyAsString());
			throw new AuthenticationException(PlatformExceptionCodes.AUTHENTICATION_CODE);
		} catch (Exception exeption) {
			log.error("error occured while validating token", exeption);
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR);
		}

	}

	@Data
	@AllArgsConstructor
	private class TokenRequest {
		private String token;
	}

	public Authentication getServerAuthentication(String token) throws ApplicationException {
		UserDTO user = validateServerToken(token);
		Set<Permission> permissions = CommonUtility.getPermissions(user.getRoles());
		return new UsernamePasswordAuthenticationToken(user, "", permissions);
	}

	private UserDTO validateServerToken(String token) throws ApplicationException {
		Jws<Claims> claimsWrapped = validateViaNewSecretKey(token);
		if (claimsWrapped == null) {
			claimsWrapped = validateOldSecretKey(token);
		}
		if (claimsWrapped == null) {
			throw new AuthenticationException(PlatformExceptionCodes.AUTHENTICATION_CODE);
		}
		Claims claims = claimsWrapped.getPayload();
		String userName = claims.getSubject();
		log.debug("server called : {}", userName);

		@SuppressWarnings("unchecked")
		List<String> roles = (List<String>) claims.get(AUTH);
		LoggedInUser user = new LoggedInUser();
		user.setRoles(new ArrayList<>());
		user.setUserId(userName);
		user.setLogInFrom(LogInFrom.INTERNAL_SERVICE);

		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setRoleId(PlatformConstants.DEFAULT_ROLE_ID);
		roleDTO.setPermissions(roles);
		user.getRoles().add(roleDTO);
		return user;
	}

	private Jws<Claims> validateViaNewSecretKey(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
			log.debug("validated through new secretKey");
			return claims;
		} catch (Exception e) {
			log.debug("new validation failed: {}", e);
			return null;
		}
	}

	private Jws<Claims> validateOldSecretKey(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(oldSecretKey).build().parseClaimsJws(token);
			log.debug("validated through old secretKey");
			return claims;
		} catch (Exception exeption) {
			log.error("error occured while validating old token", exeption);
			return null;
		}
	}

}