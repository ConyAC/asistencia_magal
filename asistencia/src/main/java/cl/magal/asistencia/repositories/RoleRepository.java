package cl.magal.asistencia.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.Role;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long>{

	Page<Role> findAll(Pageable page);

}
