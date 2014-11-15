package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.ConstructionSite;

public interface ConstructionSiteRepository extends PagingAndSortingRepository<ConstructionSite, Long> {

	Page<ConstructionSite> findAll(Pageable page);
	
	//ConstructionSite findByNombre(String nombre);
	
	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.address = :address " )
	List<ConstructionSite> findByComplicada(@Param("address") String address);
	
	@Query(value="SELECT cs.status FROM construction_site cs WHERE cs.construction_siteId = :id " ,nativeQuery=true)
	Integer findRawStatusCS(@Param("id") Long id);

}
