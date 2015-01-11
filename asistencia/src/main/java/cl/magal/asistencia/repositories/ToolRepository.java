package cl.magal.asistencia.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Tool;

public interface ToolRepository extends PagingAndSortingRepository<Tool, Long> {

}
