package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;

public interface FamilyAllowanceRepository extends
		PagingAndSortingRepository<FamilyAllowanceConfigurations, Long> {

}
