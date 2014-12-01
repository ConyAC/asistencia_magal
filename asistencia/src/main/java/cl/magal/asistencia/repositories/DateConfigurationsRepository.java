package cl.magal.asistencia.repositories;

import java.util.Date;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.DateConfigurations;

public interface DateConfigurationsRepository extends PagingAndSortingRepository<DateConfigurations, Long> {

	DateConfigurations findByDate(Date value);

}
