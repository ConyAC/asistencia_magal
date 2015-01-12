package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Loan;

public interface LoanRepository extends PagingAndSortingRepository<Loan, Long> {

}

