package cl.magal.asistencia.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	Page<User> findAll(Pageable page);

	@Query(value="SELECT u.role FROM user u WHERE u.userId = :id " , nativeQuery=true)
	Integer findRawRoleUser(@Param("id") Long id);	
}