package cl.magal.asistencia.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Loan;

public interface LoanRepository extends PagingAndSortingRepository<Loan, Long> {
	
	@Query(value="SELECT l.price FROM Loan l WHERE l.laborerConstructionSite.id = :id ")
	List<Integer> findPriceLoan(@Param("id")Long id);

}

