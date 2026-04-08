package br.com.lordecaio.finamgmt.person.exception;

import br.com.lordecaio.finamgmt.common.exception.BusinessMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PersonExceptionHandlerTest {

    private PersonExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PersonExceptionHandler();
    }

    @Test
    void handlePersonException_shouldReturnNotFoundWhenErrorIsPersonNotFound() {
        String message = "Person not found!";
        PersonException ex = PersonException.from(PersonError.PERSON_NOT_FOUND, message);

        ResponseEntity<BusinessMessage> response = handler.handlePersonException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PERSON_NOT_FOUND", response.getBody().getCode());
        assertEquals(message, response.getBody().getMessage());
    }

    @Test
    void handlePersonException_shouldReturnConflictWhenErrorIsPersonAlreadyExists() {
        String message = "Person already exists!";
        PersonException ex = PersonException.from(PersonError.PERSON_ALREADY_EXISTS, message);

        ResponseEntity<BusinessMessage> response = handler.handlePersonException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PERSON_ALREADY_EXISTS", response.getBody().getCode());
        assertEquals(message, response.getBody().getMessage());
    }
}
