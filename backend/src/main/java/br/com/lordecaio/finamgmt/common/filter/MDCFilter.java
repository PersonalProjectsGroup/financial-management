package br.com.lordecaio.finamgmt.common.filter;

import br.com.lordecaio.finamgmt.common.util.UUIDUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MDCFilter extends OncePerRequestFilter {

	public static final String TRACE_ID_KEY = "traceId";
	public static final String TRACE_ID_HEADER = "X-Trace-Id";

	protected static final String TRACE_ID_PATTERN = "T3E-%s";

	@Override
	protected void doFilterInternal(
		  HttpServletRequest request,
		  @NonNull HttpServletResponse response,
		  @NonNull FilterChain filterChain
	)
		  throws ServletException, IOException {
		try {
			String traceId = request.getHeader(TRACE_ID_HEADER);
			if (traceId == null || traceId.isEmpty()) {
				traceId = this.generateTraceId();
			}

			MDC.put(TRACE_ID_KEY, traceId);
			response.addHeader(TRACE_ID_HEADER, traceId);

			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(TRACE_ID_KEY);
		}
	}

	private String generateTraceId() {
		var uuid = UUIDUtils.nextV7().toString().replace("-", "");
		return String.format(TRACE_ID_PATTERN, uuid);
	}
}
