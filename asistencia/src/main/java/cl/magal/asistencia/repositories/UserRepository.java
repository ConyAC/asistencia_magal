package cl.magal.asistencia.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.magal.asistencia.entities.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	Page<User> findAll(Pageable page);

}
