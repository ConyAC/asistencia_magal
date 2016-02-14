package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.CostAccount;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Salary;

public interface SalaryRepository extends
			PagingAndSortingRepository<Salary, Long> {

	
	@Query(value="delete from Salary s where s.laborerConstructionSite.constructionsite = ?1 AND extract( month from s.date ) = extract ( month from ?2 ) and extract( year from s.date ) = extract ( year from ?2 )")
	@Modifying
	void deleteAllInMonth(ConstructionSite cs, Date date);

	@Query(value = "select s from Salary s where s.laborerConstructionSite.constructionsite = ?1 AND extract( month from s.date ) = extract ( month from ?2 ) and extract( year from s.date ) = extract ( year from ?2 )" )
	List<Salary> findByConstructionsiteAndMonth(ConstructionSite cs, Date date);

	
	@Query(value = "select avg(s.jornalPromedio) from Salary s where s.laborerConstructionSite = ?1 AND "
			+ " (extract( year from s.date )* 100)  + extract( month from s.date ) in "
			+ " ( ((extract( year from ?2 )* 100)  + extract ( month from ?2 )) , "
			+ "   ((extract( year from ?2 )* 100)  + extract ( month from ?2 ) - 1) , "
			+ "   ((extract( year from ?2 )* 100)  + extract ( month from ?2 ) - 2 ) )" )
	Double calculateJornalPromedioAvg(LaborerConstructionsite lc,Date terminationDate);
	
	@Query(value = "select s.jornalPromedio from Salary s where s.laborerConstructionSite = ?1 AND "
			+ " (extract( year from s.date )* 100)  + extract( month from s.date ) in "
			+ " ( ((extract( year from ?2 )* 100)  + extract ( month from ?2 )) , "
			+ "   ((extract( year from ?2 )* 100)  + extract ( month from ?2 ) - 1) , "
			+ "   ((extract( year from ?2 )* 100)  + extract ( month from ?2 ) - 2 ) ) order by s.date desc " )
	List<Double> get3LastJornalPromedio(LaborerConstructionsite lc,Date terminationDate);


	@Query(value = "SELECT s FROM Salary s LEFT JOIN LaborerConstructionsite lc on lc.constructionsite = s.laborerConstructionSite.constructionsite WHERE lc.constructionsite = ?1 AND s.costAccount = ?2 " )
	List<Salary> findByConstructionsiteAndCost(ConstructionSite cs, CostAccount ca);
}
