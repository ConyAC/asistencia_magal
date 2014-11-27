package cl.magal.asistencia.services;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.magal.asistencia.repositories.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
	static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@PostConstruct
	public void init(){
		
	}

	/**
	 * TODO
	 */
	@Override
	  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		//recupera el usuario desde base de datos
		
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new User("administrator",
                "admin", 
                enabled, 
                accountNonExpired,
                credentialsNonExpired, 
                accountNonLocked,
                new LinkedList<GrantedAuthority>());
	}
	
	@Autowired
	UserRepository rep;
	
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
}
