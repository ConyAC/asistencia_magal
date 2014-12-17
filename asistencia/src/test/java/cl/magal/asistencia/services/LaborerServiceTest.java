package cl.magal.asistencia.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.helpers.ConstructionSiteHelper;
import cl.magal.asistencia.helpers.LaborerHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class LaborerServiceTest {

	@Autowired
	LaborerService service;
	
	@Autowired
	ConstructionSiteService constructionService;

	/**
	 * Almacenar
	 */
	@Test
	public void testSaveLaborer() {
		
		Laborer l = LaborerHelper.newLaborer();
		service.saveLaborer(l);
		LaborerHelper.verify(l);
		
		Laborer dbu = service.findLaborer(l.getLaborerId());
		
		LaborerHelper.verify(dbu);		
		LaborerHelper.verify(l,dbu);		
	}
	
	/**
	 * Agregar una vacación
	 */
	@Test
	public void testAddVacation() {
		
		Laborer l = LaborerHelper.newLaborer();
		DateTime dt = new DateTime();
		
		Vacation vacation = new Vacation();
		vacation.setFromDate(dt.toDate());
		vacation.setToDate(dt.plusDays(5).toDate());
		vacation.setLaborer(l);
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		constructionService.save(cs);
		vacation.setConstructionSite(cs);
		
		l.addVacation(vacation);
		
		service.saveLaborer(l);
		LaborerHelper.verify(l);
		
		Laborer dbu = service.findLaborer(l.getLaborerId());
		
		LaborerHelper.verify(dbu);		
		LaborerHelper.verify(l,dbu);
		
		assertTrue("La lista de vacaciones no puede ser vacia",!dbu.getVacations().isEmpty());
		
	}
	/**
	 * Agregar una vacación
	 */
	@Test
	public void testRemoveVacation() {
		
		Laborer l = LaborerHelper.newLaborer();
		DateTime dt = new DateTime();
		
		Vacation vacation = new Vacation();
		vacation.setFromDate(dt.toDate());
		vacation.setToDate(dt.plusDays(5).toDate());
		vacation.setLaborer(l);
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		constructionService.save(cs);
		vacation.setConstructionSite(cs);
		
		l.addVacation(vacation);
		
		service.saveLaborer(l);
		LaborerHelper.verify(l);
		
		Laborer dbu = service.findLaborer(l.getLaborerId());
		
		LaborerHelper.verify(dbu);		
		LaborerHelper.verify(l,dbu);
		
		assertTrue("La lista de vacaciones no puede ser vacia",!dbu.getVacations().isEmpty());
		
		dbu.getVacations().clear();
		service.saveLaborer(dbu);
		
		Laborer dbu2 = service.findLaborer(l.getLaborerId());
		
		LaborerHelper.verify(dbu2);		
		LaborerHelper.verify(l,dbu2);
		
		assertTrue("La lista de vacaciones debe ser vacia",dbu2.getVacations().isEmpty());
		
	}
	
	/**
	 * Encontrar
	 */
	@Test
	public void testFindLaborer() {
		
		Laborer l = LaborerHelper.newLaborer();	

		service.saveLaborer(l);
		LaborerHelper.verify(l);
		
		Laborer dbl = service.findLaborer(l.getLaborerId());		
		assertNotNull("El obrero no puede ser nulo", dbl);
		
		assertEquals("El rut del obrero debe ser igual a ", "12345678-9", dbl.getRut());
		
	}
	
	/**
	 * Actualizaciòn
	 */
	@Test
	public void testUpdate() {
		
		Laborer l = LaborerHelper.newLaborer();

		service.saveLaborer(l);
		LaborerHelper.verify(l);
		
		Laborer dbl = service.findLaborer(l.getLaborerId());		
		LaborerHelper.verify(dbl);
		LaborerHelper.verify(l, dbl);
		
		l.setMobileNumber("99899098");	
		service.saveLaborer(l);
		
		dbl = service.findLaborer(l.getLaborerId());		
		LaborerHelper.verify(l, dbl);	
				
	}
	
	/**
	 * Eliminar
	 */
	@Test
	public void testDelete() {
		
		Laborer l = LaborerHelper.newLaborer();

		service.saveLaborer(l);
		LaborerHelper.verify(l);
		
		Laborer dbl = service.findLaborer(l.getLaborerId());	
		LaborerHelper.verify(dbl);
		LaborerHelper.verify(l, dbl);
				
		service.deleteLaborer(l.getLaborerId());		
		
		dbl = service.findLaborer(l.getLaborerId());		
				
		assertNull("El obrero debe ser nulo luego de la eliminacion", dbl );
		
	}
	
	/**
	 * Debe fallar si el obrero no tiene nombre completo
	 */
	@Test(expected=Exception.class)
	public void testFailSaveFullNameLaborer() {
		Laborer l = LaborerHelper.newLaborer();
		l.setFirstname(null);
		l.setLastname(null);

		service.saveLaborer(l);		
		fail("error");
	}
	
	/**
	 * Debe fallar si el obrero no tiene rut
	 */
	@Test(expected=Exception.class)
	public void testFailSaveRutLaborer() {
		Laborer l = LaborerHelper.newLaborer();
		l.setRut(null);

		service.saveLaborer(l);		
		fail("error");
	}
}
