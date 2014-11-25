package cl.magal.asistencia.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.services.helpers.ConstructionSiteHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class ConstructionSiteServiceTest {

	Logger logger = LoggerFactory.getLogger(ConstructionSiteServiceTest.class);
	
	@Autowired
	FichaService service;
	
	/**
	 * Debe fallar si la obra no tiene nombre
	 */
	@Test(expected=Exception.class)
	public void testFailSaveConstructionSiteWithOutName() {
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setName(null);
		//guardamos el elemento.
		service.saveConstructionSite(cs);
		fail("no debe llegar aquí");
	}
	
	/**
	 * Debe seleccionar estado Activo por defecto
	 */
	@Test
	public void testConstructionSiteActiveByDefault() {
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setStatus(null);
		//guardamos el elemento.
		service.saveConstructionSite(cs);
		
		assertNotNull("El status no puede ser nulo",cs.getStatus());
		assertEquals("El status por defecto debe ser activo",cs.getStatus(),Status.ACTIVE);
	}
	
	/**
	 * La dirección es opcional
	 */
	@Test
	public void testSaveConstructionSiteWithOutAddress() {
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setAddress(null);
		//guardamos el elemento.
		service.saveConstructionSite(cs);

	}

	
	/**
	 * Test que permite verificar que la obra se almacena correctamente y luego es posible recuperarla
	 */
	@Test
	public void testSaveConstructionSite() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		
		//guardamos el elemento.
		service.saveConstructionSite(cs);
		
		ConstructionSiteHelper.verify(cs);
		
		//recuperamos el elemento de la base
		ConstructionSite bdcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		ConstructionSiteHelper.verify(bdcs);
		
		ConstructionSiteHelper.verify(cs,bdcs);
		
	}
	
	/**
	 * Test de que el status de la obra se almacena en formato integer
	 */
	@Test
	public void testSaveStatusConstructionSiteOnInteger() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		
		//guardamos el elemento.
		service.saveConstructionSite(cs);
		
		//recuperar el elemento directamente de la base (solo para test)
		Integer rawCSStatus = service.findRawStatusCS(cs.getConstructionsiteId());
		assertTrue("El tipo de estado debe ser enum", rawCSStatus.getClass() == Integer.class);
		assertTrue("El enum debe ser igual al guardado", rawCSStatus == Status.ACTIVE.getCorrelative());
		
	}
	
	/**
	 * Encontrar todo mockear la base de datos
	 */
	@Test
	public void testFindConstructionSite() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		
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
