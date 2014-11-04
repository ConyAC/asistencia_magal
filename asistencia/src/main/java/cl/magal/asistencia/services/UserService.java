package cl.magal.asistencia.services;

import java.util.LinkedList;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
	static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	
	
	@PostConstruct
	public void init(){
		
	}
	
	
	
	
	


	@Override
	  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		//recupera el usuario desde base de datos
		
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new User("",
                "", 
                enabled, 
                accountNonExpired,
                credentialsNonExpired, 
                accountNonLocked,
                new LinkedList<GrantedAuthority>());
	}
	
   

}
