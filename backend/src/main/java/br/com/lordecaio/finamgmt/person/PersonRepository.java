package br.com.lordecaio.finamgmt.person;

import br.com.lordecaio.finamgmt.person.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface PersonRepository extends JpaRepository<Person, Long> {

	Optional<Person> findByResourceId(UUID resourceId);
	void deleteByResourceId(UUID resourceId);
}
