package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.CostAccount;
import cl.magal.asistencia.entities.Salary;

public interface CostAccountRepository extends PagingAndSortingRepository<CostAccount, Long> {

	List<CostAccount> findByConstructionSite(ConstructionSite bean);
	
//	@Modifying
//	@Transactional
//	@Query(value="UPDATE Salary s, LaborerConstructionsite lc SET s.costAccount = 0 WHERE s.costAccount = ?1 AND lc = ?2 ")
//	void removeIdByCSAndCost(CostAccount costAccount, ConstructionSite cs);
	
//	@Modifying
//	@Transactional
//	@Query(value="UPDATE Salary s SET s.costAccount.id = 0 WHERE ?1 IN (s.id) ")
//	void removeIdByCSAndCost(List<Long> salary);

}
