package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.License;

public interface LicenseRepositoy extends
		PagingAndSortingRepository<License, Long> {

	@Query(value = "select l from License l where l.laborerConstructionSite.constructionsite = ?1 AND "
			+ "( ( extract( month from l.toDate ) = extract ( month from ?2 ) AND extract( year from l.toDate ) = extract ( year from ?2 ) ) OR "
			+ " ( extract( month from l.fromDate ) = extract ( month from ?2 ) AND extract( year from l.fromDate ) = extract ( year from ?2 ) ) )" )
	List<License> findByConstructionsiteAndMonth(ConstructionSite cs, Date date);

}
