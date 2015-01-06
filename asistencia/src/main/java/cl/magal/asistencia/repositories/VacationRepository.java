package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Vacation;

public interface VacationRepository extends
		PagingAndSortingRepository<Vacation, Long> {

}
