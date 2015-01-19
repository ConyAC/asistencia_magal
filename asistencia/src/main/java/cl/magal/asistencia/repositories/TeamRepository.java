package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Team;

public interface TeamRepository extends PagingAndSortingRepository<Team, Long> {

	@Query(value="SELECT t FROM Team t WHERE t.constructionSite = ?1 AND t.deleted = false ")
	List<Team> findByConstructionSite(ConstructionSite bean);

}
