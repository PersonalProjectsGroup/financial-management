package br.com.lordecaio.finamgmt.person.exception;

import br.com.lordecaio.finamgmt.common.exception.BusinessException;

public class PersonException extends BusinessException {

	private final PersonError error;

	private PersonException(PersonError error, String message) {
		super(message);
		this.error = error;
	}

	public static PersonException from(PersonError error, String message) {
		return new PersonException(error, message);
	}

	public PersonError getError() {
		return error;
	}
}
