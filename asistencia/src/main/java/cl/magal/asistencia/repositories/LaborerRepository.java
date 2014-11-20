package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.Laborer;

public interface LaborerRepository extends PagingAndSortingRepository<Laborer, Long> {

	Page<Laborer> findAll(Pageable page);
	
	@Query(value="SELECT l.job FROM laborer l WHERE l.laborerId = :id " , nativeQuery=true)
	Integer findRawJobLaborer(@Param("id") Long id);

	@Query(value="SELECT l.* FROM laborer l left join laborer_constructionsite lc on lc.laborerId = l.laborerId WHERE lc.construction_siteId = :id " , nativeQuery=true)
	List<Laborer> findByConstructionSite(@Param("id")Long csId);
	
}
