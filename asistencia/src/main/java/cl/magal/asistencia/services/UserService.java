package cl.magal.asistencia.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Role;
import cl.magal.asistencia.entities.enums.Permission;

public interface UserService {

	//	@PostConstruct
	public abstract void init();

	/**
	 * TODO
	 */
	public abstract UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException;

	public abstract void saveUser(cl.magal.asistencia.entities.User user);

	public abstract cl.magal.asistencia.entities.User findUser(Long id);

	public abstract Integer findRawRoleUser(Long id);

	public abstract void deleteUser(Long id);

	public abstract Page<cl.magal.asistencia.entities.User> findAllUser(
			Pageable page);

	/**
	 * 
	 * @param permissions
	 * @return
	 */
	public abstract Collection<GrantedAuthority> getAuthorities(
			Set<Permission> permissions);

	/**
	 * 
	 * @param permissions
	 * @return
	 */
	public abstract List<String> getPermission(Set<Permission> permissions);

	/**
	 * Obtiene un usuario por su nombre de usuario
	 * @param username
	 * @return
	 */
	public abstract cl.magal.asistencia.entities.User findUsuarioByUsername(
			String username);

	public abstract void saveRole(Role role);

	/**
	 * TODO filtrar por usuarios activos solamente
	 * @param page
	 * @return
	 */
	public abstract Page<cl.magal.asistencia.entities.User> findAllActiveUser(
			Pageable page);

	public abstract List<ConstructionSite> getObraByUser(
			cl.magal.asistencia.entities.User u);

	public abstract List<ConstructionSite> getAllObra();

	public abstract void addConstructionSiteToUser(ConstructionSite cs,
			cl.magal.asistencia.entities.User u);

	/**
	 * Elimina todos los datos de la tabla USADO PRINCIPALMENTE POR LOS TEST 
	 */
	public abstract void clear();

	/**
	 * Devuelve una lista con todos los roles posibles
	 * @return
	 */
	public abstract List<Role> getAllRole();

}