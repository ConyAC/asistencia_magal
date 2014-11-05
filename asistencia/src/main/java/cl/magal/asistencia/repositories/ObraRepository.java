package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.Obra;

public interface ObraRepository extends PagingAndSortingRepository<Obra, Long> {

	Page<Obra> findAll(Pageable page);
	
	Obra findByNombre(String nombre);
	
	@Query(value="SELECT o FROM Obra o WHERE o.direccion = :direccion " )
	List<Obra> findByComplicada(@Param("direccion") String direccion);
}
