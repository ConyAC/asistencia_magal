package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Salary;

public interface SalaryRepository extends
			PagingAndSortingRepository<Salary, Long> {

	
	@Query(value="delete from Salary s where s.laborerConstructionSite.constructionsite = ?1 AND extract( month from s.date ) = extract ( month from ?2 ) and extract( year from s.date ) = extract ( year from ?2 )")
	@Modifying
	void deleteAllInMonth(ConstructionSite cs, Date date);

	@Query(value = "select s from Salary s where s.laborerConstructionSite.constructionsite = ?1 AND extract( month from s.date ) = extract ( month from ?2 ) and extract( year from s.date ) = extract ( year from ?2 )" )
	List<Salary> findByConstructionsiteAndMonth(ConstructionSite cs, Date date);

}
