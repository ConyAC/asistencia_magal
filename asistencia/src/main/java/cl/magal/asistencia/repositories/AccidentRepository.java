package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Accident;

public interface AccidentRepository extends
		PagingAndSortingRepository<Accident, Long> {

}
