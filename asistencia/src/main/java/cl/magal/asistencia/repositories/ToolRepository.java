package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Tool;

public interface ToolRepository extends PagingAndSortingRepository<Tool, Long> {

	@Query(value="SELECT td.date FROM dates td WHERE td.toolId = :id ")
	List<Date> findDatePostponed(@Param("id") Long toolId);
	
}

