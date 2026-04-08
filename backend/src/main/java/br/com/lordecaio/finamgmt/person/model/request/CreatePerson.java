package br.com.lordecaio.finamgmt.person.model.request;

import br.com.lordecaio.finamgmt.person.model.Person;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreatePerson(
	  @NotBlank(message = "first name is required")
	  @Size(min = 1, max = 128, message = "first name must have between 1 and 128 characters")
	  String firstName,

	  @NotBlank(message = "last name is required")
	  @Size(min = 1, max = 128, message = "last name must have between 1 and 128 characters")
	  String lastName
) {
	public Person toEntity() {
		return Person.from(firstName, lastName);
	}
}
