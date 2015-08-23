package cl.magal.asistencia.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.User;

public interface ConstructionSiteRepository extends PagingAndSortingRepository<ConstructionSite, Long> {

	Page<ConstructionSite> findAll(Pageable page);
	
	
	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.deleted = false ") // activos todos
	Page<ConstructionSite> findAllNotDeteled(Pageable page);
	
	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.deleted = false ") // activos todos
	List<ConstructionSite> findAllNotDeteled();
	
	@Query(value="select cs FROM User u join u.cs cs WHERE u = ?1 and cs.status = cl.magal.asistencia.entities.enums.Status.ACTIVE and cs.deleted = false " //activos usuario
				+ "UNION "
				+ "SELECT cs FROM ConstructionSite cs WHERE cs.status = cl.magal.asistencia.entities.enums.Status.ACTIVE and cs.deleted = false " // activos todos
				+ "UNION "
				+ "select cs FROM User u join u.cs cs WHERE u = ?1 and cs.status = cl.magal.asistencia.entities.enums.Status.FINALIZED and cs.deleted = false " // inactivos usuario
				+ "UNION "
				+ "SELECT cs FROM ConstructionSite cs WHERE cs.status = cl.magal.asistencia.entities.enums.Status.FINALIZED and cs.deleted = false " // inactivos todos
				)
	Page<ConstructionSite> findAllNotDeteledOderByUser(Pageable page,User user);
	
	@Query(value="select cs FROM User u join u.cs cs WHERE u = ?1 and cs.status = cl.magal.asistencia.entities.enums.Status.ACTIVE and cs.deleted = false " //activos usuario
			+ "UNION "
			+ "SELECT cs FROM ConstructionSite cs WHERE cs.status = cl.magal.asistencia.entities.enums.Status.ACTIVE and cs.deleted = false " // activos todos
			+ "UNION "
			+ "select cs FROM User u join u.cs cs WHERE u = ?1 and cs.status = cl.magal.asistencia.entities.enums.Status.FINALIZED and cs.deleted = false " // inactivos usuario
			+ "UNION "
			+ "SELECT cs FROM ConstructionSite cs WHERE cs.status = cl.magal.asistencia.entities.enums.Status.FINALIZED and cs.deleted = false " // inactivos todos
			)
	List<ConstructionSite> findAllNotDeteledOderByUser(User user);
	
	//ConstructionSite findByName(String nombre);
	
	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.address = :address " )
	List<ConstructionSite> findByComplicada(@Param("address") String address);
	
//	@Query(value="SELECT cs.status FROM construction_site cs WHERE cs.construction_siteId = :id " ,nativeQuery=true)
//	Integer findRawStatusCS(@Param("id") Long id);
	
//	@Query(value="SELECT cs.* FROM construction_site cs WHERE cs.address = :address " ,nativeQuery=true)
//	List<ConstructionSite> findByAddress(@Param("address") String address);
	
//	@Query(value="SELECT cs.* FROM construction_site cs WHERE cs.deleted = :deleted " ,nativeQuery=true)
//	List<ConstructionSite> findByNoDeleted(@Param("deleted") boolean deleted);

	ConstructionSite findByName(String nombre);

	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.id = ?1 and cs.deleted = false ")
	ConstructionSite findOneNotDeleted(Long id);
	
	@Query(value="SELECT cs FROM ConstructionSite cs WHERE :user MEMBER OF cs.users " )
	List<ConstructionSite> findByUser(@Param("user")User user);

	@Query(value="select cs FROM User u join u.cs cs WHERE u = ?1 and cs.status = cl.magal.asistencia.entities.enums.Status.ACTIVE and cs.deleted = false ") //activos usuario
	List<ConstructionSite> findActiveByUser(User user);

	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.status = cl.magal.asistencia.entities.enums.Status.ACTIVE and cs.deleted = false and ?1 not member of cs.users ") //activos usuario
	List<ConstructionSite> findActiveByNotUser(User user);
	
	@Query(value="select cs FROM User u join u.cs cs WHERE u = ?1 and cs.status = cl.magal.asistencia.entities.enums.Status.FINALIZED and cs.deleted = false ") //activos usuario
	List<ConstructionSite> findFinalizedByUser(User user);
	
	@Query(value="SELECT cs FROM ConstructionSite cs WHERE cs.status = cl.magal.asistencia.entities.enums.Status.FINALIZED and cs.deleted = false and ?1 not member of cs.users ") //activos usuario
	List<ConstructionSite> findFinalizedByNotUser(User user);



}
