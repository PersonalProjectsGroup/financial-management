package br.com.lordecaio.finamgmt.person;

import br.com.lordecaio.finamgmt.common.util.UUIDUtils;
import br.com.lordecaio.finamgmt.person.exception.PersonError;
import br.com.lordecaio.finamgmt.person.exception.PersonException;
import br.com.lordecaio.finamgmt.person.model.Person;
import br.com.lordecaio.finamgmt.person.model.request.CreatePerson;
import br.com.lordecaio.finamgmt.person.model.response.PublicPerson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

	private static UUID resourceId;
	private static PublicPerson publicPerson;

	@MockitoBean
	private PersonService service;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeAll
	static void beforeAll() {
		resourceId = UUIDUtils.nextV7();
		var person = Person.from("John", "Doe")
			  .withResourceId(resourceId);
		publicPerson = PublicPerson.from(person);
	}

	@Test
	void shouldReturn201WhenCreate() throws Exception {
		var createPerson = new CreatePerson("John", "Doe");
		var content = objectMapper.writeValueAsString(createPerson);

		when(service.create(any(CreatePerson.class))).thenReturn(publicPerson);

		mockMvc.perform(post(PersonController.BASE_URL)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(content))
			  .andExpect(status().isCreated())
			  .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
	}

	@Test
	void shouldReturn200WhenFindByResourceId() throws Exception {
		when(service.findByResourceId(any())).thenReturn(publicPerson);

		var result = mockMvc.perform(
					get(PersonController.BASE_URL + "/{resourceId}", resourceId))
			  .andExpect(status().isOk())
			  .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			  .andReturn();

		var response = objectMapper.readValue(result.getResponse().getContentAsString(), PublicPerson.class);

		assertEquals(publicPerson, response);
	}

	@Test
	void shouldReturn202WhenDeleteByResourceId() throws Exception {
		doNothing().when(service).deleteByResourceId(any(UUID.class));
		mockMvc.perform(delete(PersonController.BASE_URL + "/{resourceId}", resourceId))
			  .andExpect(status().isAccepted());
	}

	@Test
	void shouldReturn404WhenFindByResourceId() throws Exception {
		when(service.findByResourceId(any())).thenThrow(
			  PersonException.from(PersonError.PERSON_NOT_FOUND, "Not Found"));
		mockMvc.perform(get(PersonController.BASE_URL + "/{resourceId}", resourceId))
			  .andExpect(status().isNotFound());
	}

	@Test
	void shouldReturn404WhenDeleteByResourceId() throws Exception {
		doThrow(PersonException.from(PersonError.PERSON_NOT_FOUND, "Not Found"))
			  .when(service).deleteByResourceId(any(UUID.class));
		mockMvc.perform(delete(PersonController.BASE_URL + "/{resourceId}", resourceId))
			  .andExpect(status().isNotFound());
	}
}
