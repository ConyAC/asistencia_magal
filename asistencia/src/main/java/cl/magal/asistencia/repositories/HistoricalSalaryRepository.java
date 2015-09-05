package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.HistoricalSalary;


public interface HistoricalSalaryRepository extends PagingAndSortingRepository<HistoricalSalary, Long> {

	@Query(value = "select s from HistoricalSalary s where s.laborerConstructionSite.constructionsite = ?1 AND extract( month from s.date ) = extract ( month from ?2 ) and extract( year from s.date ) = extract ( year from ?2 )" )
	List<HistoricalSalary> findByConstructionsiteAndMonth(ConstructionSite cs,
			Date date);
}
