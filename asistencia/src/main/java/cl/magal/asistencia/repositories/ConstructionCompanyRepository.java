package cl.magal.asistencia.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionCompany;

public interface ConstructionCompanyRepository extends PagingAndSortingRepository<ConstructionCompany, Long> {
	
	@Query(value="SELECT cc FROM ConstructionCompany cc " )
	List<ConstructionCompany> findAllCC();
}
