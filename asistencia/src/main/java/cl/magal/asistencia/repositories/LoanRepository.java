package cl.magal.asistencia.repositories;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Loan;

public interface LoanRepository extends PagingAndSortingRepository<Loan, Long> {
	
	@Query(value="SELECT l.price FROM Loan l WHERE l.laborerConstructionSite.id = :id ")
	List<Integer> findPriceLoan(@Param("id")Long id);
	
	@Query(value = "select l.price from Loan l where l.laborerConstructionSite.constructionsite = ?1 and extract( month from l.dateBuy ) = extract ( month from ?2 ) and extract( year from l.dateBuy ) = extract ( year from ?2 )" )
	List<Integer> findByConstructionsiteAndMonth(ConstructionSite cs, Date date);

}

