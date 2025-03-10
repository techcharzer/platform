package com.cz.platform.security;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cz.platform.PlatformConstants;
import com.cz.platform.exception.ApplicationException;
import com.cz.platform.exception.AuthenticationException;
import com.cz.platform.exception.PlatformExceptionCodes;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenFilter extends OncePerRequestFilter {

	private final static Logger log = LoggerFactory.getLogger(AuthTokenFilter.class);

	private AuthService authService;

	private ObjectMapper mapper;

	private String securePath;
	private String actuatorPath;

	public AuthTokenFilter(AuthService authService, ObjectMapper mapper, String contextPath) {
		this.authService = authService;
		this.mapper = mapper;
		this.actuatorPath = String.format("%s/actuator/", contextPath);
		this.securePath = String.format("%s/secure/", contextPath);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// Skip the filter for any request that is NOT /actuator/** or /secure/**
		String path = request.getRequestURI();
		boolean isAuthenticationMustBeApplied = path.startsWith(actuatorPath) || path.startsWith(securePath);
		log.trace("isAuthenticationMustBeApplied over request  {}: {}", path, isAuthenticationMustBeApplied);
		return !(isAuthenticationMustBeApplied);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		try {

			String clientToken = resolveAuthToken(httpServletRequest);
			log.debug("resolving token: {}", clientToken);
			if (!ObjectUtils.isEmpty(clientToken)) {
				Optional<String> chargePointOperatorId = resolveChargePointOperatorId(httpServletRequest);
				Authentication auth = authService.getClientAuthentication(clientToken, chargePointOperatorId);

				SecurityContextImpl secureContext = new SecurityContextImpl();
				secureContext.setAuthentication(auth);
				SecurityContextHolder.setContext(secureContext);
				log.debug("client token: {}", clientToken);
			} else {
				String serverSideToken = httpServletRequest.getHeader(PlatformConstants.SSO_TOKEN_HEADER);
				if (ObjectUtils.isEmpty(serverSideToken)) {
					// fallback to request parameters for parsing the token.
					// added for only google schedule task
					serverSideToken = httpServletRequest.getParameter(PlatformConstants.SSO_TOKEN_HEADER);
				}
				if (!ObjectUtils.isEmpty(serverSideToken)) {
					Authentication auth = authService.getServerAuthentication(serverSideToken);
					SecurityContextImpl secureContext = new SecurityContextImpl();
					secureContext.setAuthentication(auth);
					SecurityContextHolder.setContext(secureContext);
					log.debug("server token: {}", serverSideToken);
				} else {
					log.debug("no token");
					throw new AuthenticationException(PlatformExceptionCodes.ACCESS_DENIED.getCode(),
							"No auth keys present.");
				}
			}
		} catch (AuthenticationException ex) {
			log.warn("auth exception occured in token filter", ex);
			// since it guarantees the user is not authenticated at all
			SecurityContextHolder.clearContext();
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
			httpServletResponse.getWriter().write(mapper.writeValueAsString(ex.getError()));
			return;
		} catch (ApplicationException e) {
			log.warn("some exception occured in token filter", e);
			SecurityContextHolder.clearContext();
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
			httpServletResponse.getWriter().write(mapper.writeValueAsString(e.getError()));
			return;
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	public String resolveAuthToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public Optional<String> resolveChargePointOperatorId(HttpServletRequest req) {
		String cpoId = req.getHeader("cpo-id");
		if (ObjectUtils.isEmpty(cpoId)) {
			return Optional.empty();
		}
		return Optional.of(cpoId);
	}

}