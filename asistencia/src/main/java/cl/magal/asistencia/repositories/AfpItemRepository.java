package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.AfpItem;

public interface AfpItemRepository extends
		PagingAndSortingRepository<AfpItem, Long> {	

	@Query(value="select afp from AfpItem afp order by afp.name asc")
	List<AfpItem> findAllOrderName();

}
