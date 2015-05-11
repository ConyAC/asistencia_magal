package cl.magal.asistencia.repositories;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Loan;

public interface LoanRepository extends PagingAndSortingRepository<Loan, Long> {
	
	@Query(value = "select l from Loan l where l.laborerConstructionSite.constructionsite = ?1 and extract( month from l.dateBuy ) = extract ( month from ?2 ) and extract( year from l.dateBuy ) = extract ( year from ?2 )" )
	List<Loan> findByConstructionsiteAndMonth(ConstructionSite cs, Date date);

	@Query(value ="select l from Loan l where l.laborerConstructionSite.id = ?1 ")
	List<Integer> findPriceLoan(Long id);

	@Query(value="SELECT t from Loan t WHERE t.laborerConstructionSite.constructionsite = ?1 AND "
			+ " extract(year from t.dateBuy) + extract(month from t.dateBuy) < extract(year from ?2) + extract(month from ?2) AND "
			+ " extract(year from t.dateBuy) + extract(month from t.dateBuy) + t.fee + size(t.datePostponed) >= extract(year from ?2) + extract(month from ?2)  AND "
			+ " ?2 NOT MEMBER OF t.datePostponed ")
	List<Loan> findFeeByConstructionsiteAndMonth(ConstructionSite cs, Date date);

}

