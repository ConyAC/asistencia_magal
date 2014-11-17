package cl.magal.asistencia.services;


import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class UserServiceTest {
	
	@Autowired
	UserService service;
	  
	/**
	 * Almacenar
	 */
	@Test
	public void testSaveUser() {
		
		User u = new User();
		u.setFirstname("Gabriel");
		u.setLastname("Emerson");
		u.setEmail("a@a.cl");
		u.setRut("123-9");

		service.saveUser(u);
		assertTrue("El id no puede ser nulo.", u.getUserId() != null );
		
		User dbu = service.findUser(u.getUserId());
		assertNotNull("El ususario no puede ser nulo", dbu);
		assertTrue("El id de u no puede ser nulo.", u.getUserId() != null );		
	}
	
	/**
	 * Encontrar
	 */
	@Test
	public void testFindUser() {
		
		User u = new User();
		u.setFirstname("Gabriel");
		u.setLastname("Emerson");
		u.setEmail("a@a.cl");
		u.setRut("123-9");
		
		service.saveUser(u);		
		assertTrue("El id no puede ser nulo.", u.getUserId() != null );
		
		User dbu = service.findUser(u.getUserId());		
		assertNotNull("El ususario no puede ser nulo", dbu);
		
		assertEquals("El rut del usuario debe ser igual a ", "123-9", dbu.getRut());
		
	}
	
	/**
	 * Actualizaciòn
	 */
	@Test
	public void testUpdate() {
		
		User u = new User();
		u.setFirstname("Gabriel");
		u.setLastname("Emerson");
		u.setEmail("a@a.cl");
		u.setRut("123-9");
		
		service.saveUser(u);		
		assertTrue("El id no puede ser nulo.", u.getUserId() != null );	
		
		User dbu = service.findUser(u.getUserId());		
		assertNotNull("El ususario no puede ser nulo", dbu);
		
		u.setEmail("b@b.com");		
		service.saveUser(u);
		
		dbu = service.findUser(u.getUserId());			
		assertEquals("Email debe ser igual", u.getEmail(), dbu.getEmail());		
				
	}
	
	/**
	 * Eliminar
	 */
	@Test
	public void testDelete() {
		
		User u = new User();
		u.setFirstname("Gabriel");
		u.setLastname("Emerson");
		u.setEmail("a@a.cl");
		u.setRut("123-9");
		
		service.saveUser(u);		
		assertTrue("El id no puede ser nulo.", u.getUserId() != null );	
				
		service.deleteUser(u.getUserId());		
		
		User dbu = service.findUser(u.getUserId());
				
		assertNull("El usuario debe ser nulo luego de la eliminacion", dbu );
		
	}
	
	/**
	 * Validar usuario
	 */
    @Test
    public void testValidateUser() {
    	
    	UserDetails userDetails = service.loadUserByUsername("administrator");	
    	assertEquals("Usuario incorrecto", "administrator", userDetails.getUsername());
    	assertEquals("Contraseña incorrecta", "admin", userDetails.getPassword());
       
    }   

}
