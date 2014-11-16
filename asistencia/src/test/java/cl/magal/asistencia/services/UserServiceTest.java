package cl.magal.asistencia.services;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class UserServiceTest {
	
	@Autowired
	UserService service;
	
	@Test
	public void testSaveUser() {
		
		User u = new User();
		u.setFirstname("Gabriel");
		u.setLastname("Emerson");
		u.setRole(Role.ADM_CENTRAL);

		
	}

}
