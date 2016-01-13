package cl.magal.asistencia.repositories;

import java.util.Collection;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.CostAccount;

public interface CostAccountRepository extends PagingAndSortingRepository<CostAccount, Long> {

	Collection<? extends CostAccount> findByConstructionSite(ConstructionSite bean);

}
