package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Laborer;

public interface LaborerRepository extends PagingAndSortingRepository<Laborer, Integer> {

}
