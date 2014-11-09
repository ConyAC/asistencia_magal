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

import cl.magal.asistencia.entities.Obra;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class ObraServiceTest {

	@Autowired
	private transient ObrasService service;
	
	@Test
	public void testSaveObra() {
		
		Obra obra = new Obra();
		obra.setNombre("Obra1");
		obra.setDireccion("Dire");
		
		service.saveObra(obra);
		
		assertTrue("El id no puede ser nulo.",obra.getId() != null );
		
	}
	
	@Test
	public void testFindObra() {
		
		Obra obra = new Obra();
		obra.setNombre("Obra1");
		obra.setDireccion("Dire");
		service.saveObra(obra);
		
		assertTrue("El id no puede ser nulo.",obra.getId() != null );
		
		Obra dbobra = service.findObra(obra.getId());
		
		assertNotNull("La obra no puede ser nula",dbobra);
		
		assertEquals("nombre debe ser igual", "Obra1",dbobra.getNombre());
		assertEquals("direccion debe ser igual", "Dire",dbobra.getDireccion());
		
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
		
	}

}
