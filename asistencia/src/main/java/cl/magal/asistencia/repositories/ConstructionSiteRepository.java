package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;

public interface ConstructionSiteRepository extends PagingAndSortingRepository<ConstructionSite, Long> {

	Page<ConstructionSite> findAll(Pageable page);
	
	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.deleted = false ")
	Page<ConstructionSite> findAllNotDeteled(Pageable page);
	
	//ConstructionSite findByName(String nombre);
	
	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.address = :address " )
	List<ConstructionSite> findByComplicada(@Param("address") String address);
	
	@Query(value="SELECT cs.status FROM construction_site cs WHERE cs.construction_siteId = :id " ,nativeQuery=true)
	Integer findRawStatusCS(@Param("id") Long id);
	
	@Query(value="SELECT cs.* FROM construction_site cs WHERE cs.address = :address " ,nativeQuery=true)
	List<ConstructionSite> findByAddress(@Param("address") String address);
	
	@Query(value="SELECT cs.* FROM construction_site cs WHERE cs.deleted = :deleted " ,nativeQuery=true)
	List<ConstructionSite> findByNoDeleted(@Param("deleted") boolean deleted);

	ConstructionSite findByName(String nombre);

	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.constructionsiteId = ?1 and cs.deleted = false ")
	ConstructionSite findOneNotDeleted(Long id);
	
	@Query(value="SELECT cs.* FROM ConstructionSite cs left join user_constructionsite uc on uc.constructionsiteId = cs.constructionsiteId WHERE uc.userId = :id " , nativeQuery=true)
	List<ConstructionSite> findByUser(@Param("id")Long userId);

}
