package cl.magal.asistencia.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.Laborer;

public interface LaborerRepository extends PagingAndSortingRepository<Laborer, Long>{

	Page<Laborer> findAll(Pageable page);
	
	@Query(value="SELECT l.job FROM laborer l WHERE l.laborerId = :id " , nativeQuery=true)
	Integer findRawJobLaborer(@Param("id") Long id);

}
