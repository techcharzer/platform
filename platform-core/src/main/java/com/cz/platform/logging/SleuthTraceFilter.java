package com.cz.platform.logging;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.cz.platform.PlatformConstants;

import brave.Span;
import brave.Tracer;

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