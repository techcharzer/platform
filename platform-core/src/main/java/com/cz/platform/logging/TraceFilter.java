package com.cz.platform.logging;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.cz.platform.PlatformConstants;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(value = Ordered.LOWEST_PRECEDENCE)
class TraceFilter extends GenericFilterBean {

	private final Tracer tracer;

	TraceFilter(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.info("trace filter configured");
		TraceContext traceContext = this.tracer.currentTraceContext().context();
		if (traceContext != null) {
			// Add trace ID to response headers
			((HttpServletResponse) response).addHeader(PlatformConstants.X_TRACE_ID, traceContext.traceId());
		}
		chain.doFilter(request, response);
	}
}
