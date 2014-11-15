package cl.magal.asistencia.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.enums.Status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class ConstructionSiteServiceTest {

	@Autowired
	FichaService service;
	
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
	
	/*@Test
	public void testFindConstructionSite() {
		
		ConstructionSite cs = new ConstructionSite();
		cs.setAddress("Dire");
		service.saveConstructionSite(cs);
		
		assertTrue("El id no puede ser nulo.",cs.getConstructionsiteId() != null );
		
		ConstructionSite dbcs = service.findConstructionSite(cs.getConstructionsiteId());
		
		assertNotNull("La obra no puede ser nula", dbcs);
		
		assertEquals("direccion debe ser igual", "Dire", dbcs.getAddress());
		
	}
	
	@Test
	public void testFindObraByNombre() {
		
		Obra obra = new Obra();
		obra.setNombre("Obra1");
		obra.setDireccion("Dire");
		service.saveObra(obra);
		
		assertTrue("El id no puede ser nulo.",obra.getId() != null );
		
		Obra dbobra = service.findObraByNombre(obra.getNombre());
		
		assertNotNull("La obra no puede ser nula",dbobra);
		
		assertEquals("nombre debe ser igual", "Obra1",dbobra.getNombre());
		assertEquals("direccion debe ser igual", "Dire",dbobra.getDireccion());
		
	}
	
	@Test
	public void testFindObraByDireccion() {
		
		Obra obra = new Obra();
		obra.setNombre("Obra1");
		obra.setDireccion("Dire");
		service.saveObra(obra);
		
		assertTrue("El id no puede ser nulo.",obra.getId() != null );
		
		Obra dbobra = service.findObraByDireccion(obra.getDireccion());
		
		assertNotNull("La obra no puede ser nula",dbobra);
		
		assertEquals("nombre debe ser igual", "Obra1",dbobra.getNombre());
		assertEquals("direccion debe ser igual", "Dire",dbobra.getDireccion());
		
	}
	
	@Test
	public void testFindAll() {
		
		Obra obra1 = new Obra();
		obra1.setNombre("Obra1");
		obra1.setDireccion("Dire");
		service.saveObra(obra1);
		
		assertTrue("El id no puede ser nulo.",obra1.getId() != null );
		
		Obra obra2 = new Obra();
		obra2.setNombre("Obra2");
		obra2.setDireccion("Dire2");
		service.saveObra(obra2);
		
		assertTrue("El id no puede ser nulo.",obra2.getId() != null );
		
		Page<Obra> dbobras = service.findAllObra(new PageRequest(0, 1));
		
		assertNotNull("La pagina no puede ser nula",dbobras);
		
		assertTrue("Me debe pasar solo 1 elemento",dbobras.getContent().size() == 1 );
		assertEquals("Me debe entregar el primer elemento",obra1.getId() ,dbobras.getContent().get(0).getId() );
		
	}
	
	@Test
	public void testFindAllOrderInveso() {
		
		Obra obra1 = new Obra();
		obra1.setNombre("Obra1");
		obra1.setDireccion("Dire");
		service.saveObra(obra1);
		
		assertTrue("El id no puede ser nulo.",obra1.getId() != null );
		
		Obra obra2 = new Obra();
		obra2.setNombre("Obra2");
		obra2.setDireccion("Dire2");
		service.saveObra(obra2);
		
		assertTrue("El id no puede ser nulo.",obra2.getId() != null );
		
		Page<Obra> dbobras = service.findAllObra(new PageRequest(0, 1,new Sort(Sort.Direction.DESC, "id")) );
		
		assertNotNull("La pagina no puede ser nula",dbobras);
		
		assertTrue("Me debe pasar solo 1 elemento",dbobras.getContent().size() == 1 );
		assertEquals("Me debe entregar el primer elemento",obra2.getId() ,dbobras.getContent().get(0).getId() );
		
	}*/

}
