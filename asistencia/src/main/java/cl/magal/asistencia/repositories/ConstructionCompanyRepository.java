package cl.magal.asistencia.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionCompany;
import cl.magal.asistencia.entities.User;

public interface ConstructionCompanyRepository extends PagingAndSortingRepository<ConstructionCompany, Long> {
	
	@Query(value="SELECT cc FROM ConstructionCompany cc " )
	List<ConstructionCompany> findAllCC();
}
