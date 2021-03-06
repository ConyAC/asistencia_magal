package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.Isapre;
import cl.magal.asistencia.entities.enums.MaritalStatus;
import cl.magal.asistencia.entities.enums.Nationality;
import cl.magal.asistencia.util.Utils;

public final class LaborerHelper {
	
	final static private String NOMBRE = "Trabajador";
	final static private String APELLIDO = "Soto";
	
	public static Laborer newLaborer(){
		Laborer laborer = new Laborer();
		laborer.setFirstname(NOMBRE+Utils.random());
		laborer.setLastname(APELLIDO+Utils.random());
//		laborer.setJob(Job.ALBAÑIL);
		laborer.getAfp().setName("Modelo");
		laborer.setMaritalStatus(MaritalStatus.CASADO);
		laborer.setRut("16627054-5");
		laborer.setMobileNumber("11223311");
		laborer.setIsapre(Isapre.FONASA);
		laborer.setNationality(Nationality.CHILENA);
		laborer.setAddress("Av. Los Alamos 3663");
		laborer.setCommune("Puente Alto");
//		laborer.setDateAdmission(new Date());
		//TODO estado
		return laborer;
	}

	public static void verify(Laborer laborer) {
		
		assertNotNull("El trabajador no puede ser nulo",laborer);
		assertNotNull("El id del trabajador no puede ser nulo",laborer.getId());
		assertNotNull("El nombre del trabajador no puede ser nulo",laborer.getFirstname());
	}
	
	public static void verify(Laborer l, Laborer bdl) {
		assertNotNull("El l no puede ser nulo.",l);
		assertNotNull("El bdl no puede ser nulo.",bdl);
		
		assertSame("El id debe ser el mismo.",l.getId(),bdl.getId());
		assertEquals("El nombre debe ser el mismo.",l.getFirstname(),bdl.getFirstname());
		
//		//verificar tipo enum 	
//		assertSame("El oficio debe ser enum", l.getJob().getClass() , bdl.getJob().getClass());
//		assertSame("El enum debe ser igual al guardado",  l.getJob() ,bdl.getJob());
				
		assertSame("El ms debe ser enum", l.getMaritalStatus().getClass(), bdl.getMaritalStatus().getClass());
		assertSame("El enum debe ser igual al guardado", l.getMaritalStatus() ,bdl.getMaritalStatus());
			
		assertSame("La afp debe ser enum", l.getAfp().getClass(), bdl.getAfp().getClass());
		assertSame("El enum debe ser igual al guardado", l.getAfp() ,bdl.getAfp());
	}
}
