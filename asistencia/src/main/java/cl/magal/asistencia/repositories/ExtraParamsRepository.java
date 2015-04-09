package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.ExtraParams;

public interface ExtraParamsRepository extends PagingAndSortingRepository<ExtraParams, Long> {
	
	@Query(value = "select a from ExtraParams a where a.laborerConstructionSite.constructionsite = ?1 and extract( month from a.date ) = extract ( month from ?2 ) and extract( year from a.date ) = extract ( year from ?2 )" )
	List<ExtraParams> findByConstructionsiteAndMonth(ConstructionSite cs, Date date);

}
