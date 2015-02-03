package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Tool;

public interface ToolRepository extends PagingAndSortingRepository<Tool, Long> {

	@Query(value="SELECT ppt.TOOL_DATE FROM PostponedPaymentTool ppt WHERE ppt.toolId = :id ", nativeQuery=true)
	List<Date> findDatePostponed(@Param("id") Long id);
	
}

