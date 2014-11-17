package cl.magal.asistencia.services;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.enums.Status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class ConstructionSiteServiceTest {

	@Autowired
	FichaService service;
	
	/**
	 * Almacenar
	 */
	@Test
	public void testSaveConstructionSite() {
		
		ConstructionSite cs = new ConstructionSite();
		cs.setAddress("Dire");
		cs.setStatus(Status.ACTIVE);
		
		//guardamos el elemento.
		service.saveConstructionSite(cs);
		
		//recuperamos el elemento de la base
		ConstructionSite bdcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		//verificar que el tipo del estado sea del tipo enum Status
		assertTrue("El tipo de estado debe ser enum", bdcs.getStatus().getClass() == Status.class);
		assertTrue("El enum debe ser igual al guardado", bdcs.getStatus() == Status.ACTIVE);
		
		//recuperar el elemento directamente de la base (solo para test)
		Integer rawCSStatus = service.findRawStatusCS(cs.getConstructionsiteId());
		assertTrue("El tipo de estado debe ser enum", rawCSStatus.getClass() == Integer.class);
		assertTrue("El enum debe ser igual al guardado", rawCSStatus == Status.ACTIVE.getCorrelative());
		
		assertTrue("El id de cs no puede ser nulo.",cs.getConstructionsiteId() != null );
		
	}
	
	/**
	 * Encontrar
	 */
	@Test
	public void testFindConstructionSite() {
		
		ConstructionSite cs = new ConstructionSite();
		cs.setAddress("Dire");
		cs.setStatus(Status.ACTIVE);
		
		service.saveConstructionSite(cs);
		
		assertTrue("El id no puede ser nulo.",cs.getConstructionsiteId() != null );
		
		ConstructionSite dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		assertNotNull("La obra no puede ser nula", dbcs);
		
		assertEquals("direccion debe ser igual", "Dire", dbcs.getAddress());
		
	}
	
	/**
	 * Listar no eliminados (campo deleted)
	 */
	@Test
	public void testFindByNoDeleted(){
		
		ConstructionSite cs = new ConstructionSite();
		cs.setAddress("Dire");
		cs.setStatus(Status.ACTIVE);
		cs.setDeleted(false);
		
		service.saveConstructionSite(cs);
		
		assertTrue("El id no puede ser nulo.", cs.getConstructionsiteId() != null );
		
		ConstructionSite dbcs = service.findByNoDeleted(cs.getDeleted());
		
		assertNotNull("La obra no puede ser nula", dbcs);
		
		assertTrue("La obra no debe estar eliminada", dbcs.getDeleted() != true);
	}
	
	/**
	 * Actualizar
	 */
	@Test
	public void testUpdate() {
		
		ConstructionSite cs = new ConstructionSite();
		cs.setAddress("Dire");
		cs.setStatus(Status.ACTIVE);
		cs.setDeleted(false);
		
		service.saveConstructionSite(cs);		
		assertTrue("El id no puede ser nulo.", cs.getConstructionsiteId() != null );
		
		ConstructionSite dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		assertNotNull("La obra no puede ser nula", dbcs);
		
		cs.setAddress("cambio");	
		service.saveConstructionSite(cs);
		
		dbcs = service.findConstructionSite(cs.getConstructionsiteId());		
		assertEquals("Email debe ser igual", cs.getAddress(), dbcs.getAddress());	 	
				
	}
	
	/**
	 * Eliminar
	 */
	@Test
	public void testDelete(){
		
		ConstructionSite cs = new ConstructionSite();
		cs.setAddress("Dire");
		cs.setStatus(Status.ACTIVE);
		
		service.saveConstructionSite(cs);
		
		assertTrue("El id no puede ser nulo.", cs.getConstructionsiteId() != null );
		
		service.deleteCS(cs.getConstructionsiteId());
		
		ConstructionSite dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		assertNull("La obra debe ser nula", dbcs);
		
	}	
}
