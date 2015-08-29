package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.AfpItem;;

public interface AfpItemRepository extends
		PagingAndSortingRepository<AfpItem, Long> {

}
