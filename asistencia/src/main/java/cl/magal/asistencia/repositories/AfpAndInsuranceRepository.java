package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.AfpAndInsuranceConfigurations;

public interface AfpAndInsuranceRepository extends
		PagingAndSortingRepository<AfpAndInsuranceConfigurations, Long> {

}
