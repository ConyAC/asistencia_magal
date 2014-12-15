package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.AdvancePaymentConfigurations;

public interface AdvancePaymentRepository extends
		PagingAndSortingRepository<AdvancePaymentConfigurations, Long> {

}
