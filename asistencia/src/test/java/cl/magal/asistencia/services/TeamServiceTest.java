package cl.magal.asistencia.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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



import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.helpers.LaborerHelper;
import cl.magal.asistencia.helpers.TeamHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class TeamServiceTest {

	@Autowired
	TeamService service;
	
	@Autowired
	LaborerService serviceL;
	
	/**
	 * Crear
	 */
	@Test
	public void testSaveTeam() {
		
		Team t = TeamHelper.newTeam();
		service.saveTeam(t);
		TeamHelper.verify(t);
		
		Team dbt = service.findTeam(t.getTeamId());
		
		TeamHelper.verify(dbt);		
		TeamHelper.verify(t,dbt);		
	}
	
	/**
	 * Encontrar
	 */
	@Test
	public void testFindTeam() {
		
		Team t = TeamHelper.newTeam();
		service.saveTeam(t);
		TeamHelper.verify(t);
		
		Team dbt = service.findTeam(t.getTeamId());
		
		assertNotNull("La cuadrilla no puede ser nulo", dbt);		
		assertEquals("El nombre de la cuadrilla debe der igual a ", "Cuadrilla 1", dbt.getName());
		
	}

	/**
	 * Actualización
	 */
	@Test
	public void testUpdate() {
		
		Team t = TeamHelper.newTeam();
		service.saveTeam(t);
		TeamHelper.verify(t);
		
		Team dbt = service.findTeam(t.getTeamId());		
		TeamHelper.verify(t);
		TeamHelper.verify(t, dbt);
		
		t.setStatus(Status.FINALIZED);	
		service.saveTeam(t);
		
		dbt = service.findTeam(t.getTeamId());
		TeamHelper.verify(t, dbt);	
				
	}
	
	/**
	 * Eliminar
	 */
	@Test
	public void testDelete() {
		
		Team t = TeamHelper.newTeam();
		service.saveTeam(t);
		TeamHelper.verify(t);
		
		Team dbt = service.findTeam(t.getTeamId());		
		TeamHelper.verify(t);
		TeamHelper.verify(t, dbt);
				
		service.deleteTeam(t.getTeamId());	
		
		dbt = service.findTeam(t.getTeamId());					
		assertNull("La cuadrilla debe ser nula luego de la eliminación", dbt );
		
	}
	
	/**
	 * Debe fallar si no se crea la cuadrilla con nombre
	 */
	@Test(expected=Exception.class)
	public void testFailSaveNameLTeam() {
		Team t = TeamHelper.newTeam();
		t.setName(null);
		
		service.saveTeam(t);
		fail("error");
	}
	
	/**
	 * Añadir trabajadores a la cuadrilla.
	 */
	@Test
	public void testAddLaborer(){
		Team t = TeamHelper.newTeam();
		service.saveTeam(t);
		TeamHelper.verify(t);
		
		Laborer laborer = LaborerHelper.newLaborer();
		service.addLaborerToTeam(laborer, t);
		
		LaborerHelper.verify(laborer);
		
		Team dbt = service.findTeam(t.getTeamId());
		
		//verifica que al recuperar la cuadrilla, se obtenga el trabajador guardado
		assertNotNull("El objeto guardado no puede ser nulo", dbt);
//		assertTrue("El objeto guardado debe contener trabajadores", !dbt.getLaborers().isEmpty());
//		assertEquals("El objeto guardado debe contener el trabajador agregado", dbt.getLaborers().get(0), laborer );	
		
		//agrega otro mas
		//agrega un trabajador a la obra
		Laborer laborer2 = LaborerHelper.newLaborer();
		service.addLaborerToTeam(laborer2, dbt);
		
		//verifica que el trabajador tiene un id válido
		LaborerHelper.verify(laborer2);
		
		dbt = service.findTeam(t.getTeamId());
		
		//los ids de los laborer no pueden ser iguales
		assertNotEquals("los ids de los obreros no pueden ser iguales", laborer2.getLaborerId(), laborer.getLaborerId());
		
		//verifica que al recuperar la cuadrilla, se obtenga el trabajador guardado
		assertNotNull("El objeto guardado no puede ser nulo", dbt);
//		assertTrue("El objeto guardado debe contener trabajadores", !dbt.getLaborers().isEmpty());
//		assertEquals("El objeto guardado debe contener el trabajador agregado", dbt.getLaborers().get(1),laborer2 );
	}
}
