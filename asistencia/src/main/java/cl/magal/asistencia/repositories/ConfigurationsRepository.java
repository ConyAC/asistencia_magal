package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Configurations;

public interface ConfigurationsRepository extends
		PagingAndSortingRepository<Configurations, Long> {

}
