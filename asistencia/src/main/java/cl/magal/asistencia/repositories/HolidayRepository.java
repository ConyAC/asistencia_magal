package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Holiday;

public interface HolidayRepository extends PagingAndSortingRepository<Holiday, Long> {
	
	@Query(value = "select h from Holiday h where extract( month from h.date ) = extract ( month from ?1 ) and extract( year from h.date ) = extract ( year from ?1 )" )
	List<Holiday> findByMonth(Date date);

}

