package br.com.lordecaio.finamgmt.person.model.response;

import br.com.lordecaio.finamgmt.common.enums.EntityStatus;
import br.com.lordecaio.finamgmt.person.model.Person;

import java.util.Objects;
import java.util.UUID;

public final class PublicPerson {

	private UUID resourceId;
	private String firstName;
	private String lastName;
	private EntityStatus status;

	private PublicPerson() {}

	public static PublicPerson from(Person person) {
		return new PublicPerson()
			  .withResourceId(person.getResourceId())
			  .withFirstName(person.getFirstName())
			  .withLastName(person.getLastName())
			  .withStatus(person.getStatus());
	}

	public UUID getResourceId() {
		return resourceId;
	}

	public PublicPerson withResourceId(UUID resourceId) {
		this.resourceId = resourceId;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public PublicPerson withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public PublicPerson withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public EntityStatus getStatus() {
		return status;
	}

	public PublicPerson withStatus(EntityStatus status) {
		this.status = status;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) { return false; }
		PublicPerson that = (PublicPerson) o;
		return Objects.equals(resourceId, that.resourceId) && Objects.equals(firstName, that.firstName)
			  && Objects.equals(lastName, that.lastName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(resourceId, firstName, lastName);
	}
}
