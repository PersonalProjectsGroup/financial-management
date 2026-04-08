package br.com.lordecaio.finamgmt.person.exception;

import br.com.lordecaio.finamgmt.common.exception.BusinessMessage;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class PersonExceptionHandler {

	@ExceptionHandler(PersonException.class)
	public ResponseEntity<BusinessMessage> handlePersonException(PersonException ex) {
		var err = ex.getError();
		var businessMessage = BusinessMessage.from(err.name(), ex.getMessage());
		return ResponseEntity.status(err.getStatus())
				.body(businessMessage);
	}

}
