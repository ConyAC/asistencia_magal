package cl.koritsu.asistenca_ws.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.koritsu.asistenca_ws.model.AttendanceClock;

public interface AttendanceClockRepository extends PagingAndSortingRepository<AttendanceClock, Long> {

	@Query(value="SELECT cs "
			+ "FROM ConstructionSite cs WHERE cs.id = ?1 and cs.deleted = false ")
	List<String> findNotIn(Long id,List<AttendanceClock> attendancesClocks);

}
