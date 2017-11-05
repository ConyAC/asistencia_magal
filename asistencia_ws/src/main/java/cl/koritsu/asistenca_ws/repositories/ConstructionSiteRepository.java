package cl.koritsu.asistenca_ws.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.asistenca_ws.model.ConstructionSite;

public interface ConstructionSiteRepository extends PagingAndSortingRepository<ConstructionSite, Long> {

	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.id = ?1 and cs.deleted = false ")
	ConstructionSite findOneNotDeleted(Long id);

}
