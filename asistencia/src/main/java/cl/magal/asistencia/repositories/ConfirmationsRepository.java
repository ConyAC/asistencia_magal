package cl.magal.asistencia.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Confirmations;
import cl.magal.asistencia.entities.ConstructionSite;

public interface ConfirmationsRepository extends PagingAndSortingRepository<Confirmations, Long> {

	@Query(value = "select c from Confirmations c where c.constructionsite = ?1 and extract( month from c.date ) = extract ( month from ?2 ) and extract( year from c.date ) = extract ( year from ?2 )" )
	Confirmations findByConstructionsiteAndMonth(ConstructionSite cs, Date date);

}
