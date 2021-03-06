package cl.magal.asistencia.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Role;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.enums.UserStatus;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.RoleRepository;
import cl.magal.asistencia.repositories.UserRepository;
import cl.magal.asistencia.ui.MagalUI;

import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

@Service(value="userService")
@Transactional(readOnly=true)
public class UserServiceImpl implements UserDetailsService, UserService {
	static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	UserRepository rep;
	
	@Autowired
	RoleRepository repRole;
	
	@Autowired
	ConstructionSiteRepository repoCS;
	
//	@PostConstruct
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#init()
	 */
	@Override
	public void init(){
		//si no existe un usuario admin, lo crea
		String userName = "admin@admin.com";
		
		cl.magal.asistencia.entities.User usuario = rep.findByEmail(userName);
		logger.error("usuario {} ",usuario);
		if( usuario == null ){
			logger.debug("usuario null");
			
			//CREA EL ROL UN ADMINISTRADOR CENTRAL
			Role role = new Role();
			role.setName("Super Administrador");
			Set<Permission> perm = new HashSet<Permission>();	
			perm.add(Permission.CREAR_OBRA);
			perm.add(Permission.EDITAR_OBRA);
			perm.add(Permission.ELIMINAR_OBRA);
			perm.add(Permission.AGREGAR_ETAPAS_OBRA);
			perm.add(Permission.ASIGNAR_OBRA);
			perm.add(Permission.CREAR_USUARIO);
			perm.add(Permission.DEFINIR_VARIABLE_GLOBAL);
			perm.add(Permission.CONFIRMAR_OBREROS);
			perm.add(Permission.BLOQUEAR_OBRERO);
			role.setPermission(perm);	
			repRole.save(role);
			
			// CREA EL USUARIO ADMIN
			String password = "123456";			
			usuario = new cl.magal.asistencia.entities.User();
			usuario.setId(1L);
			usuario.setFirstname("Joseph");
			usuario.setRut("16127401-1");
			usuario.setLastname("O'Shea");
			usuario.setRole(role);
			usuario.setEmail(userName);
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(password);
			usuario.setPassword(hashedPassword);
			usuario.setPassword2(hashedPassword);
			rep.save(usuario);
			
			logger.debug("admin guardado usuario "+usuario);
			
			//CREA EL ROL UN ADMINISTRADOR CENTRAL
			role = new Role();
			role.setName("Administrador Central");
			perm = new HashSet<Permission>();	
			perm.add(Permission.CREAR_OBRA);
			perm.add(Permission.EDITAR_OBRA);
			perm.add(Permission.ELIMINAR_OBRA);
			perm.add(Permission.ASIGNAR_OBRA);
			perm.add(Permission.CREAR_USUARIO);
			perm.add(Permission.DEFINIR_VARIABLE_GLOBAL);
			role.setPermission(perm);	
			repRole.save(role);
			
			logger.debug("guardando role "+role);
			
			cl.magal.asistencia.entities.User user = new cl.magal.asistencia.entities.User();
			user.setId(null);
			user.setRole(role);
			user.setRut("11111111-1");
			user.setFirstname("ADMIN");
			user.setLastname("CENTRAL");
			user.setEmail("central@magal.cl");
//			user.setDeleted(false);
//			user.setStatus(UserStatus.ACTIVE);
			passwordEncoder = new BCryptPasswordEncoder();
			hashedPassword = passwordEncoder.encode(password);
			user.setPassword(hashedPassword);
			user.setPassword2(hashedPassword);
			rep.save(user);
			
			logger.debug("guardando usuario "+usuario);
			
			//CREA EL ROL UN ADMINISTRADOR OBRA
			role = new Role();
			role.setName("Administrador Obra");
			perm = new HashSet<Permission>();	
			perm.add(Permission.AGREGAR_ETAPAS_OBRA);
			perm.add(Permission.CONFIRMAR_OBREROS);
			role.setPermission(perm);
			repRole.save(role);
			
			logger.debug("guardando role "+role);
			
			cl.magal.asistencia.entities.User user2 = new cl.magal.asistencia.entities.User();
			user2.setId(null);
			user2.setRole(role);
			user2.setRut("20400474-9");
			user2.setFirstname("ADMIN");
			user2.setLastname("OBRA");
			user2.setEmail("obra@magal.cl");
			user2.setDeleted(false);
			user2.setStatus(UserStatus.ACTIVE);
			passwordEncoder = new BCryptPasswordEncoder();
			hashedPassword = passwordEncoder.encode(password);
			user2.setPassword(hashedPassword);
			user2.setPassword2(hashedPassword);
			rep.save(user2);
			
			logger.debug("guardando usuario "+usuario);
			
			//CREA EL ROL UN AYUDATE
			role = new Role();
			role.setName("Ayudante");
			perm = new HashSet<Permission>();	
			role.setPermission(perm);
			repRole.save(role);
			
			user = new cl.magal.asistencia.entities.User();
			user.setRole(role);
			user.setRut("24540517-0");
			user.setFirstname("AYUDANTE");
			user.setLastname("OBRA");
			user.setEmail("ayudante@magal.cl");
			user.setDeleted(false);
			user.setStatus(UserStatus.ACTIVE);
			passwordEncoder = new BCryptPasswordEncoder();
			hashedPassword = passwordEncoder.encode(password);
			user.setPassword(hashedPassword);
			user.setPassword2(hashedPassword);
			rep.save(user);
		}else{
			logger.debug("usuario distinto de null");
		}
		
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#loadUserByUsername(java.lang.String)
	 */
	@Override
	  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		//SOLO PARA TESTING CREA USUARIOS SI NO EXISTEN
		init();
		
		cl.magal.asistencia.entities.User entityUser = null;
		//recupera el usuario desde base de datos
		try{
			entityUser = rep.findByEmail(userName);
		}catch(Exception e){
			ErrorMessage errorMessage = MagalUI.getErrorMessageForException(e);
	        if (errorMessage != null) {
	            Notification.show("Error Inesperado", errorMessage.getFormattedHtmlMessage(), Type.WARNING_MESSAGE);
	        }
			logger.error("Error al ingresar al usuario.",e);
		}
		
		if( entityUser == null ){
			logger.debug("usuario con  userName "+userName+" no encontrado");
			throw new UsernameNotFoundException("Usuario o password incorrectas");
		}
		
		logger.debug("logiando usuario "+entityUser);
        List<GrantedAuthority> ll = new LinkedList<GrantedAuthority>(); 
		if(entityUser.getRole() != null){
//			Set<Permission> perm = entityUser.getRole().getPermission();		
//		    for(Permission p : perm){
//		    	logger.debug("permisos del role id {}", p.name());
//			}
			ll = (List<GrantedAuthority>) getAuthorities(entityUser.getRole().getPermission());
		}

		boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        
        logger.debug("entityUser.getEmail() "+entityUser.getEmail()+" entityUser.getPassword() "+entityUser.getPassword());

        return new User(entityUser.getEmail(),
        		entityUser.getPassword(), 
                enabled, 
                accountNonExpired,
                credentialsNonExpired, 
                accountNonLocked,
                ll);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#saveUser(cl.magal.asistencia.entities.User)
	 */
	@Override
	@Transactional
	public void saveUser(cl.magal.asistencia.entities.User user) {
		if(user == null) throw new RuntimeException("Usuario no debe ser nulo");
		//nuevo usuario o edición de usuarios
		if(user.getId() == null || user.getPassword() != null ){
			savePassword(user);
		}else{ //si es edición recupera la información anterior
			cl.magal.asistencia.entities.User db = rep.findOne(user.getId());
			user.setPassword(db.getPassword());
		}
		
		//guardando usuario
		rep.save(user);
	}
	
	private void savePassword(cl.magal.asistencia.entities.User usuario) {
		if(usuario == null) {
			throw new RuntimeException("Usuario no debe ser nulo");
		}
		//las passwords deben coincidir
		if(!usuario.getPassword().equals(usuario.getPassword2())){
			throw new RuntimeException("Los passwords deben coincidir");
		}
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(usuario.getPassword());
		usuario.setPassword(hashedPassword);
	}
	
//	public void saveUser(cl.magal.asistencia.entities.User usuario) {
//		if(usuario == null) {
//			throw new RuntimeException("Usuario no debe ser nulo");
//		}
//		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		String password = usuario.getPassword();
//		if(password == null )
//			password = "123456";
//		String hashedPassword = passwordEncoder.encode(password);
//		usuario.setPassword(hashedPassword);
//		rep.save(usuario);
//	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#findUser(java.lang.Long)
	 */
	@Override
	public cl.magal.asistencia.entities.User findUser(Long id){
		return rep.findOne(id);
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#findRawRoleUser(java.lang.Long)
	 */
	@Override
	public Integer findRawRoleUser(Long id) {
		return (Integer) rep.findRawRoleUser(id);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#deleteUser(java.lang.Long)
	 */
	@Override
	public void deleteUser(Long id){
		rep.delete(id);
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#findAllUser(org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<cl.magal.asistencia.entities.User> findAllUser(Pageable page) {
		return rep.findAllNotDeteled(page);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#getAuthorities(java.util.Set)
	 */
	@Override
	public Collection<GrantedAuthority> getAuthorities(Set<Permission> permissions) {
        List<GrantedAuthority> authList = getGrantedAuthorities(getPermission(permissions));
        return authList;
    }
   
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#getPermission(java.util.Set)
	 */
	@Override
	public List<String> getPermission(Set<Permission> permissions) {

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
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#findUsuarioByUsername(java.lang.String)
	 */
	@Override
	public cl.magal.asistencia.entities.User findUsuarioByUsername(String username) {
		return rep.findByEmail(username);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#saveRole(cl.magal.asistencia.entities.Role)
	 */
	@Override
	public void saveRole(Role role) {
		repRole.save(role);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#findAllActiveUser(org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<cl.magal.asistencia.entities.User> findAllActiveUser(Pageable page) {
		return rep.findAll(page);
	}
	
	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#getObraByUser(cl.magal.asistencia.entities.User)
	 */
	@Override
	public List<ConstructionSite> getObraByUser( cl.magal.asistencia.entities.User u) {
		List<ConstructionSite> cs = repoCS.findByUser(u);
		return cs;
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#getAllObra()
	 */
	@Override
	public List<ConstructionSite> getAllObra() {
		return (List<ConstructionSite>) repoCS.findAllNotDeteled();
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#addConstructionSiteToUser(cl.magal.asistencia.entities.ConstructionSite, cl.magal.asistencia.entities.User)
	 */
	@Override
	@Transactional
	public void addConstructionSiteToUser(ConstructionSite cs, cl.magal.asistencia.entities.User u) {
		
		cl.magal.asistencia.entities.User dbu = rep.findOne(u.getId());
		repoCS.save(cs);
		dbu.addCS(cs);
		rep.save(dbu);
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#clear()
	 */
	@Override
	public void clear() {
		rep.deleteAll();
		
	}

	/* (non-Javadoc)
	 * @see cl.magal.asistencia.services.UserService#getAllRole()
	 */
	@Override
	public List<Role> getAllRole() {
		return (List<Role>) repRole.findAll();
	}
}
