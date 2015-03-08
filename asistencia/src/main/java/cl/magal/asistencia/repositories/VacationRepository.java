package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Vacation;

public interface VacationRepository extends
		PagingAndSortingRepository<Vacation, Long> {

	@Query(value = "select v from Vacation v where v.laborerConstructionSite.constructionsite = ?1 AND "
			+ "( ( extract( month from v.toDate ) = extract ( month from ?2 ) AND extract( year from v.toDate ) = extract ( year from ?2 ) ) OR "
			+ " ( extract( month from v.fromDate ) = extract ( month from ?2 ) AND extract( year from v.fromDate ) = extract ( year from ?2 ) ) )" )
	List<Vacation> findByConstructionsiteAndMonth(ConstructionSite cs, Date date);

}
