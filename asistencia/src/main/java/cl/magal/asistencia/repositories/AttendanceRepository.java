package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.ConstructionSite;

public interface AttendanceRepository extends PagingAndSortingRepository<Attendance, Long> {

	@Query(value = "select a from Attendance a join fetch a.laborerConstructionSite lc join fetch lc.laborer where lc.constructionsite = ?1 and extract( month from a.date ) = extract ( month from ?2 ) and extract( year from a.date ) = extract ( year from ?2 )" )
	List<Attendance> findByConstructionsiteAndMonth(ConstructionSite cs, Date date);

	@Query(value = "select a from Attendance a where a.laborerConstructionSite.constructionsite = ?1 " )
	List<Attendance> findByConstructionsite(ConstructionSite cs);
	
	@Query(value = "select a from Attendance a where extract( month from a.date ) = extract ( month from ?1 ) and extract( year from a.date ) = extract ( year from ?1 )" )
	List<Attendance> findBydMonth(Date date);

}
