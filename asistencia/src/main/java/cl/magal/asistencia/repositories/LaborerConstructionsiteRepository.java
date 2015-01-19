package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;

public interface LaborerConstructionsiteRepository extends
		PagingAndSortingRepository<LaborerConstructionsite, Long> {

	List<LaborerConstructionsite> findByConstructionsite(ConstructionSite constructionsite);
	
	LaborerConstructionsite findByConstructionsiteAndLaborer(ConstructionSite constructionsite, Laborer laborer);
	
	List<LaborerConstructionsite> findByLaborer(Laborer laborer);
	
	LaborerConstructionsite findFirstByLaborerOrderByContractsStartDateDesc(Laborer laborer);

}
