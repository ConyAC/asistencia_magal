package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Contract;

public interface ContractRepository extends
		PagingAndSortingRepository<Contract, Long> {

}
