package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.License;

public interface AbsenceRepositoy extends
		PagingAndSortingRepository<License, Long> {

}
