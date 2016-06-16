package cl.magal.asistencia.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Permission;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	Page<User> findAll(Pageable page);

	@Query(value="SELECT u.role FROM user u WHERE u.userId = :id " , nativeQuery=true)
	Integer findRawRoleUser(@Param("id") Long id);	
	
	@Query(value="SELECT u FROM User u WHERE u.deleted = false ")
	Page<User> findAllNotDeteled(Pageable page);
	@Query(value="SELECT u FROM User u WHERE u.email = ?1 and u.deleted = false ")
	User findByEmail(String email); 
	
	@Query(value="SELECT u FROM User u WHERE u.deleted = false " )
	List<User> findAllNotDeteled();

	@Query(value="SELECT u FROM User u, u.role r WHERE ?1 member of r.permission and u.deleted = false " )
	List<User> findAllByPermission(Permission confirmarAsistenciaCentral);
}
