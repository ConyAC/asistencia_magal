package cl.magal.asistencia.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.DateConfigurations;

public interface DateConfigurationsRepository extends PagingAndSortingRepository<DateConfigurations, Long> {

	@Query(value="select d from DateConfigurations d where extract(month from d.date ) = extract(month from ?1 ) and extract (year from d.date) = extract(year from ?1) ")
	DateConfigurations findByDate(Date value);

}
