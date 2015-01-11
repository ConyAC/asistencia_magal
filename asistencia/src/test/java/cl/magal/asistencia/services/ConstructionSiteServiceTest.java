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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.helpers.ConstructionSiteHelper;
import cl.magal.asistencia.helpers.LaborerHelper;
import cl.magal.asistencia.helpers.UserHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class ConstructionSiteServiceTest {

	Logger logger = LoggerFactory.getLogger(ConstructionSiteServiceTest.class);
	
	@Autowired
	ConstructionSiteService service;
	@Autowired
	UserService userService;
	
	User user;
	
	@Before
	public void before(){
		service.clear();userService.clear();
		user = UserHelper.newUser();
		userService.saveUser(user);
	}
	
	/**
	 * Debe fallar si la obra no tiene nombre
	 */
	@Test(expected=Exception.class)
	public void testFailSaveConstructionSiteWithOutName() {
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setName(null);
		//guardamos el elemento.
		service.save(cs);
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
		service.save(cs);
		
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
		service.save(cs);

	}

	
	/**
	 * Test que permite verificar que la obra se almacena correctamente y luego es posible recuperarla
	 */
	@Test
	public void testSaveConstructionSite() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		
		//guardamos el elemento.
		service.save(cs);
		
		ConstructionSiteHelper.verify(cs);
		
		//recuperamos el elemento de la base
		ConstructionSite bdcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		ConstructionSiteHelper.verify(bdcs);
		
		ConstructionSiteHelper.verify(cs,bdcs);
		
	}
	
	/**
	 * Test que permite verificar que la obra se almacena correctamente y luego es posible recuperarla
	 */
	@Test
	public void testSaveConstructionSiteWithPersonInCharge() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setPersonInCharge(user);
		//guardamos el elemento.
		service.save(cs);
		
		ConstructionSiteHelper.verify(cs);
		
		assertNotNull("La persona a cargo no puede ser nula, si ésta fue seteada",cs.getPersonInCharge());
		
		//recuperamos el elemento de la base
		ConstructionSite bdcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		ConstructionSiteHelper.verify(bdcs);
		
		ConstructionSiteHelper.verify(cs,bdcs);
		
		assertNotNull("La persona a cargo no puede ser nula, si ésta fue guardada",bdcs.getPersonInCharge());
		
	}
	
	/**
	 * Test de que el status de la obra se almacena en formato integer
	 */
	@Test
	public void testSaveStatusConstructionSiteOnInteger() {
		
//		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
//		
//		//guardamos el elemento.
//		service.save(cs);
//		
//		//recuperar el elemento directamente de la base (solo para test)
//		Integer rawCSStatus = service.findRawStatusCS(cs.getConstructionsiteId());
//		assertTrue("El tipo de estado debe ser enum", rawCSStatus.getClass() == Integer.class);
//		assertTrue("El enum debe ser igual al guardado", rawCSStatus == Status.ACTIVE.getCorrelative());
		
	}
	
	/**
	 * Encontrar todo mockear la base de datos
	 */
	@Test
	public void testFindConstructionSite() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		
		service.save(cs);
		
		assertTrue("El id no puede ser nulo.",cs.getConstructionsiteId() != null );
		
		ConstructionSite dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		assertNotNull("La obra no puede ser nula", dbcs);
		
		assertEquals("direccion debe ser igual", cs.getAddress(), dbcs.getAddress());
		
	}
	
	/**
	 * Listar no eliminados (campo deleted)
	 */
	@Test
	public void testSaveNotDeletedByDefault(){
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setDeleted(null);
		service.save(cs);
		
		assertTrue("El id no puede ser nulo.", cs.getConstructionsiteId() != null );
		
		ConstructionSite dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		assertNotNull("La obra no puede ser nula", dbcs);
		assertNotNull("La obra no debe estar eliminada", dbcs.getDeleted());
		assertTrue("La obra no debe estar eliminada", !dbcs.getDeleted());
	}
	
	/**
	 * Actualizar
	 */
	@Test
	public void testUpdate() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		
		service.save(cs);		
		
		ConstructionSiteHelper.verify(cs);
		
		ConstructionSite dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		ConstructionSiteHelper.verify(dbcs);
		ConstructionSiteHelper.verify(cs,dbcs);
		
		cs.setAddress("cambio");	
		service.save(cs);
		
		dbcs = service.findConstructionSite(cs.getConstructionsiteId());		
		ConstructionSiteHelper.verify(cs,dbcs); 	
				
	}
	
	/**
	 * Eliminar
	 */
	@Test
	public void testDelete(){
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		service.save(cs);
		
		ConstructionSiteHelper.verify(cs);
		//intenta encontrarlo
		ConstructionSite dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		ConstructionSiteHelper.verify(dbcs);
		ConstructionSiteHelper.verify(cs,dbcs);
		
		service.deleteCS(cs.getConstructionsiteId());
		
		dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		assertNull("La obra debe ser nula", dbcs);
		
	}	
	
	@Test
	public void testFindAllJustNotDeleted(){
		
		service.clear();
		
		//crea 3 obras
		ConstructionSite primera = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		service.save(primera);
		ConstructionSiteHelper.verify(primera);
		
		ConstructionSite segunda = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		service.save(segunda);
		ConstructionSiteHelper.verify(segunda);
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		service.save(cs);
		ConstructionSiteHelper.verify(cs);
		
		//elimina la 3°
		service.deleteCS(cs.getConstructionsiteId());
		
		//si se buscan todas, debe retornar solo las dos primeras
		Page<ConstructionSite> page = service.findAllConstructionSite(new PageRequest(0, 10));
		
		assertNotNull("La pagina no puede ser nula",page);
		assertTrue("La pagina no puede tener más de dos elementos",page.getContent().size() == 2);
		assertTrue("La pagina debe contener el primer elemento",page.getContent().contains(primera));
		assertTrue("La pagina debe contener el segundo elemento",page.getContent().contains(segunda));
	}
	
	@Test
	public void testAddLaborer(){
		//crea una obra
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		service.save(cs);		
		//verifica
		
		//agrega un trabajador a la obra
		Laborer laborer1 = LaborerHelper.newLaborer();
		service.addLaborerToConstructionSite(laborer1,cs);
		
		//verifica que el trabajador tiene un id válido
		LaborerHelper.verify(laborer1);
		
		ConstructionSite dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		//verifica que al recuperar la obra, se obtenga el trabajador guardado
		assertNotNull("El objeto guardado no puede ser nulo",dbcs);
		assertTrue("El objeto guardado debe contener trabajadores",!dbcs.getLaborers().isEmpty());
		assertEquals("El objeto guardado debe contener el trabajador agregado",dbcs.getLaborers().get(0),laborer1 );
		
		//agrega otro mas
		//agrega un trabajador a la obra
		Laborer laborer2 = LaborerHelper.newLaborer();
		service.addLaborerToConstructionSite(laborer2,cs);
		
		//verifica que el trabajador tiene un id válido
		LaborerHelper.verify(laborer2);
		
		dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		//los ids de los laborer no pueden ser iguales
		assertNotEquals("los ids de los laborer no pueden ser iguales",laborer2.getLaborerId(),laborer1.getLaborerId());
		
		//verifica que al recuperar la obra, se obtenga el trabajador guardado
		assertNotNull("El objeto guardado no puede ser nulo",dbcs);
		assertTrue("El objeto guardado debe contener trabajadores",!dbcs.getLaborers().isEmpty());
		assertEquals("El objeto guardado debe contener el trabajador agregado",dbcs.getLaborers().get(1),laborer2 );
		
	}
}
