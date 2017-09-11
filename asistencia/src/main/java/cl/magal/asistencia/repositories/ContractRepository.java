package cl.magal.asistencia.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.Speciality;

public interface ContractRepository extends PagingAndSortingRepository<Contract, Long> {

	@Query(value="SELECT max(lc.jobCode) "
			+ "FROM LaborerConstructionsite lc "
			+ "WHERE lc.constructionsite = ?1 AND lc.jobCode >= ?2 AND lc.jobCode <= ?3 ")
	Integer findJobCodeByConstructionsiteAndBetweenMinAndMax(ConstructionSite constructionsite, int min, int max);

	
	@Query(value="SELECT max(lc.jobCode) "
			+ "FROM LaborerConstructionsite lc  "
			+ "WHERE lc.constructionsite = ?1 AND lc.step = ?2 ")
	Integer existsWithStep(ConstructionSite bean, String step);


	@Query(value="SELECT max(lc.jobCode) "
			+ "FROM LaborerConstructionsite lc "
			+ "WHERE lc.constructionsite = ?1 AND lc.speciality = ?2 ")
	Integer existsWithSpeciality(ConstructionSite bean, Speciality speciality);

}
