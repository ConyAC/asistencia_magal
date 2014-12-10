package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.WageConfigurations;

public interface WageConfigurationsRepository extends
		PagingAndSortingRepository<WageConfigurations, Long> {

}
