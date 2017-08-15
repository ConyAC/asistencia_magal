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

	
	@Query(value="select lc from LaborerConstructionsite lc join fetch lc.laborer where lc.constructionsite = ?1 and lc.active != 0 ")
	List<LaborerConstructionsite> findByConstructionsiteAndIsActive(ConstructionSite constructionsite);
	
	LaborerConstructionsite findByConstructionsiteAndLaborer(ConstructionSite constructionsite, Laborer laborer);
	
	@Query(value="select lc from LaborerConstructionsite lc where lc.laborer = ?1 order by lc.active desc,lc.terminationDate desc")
	List<LaborerConstructionsite> findByLaborer(Laborer laborer);
	
	LaborerConstructionsite findFirstByLaborerOrderByStartDateDesc(Laborer laborer);

	@Query(value="select lcs.constructionsite from LaborerConstructionsite lcs where lcs.laborer = ?1 and lcs.active = 1 ")
	ConstructionSite findConstructionsiteByLaborer(Laborer laborer);
	
	@Query(value="select lc from LaborerConstructionsite lc where lc.active != 0 ")
	List<LaborerConstructionsite> findConstructionsiteActive();

	@Query(value="select lc from LaborerConstructionsite lc join fetch lc.laborer where lc.constructionsite = ?1 "
			+ " and ( (extract( year from lc.startDate) * 100) + extract( month from lc.startDate) ) <= ( (extract( year from ?2) * 100) + extract( month from ?2)) "
			+ " and ( lc.terminationDate is null or ( ( extract( year from lc.terminationDate ) * 100) + extract( month from lc.terminationDate ) ) >= ((extract( year from  ?2 ) * 100) + extract( month from  ?2 )) ) ")
	List<LaborerConstructionsite> findByConstructionsiteAndIsActiveThisMonth(ConstructionSite cs, Date date);

	@Query(value="select lc from LaborerConstructionsite lc where lc.constructionsite = ?1 and lc.jobCode = ?2")
	LaborerConstructionsite findByConstructionsiteAndJobCode(ConstructionSite cs, int jobcode);

}
