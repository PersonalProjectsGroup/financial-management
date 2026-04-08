package br.com.lordecaio.finamgmt.person;

import br.com.lordecaio.finamgmt.common.model.AuditableEntity;
import br.com.lordecaio.finamgmt.common.util.UUIDUtils;
import br.com.lordecaio.finamgmt.person.exception.PersonException;
import br.com.lordecaio.finamgmt.person.model.Person;
import br.com.lordecaio.finamgmt.person.model.request.CreatePerson;
import br.com.lordecaio.finamgmt.person.model.response.PublicPerson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

	@Mock
	private PersonRepository repository;

	@Spy
	@InjectMocks
	private PersonService service;

	private static Person person;
	private static CreatePerson createPerson;
	private static PublicPerson publicPerson;

	private final SecureRandom random = new SecureRandom();

	@BeforeEach
	void setUp() {
		createPerson = new CreatePerson("John", "Doe");
		person = createPerson.toEntity()
			  .withId(random.nextLong())
			  .withResourceId(UUIDUtils.nextV7());
		publicPerson = PublicPerson.from(person);
	}

	@AfterEach
	void tearDown() {
		person = null;
		createPerson = null;
		publicPerson = null;
	}

	@Test
	void should_Create() {
		when(repository.save(any(Person.class))).thenReturn(person);

		var result = service.create(createPerson);

		verify(repository).save(any(Person.class));
		assertNotNull(result);
		assertEquals(publicPerson, result);
	}

	@Test
	void should_FindByResourceId() {
		when(repository.findByResourceId(person.getResourceId())).thenReturn(Optional.of(person));

		var result = service.findByResourceId(person.getResourceId());

		verify(repository).findByResourceId(person.getResourceId());
		assertNotNull(result);
		assertEquals(publicPerson, result);
	}

	@Test
	void should_DeleteByResourceId() {
		markPersonAsNotNew(person);
		when(repository.findByResourceId(person.getResourceId())).thenReturn(Optional.of(person));
		when(repository.save(any(Person.class))).thenReturn(person);

		service.deleteByResourceId(person.getResourceId());

		assertTrue(person.isDeleted());
		assertNotNull(person.getDeletedAt());
		verify(repository).save(any(Person.class));
	}

	@Test
	void should_FindOrThrowByResourceId() {
		when(repository.findByResourceId(person.getResourceId())).thenReturn(Optional.of(person));

		var result = service.findOrThrowByResourceId(person.getResourceId());

		verify(repository).findByResourceId(person.getResourceId());
		assertNotNull(result);
		assertEquals(person, result);
	}

	@Test
	void shouldThrowWhen_FindByResourceId() {
		when(repository.findByResourceId(person.getResourceId())).thenReturn(Optional.empty());
		PersonException exception = assertThrows(PersonException.class, () ->
			  service.findByResourceId(person.getResourceId())
		);
		assertNotNull(exception);
		assertEquals("Person not found by resourceId: " + person.getResourceId(), exception.getMessage());
	}

	@Test
	void shouldThrowWhen_DeleteByResourceId() {
		when(repository.findByResourceId(person.getResourceId())).thenReturn(Optional.empty());
		PersonException exception = assertThrows(PersonException.class, () ->
			  service.deleteByResourceId(person.getResourceId())

		);
		assertNotNull(exception);
		assertEquals("Person not found by resourceId: " + person.getResourceId(), exception.getMessage());
	}

	@Test
	void shouldThrowWhen_FindOrThrowByResourceId() {
		when(repository.findByResourceId(person.getResourceId())).thenReturn(Optional.empty());
		PersonException exception = assertThrows(PersonException.class, () ->
			  service.findOrThrowByResourceId(person.getResourceId())
		);
		assertNotNull(exception);
		assertEquals("Person not found by resourceId: " + person.getResourceId(), exception.getMessage());
	}

	private void markPersonAsNotNew(final Person target) {
		Arrays.stream(AuditableEntity.class.getDeclaredMethods())
			  .filter(m -> m.getName().equalsIgnoreCase("markAsNotNeo"))
			  .findFirst().ifPresent(m -> {
				  try {
					  m.setAccessible(true);
					  m.invoke(target);
					  m.setAccessible(false);
				  } catch (Exception e) {
					  fail();
				  }
			  });
	}
}
