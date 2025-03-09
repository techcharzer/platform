package com.cz.platform.logging;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.cz.platform.PlatformConstants;

import brave.Span;
import brave.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(value = Ordered.LOWEST_PRECEDENCE)
class SleuthTraceFilter extends GenericFilterBean {

	private final Tracer tracer;

	SleuthTraceFilter(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Span currentSpan = this.tracer.currentSpan();
		if (currentSpan == null) {
			chain.doFilter(request, response);
			return;
		}
		// for readability we're returning trace id in a hex form
		((HttpServletResponse) response).addHeader(PlatformConstants.X_TRACE_ID, currentSpan.context().traceIdString());
		chain.doFilter(request, response);
	}

}