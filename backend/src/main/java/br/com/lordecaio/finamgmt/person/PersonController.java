package br.com.lordecaio.finamgmt.person;

import br.com.lordecaio.finamgmt.person.model.request.CreatePerson;
import br.com.lordecaio.finamgmt.person.model.response.PublicPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(PersonController.BASE_URL)
public class PersonController {

	public static final String BASE_URL = "/profiles";
	private static final Logger log = LoggerFactory.getLogger(PersonController.class);

	private final PersonService service;

	PersonController(PersonService service) { this.service = service; }

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PublicPerson create(@RequestBody CreatePerson createPerson) {
		log.info("Start - Profile creation with data: {}", createPerson);

		var profile = service.create(createPerson);

		log.info("End - Profile creation");
		return profile;

	}

	@GetMapping("/{resourceId}")
	public PublicPerson findByResourceId(@PathVariable UUID resourceId) {
		log.info("Start - Find person by resourceId: {}", resourceId);

		var profile = service.findByResourceId(resourceId);

		log.info("End - Find person by resourceId");
		return profile;
	}

	@DeleteMapping("/{resourceId}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void delete(@PathVariable UUID resourceId) {
		log.info("Start - Delete person by resourceId: {}", resourceId);

		service.deleteByResourceId(resourceId);

		log.info("End - Delete person by resourceId");
	}

}
