package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.ConstructionSite;

public interface AdvancePaymentRepository extends
		PagingAndSortingRepository<AdvancePaymentConfigurations, Long> {
	
	@Query(value = "select a from AdvancePaymentConfigurations a where a.constructionSite = ?1 " )
	List<AdvancePaymentConfigurations> findAdvancePaymentConfigurationsByCS(ConstructionSite cs);

}
