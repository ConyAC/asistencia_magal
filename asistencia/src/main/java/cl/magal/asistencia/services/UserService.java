package cl.magal.asistencia.services;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.magal.asistencia.entities.Role;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.repositories.RoleRepository;
import cl.magal.asistencia.repositories.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
	static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	UserRepository rep;
	
	@Autowired
	RoleRepository repRole;
	
	@PostConstruct
	public void init(){
		//si no existe un usuario admin, lo crea
		String userName = "admin@admin.com";
		
		cl.magal.asistencia.entities.User usuario = rep.findByEmail(userName);
		if( usuario == null ){
			String password = "123456";			
			usuario = new cl.magal.asistencia.entities.User();
			usuario.setFirstname("Joseph");
			usuario.setRut("12345678-9");
			usuario.setLastname("O'Shea");
			usuario.setEmail(userName);
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(password);
			usuario.setPassword(hashedPassword);
			usuario.setPassword2(hashedPassword);
			rep.save(usuario);
		}
		
	}

	/**
	 * TODO
	 */
	@Override
	  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		//recupera el usuario desde base de datos
		cl.magal.asistencia.entities.User entityUser = rep.findByEmail(userName);
		if( entityUser == null )
			throw new UsernameNotFoundException("Usuario o password incorrectas");		
		if(entityUser.getRole() != null){
			Set<Permission> perm = entityUser.getRole().getPermission();		
		    for(Permission p : perm){
		    	logger.debug("permisos del role id {}", p.name());
			}
		}

		boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new User(entityUser.getEmail(),
        		entityUser.getPassword(), 
                enabled, 
                accountNonExpired,
                credentialsNonExpired, 
                accountNonLocked,
                new LinkedList<GrantedAuthority>());
	}
	
	public void saveUser(cl.magal.asistencia.entities.User u) {
		rep.save(u);
	}
	
	public cl.magal.asistencia.entities.User findUser(Long id){
		return rep.findOne(id);
	}

	public Integer findRawRoleUser(Long id) {
		return (Integer) rep.findRawRoleUser(id);
	}
	
	public void deleteUser(Long id){
		rep.delete(id);
	}

	public Page<cl.magal.asistencia.entities.User> findAllUser(Pageable page) {
		return rep.findAllNotDeteled(page);
	}
	
	/**
	 * 
	 * @param permissions
	 * @return
	 */
	public Collection<? extends GrantedAuthority> getAuthorities(List<Permission> permissions) {
        List<GrantedAuthority> authList = getGrantedAuthorities(getPermission(permissions));
        return authList;
    }
   
	/**
	 * 
	 * @param permissions
	 * @return
	 */
	public List<String> getPermission(List<Permission> permissions) {

       List<String> result = new LinkedList<String>();
       for(Permission permission : permissions )
    	   result.add(permission.name());
       return result;
	}
   
	/**
	 * 
	 * @param permissions
	 * @return
	 */
	public static List<GrantedAuthority> getGrantedAuthorities(
            List<String> permissions) {
        List<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();

        for (String permission : permissions) {
        	logger.debug("permission id {}",permission);
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }
	
	/**
	 * Obtiene un usuario por su nombre de usuario
	 * @param username
	 * @return
	 */
	public cl.magal.asistencia.entities.User findUsuarioByUsername(String username) {
		return rep.findByEmail(username);
	}
	
	/**
	 * 
	 * @param usuario
	 */
	private void savePassword(cl.magal.asistencia.entities.User usuario) {
		if(usuario == null) {
			throw new RuntimeException("Usuario no debe ser nulo");
		}
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(usuario.getPassword());
		usuario.setPassword(hashedPassword);
	}
	
	
	/**
	 * Permite modificar el password a un usuario ya creado
	 * @param id
	 * @param password
	 */
	public void savePassword(Long id, String password) {
		cl.magal.asistencia.entities.User usuario = rep.findOne(id);
		if(usuario == null) {
			throw new RuntimeException("El usuario no existe");
		}
		usuario.setPassword(password);
		savePassword(usuario);
		rep.save(usuario);
	}

	public void saveRole(Role role) {
		repRole.save(role);
	}
}
