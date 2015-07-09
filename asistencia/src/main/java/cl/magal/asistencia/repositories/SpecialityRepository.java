package cl.magal.asistencia.repositories;

import java.util.Collection;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Speciality;

public interface SpecialityRepository extends PagingAndSortingRepository<Speciality, Long> {

	Collection<? extends Speciality> findByConstructionSite(ConstructionSite bean);

}
