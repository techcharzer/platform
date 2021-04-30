package com.cz.platform.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthTokenFilter extends OncePerRequestFilter {

	private AuthService authService;

	private ObjectMapper mapper;

	public AuthTokenFilter(AuthService authService, ObjectMapper mapper) {
		this.authService = authService;
		this.mapper = mapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		try {
			String clientToken = resolveAuthToken(httpServletRequest);
			if (!ObjectUtils.isEmpty(clientToken)) {
				Authentication auth = authService.getClientAuthentication(clientToken);
				SecurityContextImpl secureContext = new SecurityContextImpl();
				secureContext.setAuthentication(auth);
				SecurityContextHolder.setContext(secureContext);
			} else {
				String serverSideToken = httpServletRequest.getHeader(PlatformConstants.SSO_TOKEN_HEADER);
				if (!ObjectUtils.isEmpty(serverSideToken)) {
					Authentication auth = authService.getServerAuthentication(serverSideToken);
					SecurityContextImpl secureContext = new SecurityContextImpl();
					secureContext.setAuthentication(auth);
					SecurityContextHolder.setContext(secureContext);
				}
			}
		} catch (AuthenticationException ex) {
			// since it guarantees the user is not authenticated at all
			SecurityContextHolder.clearContext();
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
			httpServletResponse.getWriter().write(mapper.writeValueAsString(ex.getError()));
			return;
		} catch (ApplicationException e) {
			SecurityContextHolder.clearContext();
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
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

}