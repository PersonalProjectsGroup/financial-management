package br.com.lordecaio.finamgmt.person.model;

import br.com.lordecaio.finamgmt.common.annotation.ResourceId;
import br.com.lordecaio.finamgmt.common.annotation.listener.ResourceIdListener;
import br.com.lordecaio.finamgmt.common.model.DomainEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(
	  name = Person.TABLE_NAME,
	  indexes = {
			@Index(name = Person.INDEX_PROFILE_RESOURCE_ID, columnList = Person.COLUMN_RESOURCE_ID, unique = true)
	  })
@EntityListeners(ResourceIdListener.class)
public class Person extends DomainEntity<Person> {

	public static final String TABLE_NAME = "person";
	public static final String COLUMN_ID = "profile_id";
	public static final String COLUMN_RESOURCE_ID = "resource_id";
	public static final String COLUMN_FIRST_NAME = "first_name";
	public static final String COLUMN_LAST_NAME = "last_name";

	public static final String INDEX_PROFILE_RESOURCE_ID = "idx_profile_resource_id";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = COLUMN_ID)
	private Long id;

	@ResourceId
	@Column(name = COLUMN_RESOURCE_ID)
	private UUID resourceId;

	@Column(name = COLUMN_FIRST_NAME, nullable = false)
	private String firstName;

	@Column(name = COLUMN_LAST_NAME, nullable = false)
	private String lastName;

	protected Person() {
		super();
	}

	public static Person empty() {
		return new Person();
	}

	public static Person from(String firstName, String lastName) {
		return new Person().withFirstName(firstName).withLastName(lastName);
	}

	public Long getId() {
		return id;
	}

	public Person withId(Long id) {
		this.id = id;
		return this;
	}

	public UUID getResourceId() {
		return resourceId;
	}

	public Person withResourceId(UUID resourceId) {
		this.resourceId = resourceId;
		return this;
	}

	public String getFirstName() {
		return Optional.ofNullable(firstName).orElse("");
	}

	public Person withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return Optional.ofNullable(lastName).orElse("");
	}

	public Person withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
}
