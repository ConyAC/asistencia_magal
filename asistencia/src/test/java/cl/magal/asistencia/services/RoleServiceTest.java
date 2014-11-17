package cl.magal.asistencia.services;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class RoleServiceTest {

	@Autowired
	RoleService service;

	/**
	 * 
	 */
	@Test
	public void testSaveRole() {	
		
		Role r = new Role();
		r.setName("Adm_Central");
		r.setDescription("Administra los usuarios del sistema...");
		
		service.saveRole(r);
		assertTrue("El id no puede ser nulo.", r.getRoleId() != null );
		
	}	
	
	/**
	 * 
	 */
	@Test
	public void testFindRole() {	
		
		Role r = new Role();
		r.setName("Adm_Central");
		r.setDescription("Administra los usuarios del sistema...");
		
		service.saveRole(r);
		assertTrue("El id no puede ser nulo.", r.getRoleId() != null );
		
		Role dbr = service.findRole(r.getRoleId());
		
		assertNotNull("El rol no puede ser nulo", dbr);		
		assertEquals("El nombre del rol debe ser igual a ", "Adm_Central", dbr.getName());
		
	}	
	
	/**
	 * 
	 */
	@Test
	public void testUpdateRole() {	
		
		Role r = new Role();
		r.setName("Adm_Central");
		r.setDescription("Administra los usuarios del sistema...");
		
		service.saveRole(r);
		assertTrue("El id no puede ser nulo.", r.getRoleId() != null );
		
		Role dbr = service.findRole(r.getRoleId());		
		assertNotNull("El rol no puede ser nulo", dbr);	
		
		r.setName("Adm_Central_1");
		service.saveRole(r);
		
		dbr = service.findRole(r.getRoleId());
		assertEquals("El nombre del rol debe ser igual a ", r.getName(), dbr.getName());
		
	}	
	
	/**
	 * 
	 */
	@Test
	public void testDeleteRole() {	
		
		Role r = new Role();
		r.setName("Adm_Central");
		r.setDescription("Administra los usuarios del sistema...");
		
		service.saveRole(r);
		assertTrue("El id no puede ser nulo.", r.getRoleId() != null );
		
		service.deleteRole(r.getRoleId());
		
		Role dbr = service.findRole(r.getRoleId());		

		assertNull("El rol debe ser nulo luego de la eliminacion", dbr );
		
	}	
}
