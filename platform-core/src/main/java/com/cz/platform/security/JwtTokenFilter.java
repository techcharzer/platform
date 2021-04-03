package com.cz.platform.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cz.platform.exception.AuthenticationException;

public class JwtTokenFilter extends OncePerRequestFilter {

	private JwtTokenProvider jwtTokenProvider;

	public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		try {
			String clientToken = resolveAuthToken(httpServletRequest);
			if (!ObjectUtils.isEmpty(clientToken) && jwtTokenProvider.validateClientToken(clientToken)) {
				Authentication auth = jwtTokenProvider.getAuthentication(clientToken);
				SecurityContextHolder.getContext().setAuthentication(auth);
			} else {
				String serverSideToken = httpServletRequest.getHeader("x-ss-token");
				if (!ObjectUtils.isEmpty(serverSideToken)) {
					Authentication auth = jwtTokenProvider.getServerAuthentication(serverSideToken);
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		} catch (AuthenticationException ex) {
			// since it guarantees the user is not authenticated at all
			SecurityContextHolder.clearContext();
			httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
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