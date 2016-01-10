package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;

public interface LaborerConstructionsiteRepository extends
		PagingAndSortingRepository<LaborerConstructionsite, Long> {

	
	@Query(value="select lc from LaborerConstructionsite lc join fetch lc.laborer join fetch lc.activeContract where lc.constructionsite = ?1 and lc.active != 0 ")
	List<LaborerConstructionsite> findByConstructionsiteAndIsActive(ConstructionSite constructionsite);
	
	LaborerConstructionsite findByConstructionsiteAndLaborer(ConstructionSite constructionsite, Laborer laborer);
	
	@Query(value="select lc from LaborerConstructionsite lc where lc.laborer = ?1 order by lc.activeContract.active desc,lc.activeContract.terminationDate desc")
	List<LaborerConstructionsite> findByLaborer(Laborer laborer);
	
	LaborerConstructionsite findFirstByLaborerOrderByActiveContractStartDateDesc(Laborer laborer);

	@Query(value="select lcs.constructionsite from LaborerConstructionsite lcs where lcs.laborer = ?1 and lcs.active = 1 ")
	ConstructionSite findConstructionsiteByLaborer(Laborer laborer);
	
	@Query(value="select lc from LaborerConstructionsite lc join fetch lc.activeContract where lc.active != 0 ")
	List<LaborerConstructionsite> findConstructionsiteActive();

	@Query(value="select lc from LaborerConstructionsite lc join fetch lc.laborer join fetch lc.activeContract ac where lc.constructionsite = ?1 "
			+ " and ( (extract( year from ac.startDate) * 100) + extract( month from ac.startDate) ) <= ( (extract( year from ?2) * 100) + extract( month from ?2)) "
			+ " and ( ac.terminationDate is null or ( ( extract( year from ac.terminationDate ) * 100) + extract( month from ac.terminationDate ) ) >= ((extract( year from  ?2 ) * 100) + extract( month from  ?2 )) ) ")
	List<LaborerConstructionsite> findByConstructionsiteAndIsActiveThisMonth(ConstructionSite cs, Date date);

}
