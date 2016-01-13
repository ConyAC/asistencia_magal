package cl.magal.asistencia.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.CostAccount;
import cl.magal.asistencia.entities.Speciality;

public interface ContractRepository extends PagingAndSortingRepository<Contract, Long> {

	@Query(value="SELECT max(c.jobCode) "
			+ "FROM Contract c "
			+ "WHERE c.laborerConstructionSite.constructionsite = ?1 AND c.jobCode >= ?2 AND c.jobCode <= ?3 ")
	Integer findJobCodeByConstructionsiteAndBetweenMinAndMax(ConstructionSite constructionsite, int min, int max);

	
	@Query(value="SELECT max(c.jobCode) "
			+ "FROM Contract c "
			+ "WHERE c.laborerConstructionSite.constructionsite = ?1 AND c.step = ?2 ")
	Integer existsWithStep(ConstructionSite bean, String step);


	@Query(value="SELECT max(c.jobCode) "
			+ "FROM Contract c "
			+ "WHERE c.laborerConstructionSite.constructionsite = ?1 AND c.speciality = ?2 ")
	Integer existsWithSpeciality(ConstructionSite bean, Speciality speciality);
	
	@Query(value="SELECT max(c.jobCode) "
			+ "FROM Contract c "
			+ "WHERE c.laborerConstructionSite.constructionsite = ?1 AND c.costAccount = ?2 ")
	Integer existsWithCostAccount(ConstructionSite bean, CostAccount costAccount);

}
