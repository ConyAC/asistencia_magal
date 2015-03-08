package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Accident;
import cl.magal.asistencia.entities.ConstructionSite;

public interface AccidentRepository extends
		PagingAndSortingRepository<Accident, Long> {

	@Query(value = "select a from Accident a where a.laborerConstructionSite.constructionsite = ?1 AND "
			+ "( ( extract( month from a.toDate ) = extract ( month from ?2 ) AND extract( year from a.toDate ) = extract ( year from ?2 ) ) OR "
			+ " ( extract( month from a.fromDate ) = extract ( month from ?2 ) AND extract( year from a.fromDate ) = extract ( year from ?2 ) ) )" )
	List<Accident> findByConstructionsiteAndMonth(ConstructionSite cs, Date date);

}
