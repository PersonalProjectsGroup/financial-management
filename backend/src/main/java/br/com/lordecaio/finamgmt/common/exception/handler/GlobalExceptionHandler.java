package br.com.lordecaio.finamgmt.common.exception.handler;

import br.com.lordecaio.finamgmt.common.exception.BusinessException;
import br.com.lordecaio.finamgmt.common.exception.BusinessMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
	public BusinessMessage handleBusinessException(BusinessException ex) {
		log.warn("Business rule violation: {}", ex.getMessage());
		return BusinessMessage.from("BUSINESS_RULE_VIOLATION", ex.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public BusinessMessage handleRuntimeException(RuntimeException ex) {
		log.error("Unhandled runtime exception: ", ex);
		return BusinessMessage.from("RUNTIME_ERROR", "An internal operational error occurred.");
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public BusinessMessage handleGeneralException(Exception ex) {
		log.error("Critical exception captured: ", ex);
		return BusinessMessage.from("CRITICAL_SERVER_ERROR", "An unexpected system failure occurred.");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public BusinessMessage handleValidationException(MethodArgumentNotValidException ex) {
		log.warn("Validation failed for request: {}", ex.getObjectName());

		BusinessMessage response = BusinessMessage.from("VALIDATION_FAILED", "One or more fields are invalid.");

		ex.getBindingResult().getFieldErrors().forEach(error ->
			  response.addDetail(error.getField(), error.getDefaultMessage())
		);

		return response;
	}
}