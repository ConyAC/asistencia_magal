package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Bank;

public interface BankRepository extends
		PagingAndSortingRepository<Bank, Long> {

}
