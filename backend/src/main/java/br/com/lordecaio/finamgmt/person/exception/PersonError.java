package br.com.lordecaio.finamgmt.person.exception;

import org.springframework.http.HttpStatus;

public enum PersonError {
	PERSON_ALREADY_EXISTS(HttpStatus.CONFLICT),
	PERSON_NOT_FOUND(HttpStatus.NOT_FOUND);

	private final HttpStatus status;

	PersonError(HttpStatus status) { this.status = status; }

	public HttpStatus getStatus() {
		return status;
	}
}
