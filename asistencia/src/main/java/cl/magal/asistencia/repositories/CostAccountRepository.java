package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.CostAccount;

public interface CostAccountRepository extends PagingAndSortingRepository<CostAccount, Long> {

	List<CostAccount> findByConstructionSite(ConstructionSite bean);
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Salary s SET s.costAccount = null WHERE s.id IN (SELECT s.id FROM Salary s WHERE s.laborerConstructionSite.constructionsite = ?1 AND s.costAccount = ?2 ) ")
	void removeIdByCSAndCost(ConstructionSite cs, CostAccount ca);
}
