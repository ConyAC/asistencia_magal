package cl.magal.asistencia.services;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Role;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.UserStatus;
import cl.magal.asistencia.helpers.ConstructionSiteHelper;
import cl.magal.asistencia.helpers.RoleHelper;
import cl.magal.asistencia.helpers.UserHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class UserServiceTest {
	
	Logger logger = LoggerFactory.getLogger(ConstructionSiteServiceTest.class);
	
	@Autowired
	UserServiceImpl service;
	@Autowired
	ConstructionSiteService constructionSiteService;
	
	@Autowired
	private transient AuthenticationManager authenticationManager;
	
	@Before
	public void before(){
		service.clear();
	}
	
	@Test
	public void saveRole(){
		Role role = RoleHelper.newRole();
		service.saveRole(role);
	}
	
	  
	/**
	 * Almacenar y encontrar usuario
	 */
	@Test
	public void testSaveUser() {
		
		Role role = RoleHelper.newRole();
		service.saveRole(role);
		
		User u = UserHelper.newUser();
		service.saveUser(u);
		UserHelper.verify(u);		
		User dbu = service.findUser(u.getId());
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
		
		User dbu = service.findUser(u.getId());		
		
		UserHelper.verify(dbu);
		UserHelper.verify(u, dbu);
		
		u.setFirstname("Jesse");
		service.saveUser(u);			
		
		dbu = service.findUser(u.getId());
		UserHelper.verify(u, dbu);				
	}
	
	/**
	 * Encontrar
	 */
	@Test
	public void testFindUser() {
		
		User u = UserHelper.newUser();
		
		service.saveUser(u);		
		assertTrue("El id no puede ser nulo.", u.getId() != null );
		
		User dbu = service.findUser(u.getId());		
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
		User dbu = service.findUser(u.getId());
		
		UserHelper.verify(dbu);
		UserHelper.verify(u, dbu);
		
		service.deleteUser(u.getId());		
		
		dbu = service.findUser(u.getId());
				
		assertNull("El usuario debe ser nulo luego de la eliminacion", dbu );
		
	}
		
	/**
	 * Validar usuario
	 */
    @Test
    public void testValidateUser() {
    	
    	String pasword = "123456";
    	String email = "a@b.com";
    	
    	User u = UserHelper.newUser();	    	
    	service.saveUser(u);	
    	
		UserHelper.verify(u);
		User dbu = service.findUser(u.getId());
		
		UserHelper.verify(dbu);
		UserHelper.verify(u, dbu);
		
    	UserDetails userDetails = service.loadUserByUsername(u.getEmail());	
    	assertEquals("Usuario incorrecto", email, userDetails.getUsername());
    	
    	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    	String hashedPassword = passwordEncoder.encode(pasword);
    	assertNotEquals("El hash del password no puede ser el mismo!",hashedPassword, u.getPassword());
       
    }   

    /**
     * Asignar rol
     */
    @Test
    public void testUserRole() {
    	
    	Role role = RoleHelper.newRole();
		service.saveRole(role);
    	
    	User u = UserHelper.newUser();
    	u.setRole(role);
    	
		service.saveUser(u);
		UserHelper.verify(u);
		
		User dbu = service.findUser(u.getId());
		UserHelper.verify(dbu);
		
		assertTrue("El id del role no puede ser nulo.", u.getRole().getId() != null );		
		assertEquals("Id de Rol", u.getRole(), dbu.getRole());
       
    }   
    
    /**
	 * Encontrar por username
	 */
	@Test
	public void testFindUserByUserName() {
		
		String email =  "c@b.com"; //usa un email en particular para no chocar con los otros test
		User u = UserHelper.newUser();
		u.setEmail(email);
		
		service.saveUser(u);		
		UserHelper.verify(u);
		
		User dbu = service.findUsuarioByUsername(email);		
		assertNotNull("El ususario no puede ser nulo", dbu);
		
		assertEquals("El email del usuario debe ser igual a ",email, dbu.getEmail());
		
	}
	
	/**
	 * Permite testear el login de un nuevo usuario
	 */
	@Test
	public void testLogin(){
		String email = "login@login.com";
		String password = "123456";
		User u = UserHelper.newUser();
		u.setEmail(email);
		u.setPassword(password);
		
		//crea el usuario
		service.saveUser(u);
		
		//y luego intenta logearse 
		try{
			UsernamePasswordAuthenticationToken token = 
	                new UsernamePasswordAuthenticationToken(email, password);
	        
	        Authentication authentication = authenticationManager.authenticate(token);
		}catch(AuthenticationException e){
			fail("Error al realizar el login");
		}
	}
	
	/**
	 * Permite testear un mal login de un nuevo usuario
	 */
	@Test(expected=AuthenticationException.class)
	public void testBadLogin(){
		String email = "login@login.com";
		String password = "123456";
		String badPassword = "654321";
		User u = UserHelper.newUser();
		u.setEmail(email);
		u.setPassword(password);
		
		//crea el usuario
		service.saveUser(u);
		
		//y luego intenta logearse 
		UsernamePasswordAuthenticationToken token = 
                new UsernamePasswordAuthenticationToken(email, badPassword);
        
        Authentication authentication = authenticationManager.authenticate(token);
		fail("No deberia llegar aquí");
	}
	
	/**
	 * Listar activos
	 */
	@Test
	public void testListActive(){
		
		User u = UserHelper.newUser();
		u.setStatus(UserStatus.DESACTIVADO);
		service.saveUser(u);
		UserHelper.verify(u,true);
		
		User dbu = service.findUser(u.getId());
		
		UserHelper.verify(dbu,true);
		assertNotNull("El usuario no debe estar desactivado", dbu.getStatus());
	}

	/**
	 * Test para agregar obras a un usuario
	 */
	@Test
	public void testAddConstructioSite(){
		//crea dos obras
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		constructionSiteService.save(cs);
		//crea dos obras
		ConstructionSite cs2 = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		constructionSiteService.save(cs2);
		
		//crea un usuario
		User u = UserHelper.newUser();
		//crea el usuario
		service.saveUser(u);
		
		//agrega la construction 2 
		service.addConstructionSiteToUser(cs2, u);
		
		User dbu = service.findUser(u.getId());
		
		//verifica que al recuperar la obra, se obtenga el trabajador guardado
		assertNotNull("El objeto guardado no puede ser nulo",dbu);
		assertTrue("El objeto guardado debe contener trabajadores",!dbu.getCs().isEmpty());
		assertEquals("El objeto guardado debe contener el trabajador agregado",dbu.getCs().get(0),cs2 );
		
		//agrega otro mas
		service.addConstructionSiteToUser(cs,u);
		
		dbu = service.findUser(u.getId());
		
		//los ids de los laborer no pueden ser iguales
		assertNotEquals("los ids de los laborer no pueden ser iguales",cs2.getId(),cs.getId());
		
		//verifica que al recuperar la obra, se obtenga el trabajador guardado
		assertNotNull("El objeto guardado no puede ser nulo",dbu);
		assertTrue("El objeto guardado debe contener trabajadores",!dbu.getCs().isEmpty());
		assertEquals("El objeto guardado debe contener el trabajador agregado",dbu.getCs().get(1),cs );
		
	}
    
}
