package cl.magal.asistencia.services;


import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.Role;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.helpers.UserHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class UserServiceTest {
	
	Logger logger = LoggerFactory.getLogger(ConstructionSiteServiceTest.class);
	
	@Autowired
	UserService service;
	  
	/**
	 * Almacenar y encontrar usuario
	 */
	@Test
	public void testSaveUser() {
		
		User u = UserHelper.newUser();
		
		service.saveUser(u);
		UserHelper.verify(u);				
		User dbu = service.findUser(u.getUserId());
		UserHelper.verify(dbu);		
		UserHelper.verify(u, dbu);				
	}
	
	/**
	 * Falla si los principales datos estàn nulos
	 */
	@Test(expected=Exception.class)
	public void testFailSaveNull() {
		
		User u = UserHelper.newUser();	
		u.setFirstname(null);
		u.setLastname(null);
		u.setRut(null);
		
		service.saveUser(u);
		fail("No debe llegar aquì");		
	}
	
	/**
	 * Actualizaciòn
	 */
	@Test
	public void testUpdate() {
		
		User u = UserHelper.newUser();
				
		service.saveUser(u);		

		UserHelper.verify(u);
		
		User dbu = service.findUser(u.getUserId());		
		
		UserHelper.verify(dbu);
		UserHelper.verify(u, dbu);
		
		u.setFirstname("Jesse");
		service.saveUser(u);			
		
		dbu = service.findUser(u.getUserId());
		UserHelper.verify(u, dbu);				
	}
	
	/**
	 * Encontrar
	 */
	@Test
	public void testFindUser() {
		
		User u = UserHelper.newUser();
		
		service.saveUser(u);		
		assertTrue("El id no puede ser nulo.", u.getUserId() != null );
		
		User dbu = service.findUser(u.getUserId());		
		assertNotNull("El ususario no puede ser nulo", dbu);
		
		assertEquals("El rut del usuario debe ser igual a ", "16127401-1", dbu.getRut());
		
	}	
	
	/**
	 * Eliminar
	 */
	@Test
	public void testDelete() {
		
		User u = UserHelper.newUser();
		service.saveUser(u);		

		UserHelper.verify(u);
		User dbu = service.findUser(u.getUserId());
		
		UserHelper.verify(dbu);
		UserHelper.verify(u, dbu);
		
		service.deleteUser(u.getUserId());		
		
		dbu = service.findUser(u.getUserId());
				
		assertNull("El usuario debe ser nulo luego de la eliminacion", dbu );
		
	}
		
	/**
	 * Validar usuario
	 */
    @Test
    public void testValidateUser() {
    	
    	User u = UserHelper.newUser();	    	
    	service.saveUser(u);	
    	
		UserHelper.verify(u);
		User dbu = service.findUser(u.getUserId());
		
		UserHelper.verify(dbu);
		UserHelper.verify(u, dbu);
		
    	UserDetails userDetails = service.loadUserByUsername(u.getEmail());	
    	assertEquals("Usuario incorrecto", "a@b.com", userDetails.getUsername());
    	assertEquals("Contraseña incorrecta", "123456", u.getPassword());
       
    }   

    /**
     * Asignar rol
     */
    @Test
    public void testUserRole() {
    	
    	User u = new User();
    	u.setFirstname("Gabriel");
		u.setLastname("Emerson");
		u.setEmail("a@a.cl");
		u.setRut("123-9");
		u.setRole(new Role());

		service.saveUser(u);
		assertTrue("El id no puede ser nulo.", u.getUserId() != null );
		
		User dbu = service.findUser(u.getUserId());
		assertNotNull("El ususario no puede ser nulo", dbu);
		assertTrue("El id de u no puede ser nulo.", u.getUserId() != null );		
		assertEquals("Id de Rol", u.getRole(), dbu.getRole());
       
    }   

    
}
