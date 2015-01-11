package cl.magal.asistencia.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Contract;

public interface ContractRepository extends PagingAndSortingRepository<Contract, Long> {

	@Query(value="SELECT max(c.jobCode) "
			+ "FROM Contract c "
			+ "WHERE c.laborerConstructionSite.constructionsite = ?1 AND c.jobCode >= ?2 AND c.jobCode <= ?3 ")
	Integer findJobCodeByConstructionsiteAndBetweenMinAndMax(ConstructionSite constructionsite, int min, int max);

}
