package br.com.lordecaio.finamgmt.common.exception.handler;

import br.com.lordecaio.finamgmt.common.exception.BusinessException;
import br.com.lordecaio.finamgmt.common.exception.BusinessMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Test
	void handleBusinessException_shouldReturnBusinessMessageWithUnprocessableContentStatus() {
		String errorMessage = "Business rule violated!";
		BusinessException ex = new BusinessException(errorMessage);

		BusinessMessage result = handler.handleBusinessException(ex);

		assertNotNull(result);
		assertEquals("BUSINESS_RULE_VIOLATION", result.getCode());
		assertEquals(errorMessage, result.getMessage());
	}

	@Test
	void handleRuntimeException_shouldReturnBusinessMessageWithInternalServerErrorStatus() {
		String errorMessage = "Something unexpected happened!";
		RuntimeException ex = new RuntimeException(errorMessage);

		BusinessMessage result = handler.handleRuntimeException(ex);

		assertNotNull(result);
		assertEquals("RUNTIME_ERROR", result.getCode());
		assertEquals("An internal operational error occurred.", result.getMessage());
	}

	@Test
	void handleGeneralException_shouldReturnBusinessMessageWithInternalServerErrorStatus() {
		String errorMessage = "A critical error occurred!";
		Exception ex = new Exception(errorMessage);

		BusinessMessage result = handler.handleGeneralException(ex);

		assertNotNull(result);
		assertEquals("CRITICAL_SERVER_ERROR", result.getCode());
		assertEquals("An unexpected system failure occurred.", result.getMessage());
	}

	@Test
	void handleValidationException_shouldReturnBusinessMessageWithBadRequestStatusAndDetails() {
		String objectName = "personDto";
		String fieldName = "name";
		String fieldMessage = "Name cannot be empty";

		FieldError fieldError = new FieldError(objectName, fieldName, fieldMessage);

		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
		when(bindingResult.getObjectName()).thenReturn(objectName);

		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

		BusinessMessage result = handler.handleValidationException(ex);

		assertNotNull(result);
		assertEquals("VALIDATION_FAILED", result.getCode());
		assertEquals("One or more fields are invalid.", result.getMessage());
		assertNotNull(result.getDetails());
		assertEquals(1, result.getDetails().size());
		assertTrue(result.getDetails().get(fieldName).contains(fieldMessage));
	}

	@Test
	void handleValidationException_shouldReturnBusinessMessageWithBadRequestStatusAndNoDetailsIfNoFieldErrors() {
		String objectName = "personDto";

		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());
		when(bindingResult.getObjectName()).thenReturn(objectName);

		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

		BusinessMessage result = handler.handleValidationException(ex);

		assertNotNull(result);
		assertEquals("VALIDATION_FAILED", result.getCode());
		assertEquals("One or more fields are invalid.", result.getMessage());
		assertNotNull(result.getDetails());
		assertEquals(0, result.getDetails().size());
	}
}
