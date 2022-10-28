package com.cz.platform.security;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

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
import com.cz.platform.enums.UserType;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.AuthenticationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.cz.platform.utility.CommonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

	private String secretKey;

	@Autowired
	private RestTemplate template;

	@Autowired
	private ObjectMapper mapper;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(props.getJwtSecretKey().getBytes());
	}

	public String createToken(String clientId, List<String> roles) {

		Claims claims = Jwts.claims().setSubject(clientId);
		claims.put(AUTH, roles);

		Date now = new Date();

		return Jwts.builder()//
				.setClaims(claims)//
				.setIssuedAt(now)//
				.signWith(SignatureAlgorithm.HS256, secretKey)//
				.compact();
	}

	public Authentication getClientAuthentication(String token) throws ApplicationException {
		UserDTO user = validateClientToken(token);
		Set<Permission> permissions = CommonUtility.getPermissions(user.getRoles());
		return new UsernamePasswordAuthenticationToken(user, "", permissions);
	}

	private UserDTO validateClientToken(String token) throws ApplicationException {
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

	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public UserDTO validateServerToken(String token) throws ApplicationException {
		try {
			String userName = getUsername(token);
			log.debug("server called : {}, in token : {}", userName, token);
			Jws<Claims> claimsWrapped = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			Claims claims = claimsWrapped.getBody();

			@SuppressWarnings("unchecked")
			List<String> roles = (List<String>) claims.get(AUTH);

			LoggedInUser user = new LoggedInUser();
			user.setRoles(new ArrayList<>());
			user.setUserId(userName);
			user.setUserType(UserType.INTERNAL_SERVICE);

			RoleDTO roleDTO = new RoleDTO();
			roleDTO.setRoleId(PlatformConstants.DEFAULT_ROLE_ID);
			roleDTO.setPermissions(roles);
			user.getRoles().add(roleDTO);
			return user;
		} catch (JwtException | IllegalArgumentException e) {
			throw new AuthenticationException(PlatformExceptionCodes.AUTHENTICATION_CODE);
		} catch (Exception exeption) {
			log.error("error occured while validating token", exeption);
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR);
		}
	}

}