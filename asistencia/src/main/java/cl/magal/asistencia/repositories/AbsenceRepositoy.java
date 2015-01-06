package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Absence;

public interface AbsenceRepositoy extends
		PagingAndSortingRepository<Absence, Long> {

}
