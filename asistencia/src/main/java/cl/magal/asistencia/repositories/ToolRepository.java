package cl.magal.asistencia.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Tool;

public interface ToolRepository extends PagingAndSortingRepository<Tool, Long> {

	@Query(value="SELECT ppt.TOOL_DATE FROM PostponedPaymentTool ppt WHERE ppt.toolId = :id ", nativeQuery=true)
	List<Date> findDatePostponed(@Param("id")Long id);

//	@Query(value="SELECT t from Tool t WHERE t.laborerConstructionSite.constructionsite = ?1 AND "
//			+ " (extract(year from t.dateBuy)* 100)  + extract(month from t.dateBuy) <= (extract(year from ?2)* 100)  + extract(month from ?2) AND "
//			+ " (extract(year from t.dateBuy)* 100)  + extract(month from t.dateBuy) + t.fee + size(t.datePostponed) > (extract(year from ?2)* 100)  + extract(month from ?2)  AND "
//			+ " ?2 NOT MEMBER OF t.datePostponed ")
//	List<Tool> findFeeByConstructionsiteAndMonth(ConstructionSite cs, Date date);	
	
	@Query(value="SELECT t from Tool t WHERE t.laborerConstructionSite.constructionsite = ?1 AND "
			+ " (extract(year from t.dateBuy)* 100)  + extract(month from t.dateBuy) <= (extract(year from ?2)* 100)  + extract(month from ?2) AND "
			+ " (extract(year from SQL('DATE_ADD(?, INTERVAL ? MONTH)',t.dateBuy,t.fee + size(t.datePostponed)))* 100)  + extract(month from SQL('DATE_ADD(?, INTERVAL ? MONTH)',t.dateBuy,t.fee + size(t.datePostponed))) > (extract(year from ?2)* 100)  + extract(month from ?2)  AND "
			+ " ?2 NOT MEMBER OF t.datePostponed ")
	List<Tool> findFeeByConstructionsiteAndMonth(ConstructionSite cs, Date date);	
}

