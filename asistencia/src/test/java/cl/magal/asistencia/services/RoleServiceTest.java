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
import cl.magal.asistencia.helpers.RoleHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class RoleServiceTest {

	@Autowired
	RoleService service;

	/**
	 * Guardar Role
	 */
	@Test
	public void testSaveRole() {	
		
		Role r = RoleHelper.newRole();
		service.saveRole(r);
		
		RoleHelper.verify(r);		
	}	
	
	/**
	 * 
	 */
	@Test
	public void testFindRole() {	
		
		Role r = RoleHelper.newRole();
		
		service.saveRole(r);
		RoleHelper.verify(r);
		
		Role dbr = service.findRole(r.getRoleId());
		
		assertNotNull("El rol no puede ser nulo", dbr);		
		assertEquals("El nombre del rol debe ser igual a ", "ADM", dbr.getName());
		
	}	
	
	/**
	 * Actualizar
	 */
	@Test
	public void testUpdateRole() {	
		
		Role r = RoleHelper.newRole();
		service.saveRole(r);
		RoleHelper.verify(r);
		
		Role dbr = service.findRole(r.getRoleId());		
		RoleHelper.verify(r);
		
		r.setName("Adm_Central_1");
		service.saveRole(r);
		
		dbr = service.findRole(r.getRoleId());
		assertEquals("El nombre del rol debe ser igual a ", r.getName(), dbr.getName());
		
	}	
	
	/**
	 * Eliminar
	 */
	@Test
	public void testDeleteRole() {	
		
		Role r = RoleHelper.newRole();
		
		service.saveRole(r);
		assertTrue("El id no puede ser nulo.", r.getRoleId() != null );
		
		service.deleteRole(r.getRoleId());
		
		Role dbr = service.findRole(r.getRoleId());		

		assertNull("El rol debe ser nulo luego de la eliminacion", dbr );
		
	}	
}
