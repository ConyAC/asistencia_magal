package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;

public interface LaborerConstructionsiteRepository extends
		PagingAndSortingRepository<LaborerConstructionsite, Long> {

	
	@Query(value="select lc from LaborerConstructionsite lc where lc.constructionsite = ?1 and lc.active != 0 ")
	List<LaborerConstructionsite> findByConstructionsiteAndIsActive(ConstructionSite constructionsite);
	
	LaborerConstructionsite findByConstructionsiteAndLaborer(ConstructionSite constructionsite, Laborer laborer);
	
	List<LaborerConstructionsite> findByLaborer(Laborer laborer);
	
	LaborerConstructionsite findFirstByLaborerOrderByActiveContractStartDateDesc(Laborer laborer);

}
