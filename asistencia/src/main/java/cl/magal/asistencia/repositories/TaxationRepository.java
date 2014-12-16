package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.TaxationConfigurations;

public interface TaxationRepository extends PagingAndSortingRepository<TaxationConfigurations, Long> {

}
