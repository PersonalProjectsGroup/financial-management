package br.com.lordecaio.finamgmt.person;

import br.com.lordecaio.finamgmt.person.exception.PersonError;
import br.com.lordecaio.finamgmt.person.exception.PersonException;
import br.com.lordecaio.finamgmt.person.model.Person;
import br.com.lordecaio.finamgmt.person.model.request.CreatePerson;
import br.com.lordecaio.finamgmt.person.model.response.PublicPerson;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PersonService {

	private static final Logger log = LoggerFactory.getLogger(PersonService.class);

	private final PersonRepository repository;

	PersonService(PersonRepository repository) { this.repository = repository; }

	public PublicPerson create(CreatePerson person) {
		log.info("Start - create - Person creation with data: {}", person);

		var saved = repository.save(person.toEntity());

		log.info("End - create - Person creation");
		return PublicPerson.from(saved);
	}

	public PublicPerson findByResourceId(UUID resourceId) {
		log.info("Start - findByResourceId - Find person by resourceId: {}", resourceId);

		return PublicPerson.from(this.findOrThrowByResourceId(resourceId));
	}

	@Transactional
	public void deleteByResourceId(UUID resourceId) {
		log.info("Start - deleteByResourceId - Person deletion with resourceId: {}", resourceId);

		repository.save(this.findOrThrowByResourceId(resourceId).delete());

		log.info("End - deleteByResourceId - Person deletion");
	}

	public Person findOrThrowByResourceId(UUID resourceId) {
		log.info("Start - findOrThrowByResourceId - Find person by resourceId: {}", resourceId);
		return repository.findByResourceId(resourceId)
			  .orElseThrow(() -> {
				  var message = "Person not found by resourceId: " + resourceId;
				  log.error("End - findOrThrowByResourceId - {}", message);
				  return PersonException.from(PersonError.PERSON_NOT_FOUND, message);
			  });
	}
}
