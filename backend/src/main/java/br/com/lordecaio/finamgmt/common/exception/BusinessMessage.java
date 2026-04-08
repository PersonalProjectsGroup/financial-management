package br.com.lordecaio.finamgmt.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.lordecaio.finamgmt.common.filter.MDCFilter.TRACE_ID_KEY;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class BusinessMessage {
	private String code;
	private String message;
	private final String traceId;
	private final Map<String, List<String>> details;
	private final LocalDateTime timestamp = LocalDateTime.now();

	private BusinessMessage() {
		details = new HashMap<>();

		String currentTraceId = MDC.get(TRACE_ID_KEY);
		if (currentTraceId == null || currentTraceId.isEmpty()) {
			currentTraceId = "N/A";
		}
		this.traceId = currentTraceId;
	}

	public static BusinessMessage from(String code, String message) {
		return new BusinessMessage().withCode(code).withMessage(message);
	}

	public String getCode() {
		return code;
	}

	public BusinessMessage withCode(String code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public BusinessMessage withMessage(String message) {
		this.message = message;
		return this;
	}

	public String getTraceId() {
		return traceId;
	}

	public BusinessMessage addDetail(String key, String value) {
		details.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
		return this;
	}

	public Map<String, List<String>> getDetails() {
		return Collections.unmodifiableMap(details);
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
