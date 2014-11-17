package cl.magal.asistencia.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.MaritalStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class LaborerServiceTest {

	@Autowired
	LaborerService service;

	/**
	 * Almacenar
	 */
	@Test
	public void testSaveLaborer() {
		
		Laborer l = new Laborer();
		l.setFirstname("Dan");
		l.setLastname("Haus");
		l.setAddress("add");
		l.setAfp(Afp.MODELO);
		l.setContractId(1);
		l.setDateAdmission(new Date());
		l.setDateBirth(new Date());
		l.setJob(Job.ALBAÑIL);
		l.setMaritalStatus(MaritalStatus.CASADO);
		l.setMobileNumber("33443");
		l.setPhone("0222");
		l.setRut("123-9");	

		service.saveLaborer(l);
		assertTrue("El id no puede ser nulo.", l.getLaborerId() != null );
		
		Laborer dbu = service.findLaborer(l.getLaborerId());
		
		//verificar tipo enum 	
		assertTrue("El oficio debe ser enum", dbu.getJob().getClass() == Job.class);
		assertTrue("El enum debe ser igual al guardado", dbu.getJob() == Job.ALBAÑIL);
		
		assertTrue("El ms debe ser enum", dbu.getMaritalStatus().getClass() == MaritalStatus.class);
		assertTrue("El enum debe ser igual al guardado", dbu.getMaritalStatus() == MaritalStatus.CASADO);
		
		assertTrue("La afp debe ser enum", dbu.getAfp().getClass() == Afp.class);
		assertTrue("El enum debe ser igual al guardado", dbu.getAfp() == Afp.MODELO);
		
		//recuperar el elemento directamente de la base	(test)
		Integer rawJob = service.findRawJobLaborer(l.getLaborerId());		
		assertTrue("El tipo de job debe ser enum", rawJob.getClass() == Integer.class);
		assertTrue("El enum debe ser igual al guardado", rawJob == Job.ALBAÑIL.getCorrelative());
				
		assertTrue("El id de u no puede ser nulo.", l.getLaborerId() != null );
		
	}
	
	/**
	 * Encontrar
	 */
	@Test
	public void testFindLaborer() {
		
		Laborer l = new Laborer();
		l.setFirstname("Dan");
		l.setLastname("Haus");
		l.setAddress("add");
		l.setAfp(Afp.MODELO);
		l.setContractId(1);
		l.setDateAdmission(new Date());
		l.setDateBirth(new Date());
		l.setJob(Job.ALBAÑIL);
		l.setMaritalStatus(MaritalStatus.CASADO);
		l.setMobileNumber("33443");
		l.setPhone("0222");
		l.setRut("123-9");	

		service.saveLaborer(l);
		assertTrue("El id no puede ser nulo.", l.getLaborerId() != null );
		
		Laborer dbl = service.findLaborer(l.getLaborerId());		
		assertNotNull("El obrero no puede ser nulo", dbl);
		
		assertEquals("El rut del obrero debe ser igual a ", "123-9", dbl.getRut());
		
	}
	
	/**
	 * Actualizaciòn
	 */
	@Test
	public void testUpdate() {
		
		Laborer l = new Laborer();
		l.setFirstname("Dan");
		l.setLastname("Haus");
		l.setAddress("add");
		l.setAfp(Afp.MODELO);
		l.setContractId(1);
		l.setDateAdmission(new Date());
		l.setDateBirth(new Date());
		l.setJob(Job.ALBAÑIL);
		l.setMaritalStatus(MaritalStatus.CASADO);
		l.setMobileNumber("33443");
		l.setPhone("0222");
		l.setRut("123-9");	

		service.saveLaborer(l);
		assertTrue("El id no puede ser nulo.", l.getLaborerId() != null );
		
		Laborer dbl = service.findLaborer(l.getLaborerId());		
		assertNotNull("El obrero no puede ser nulo", dbl);
		
		l.setMobileNumber("3334");	
		service.saveLaborer(l);
		
		dbl = service.findLaborer(l.getLaborerId());		
		assertEquals("Telefono debe ser igual", l.getMobileNumber(), dbl.getMobileNumber());		
				
	}
	
	/**
	 * Eliminar
	 */
	@Test
	public void testDelete() {
		
		Laborer l = new Laborer();
		l.setFirstname("Dan");
		l.setLastname("Haus");
		l.setAddress("add");
		l.setAfp(Afp.MODELO);
		l.setContractId(1);
		l.setDateAdmission(new Date());
		l.setDateBirth(new Date());
		l.setJob(Job.ALBAÑIL);
		l.setMaritalStatus(MaritalStatus.CASADO);
		l.setMobileNumber("33443");
		l.setPhone("0222");
		l.setRut("123-9");	

		service.saveLaborer(l);
		assertTrue("El id no puede ser nulo.", l.getLaborerId() != null );
				
		service.deleteLaborer(l.getLaborerId());		
		
		Laborer dbl = service.findLaborer(l.getLaborerId());		
				
		assertNull("El obrero debe ser nulo luego de la eliminacion", dbl );
		
	}
}
