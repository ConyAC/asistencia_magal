package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.MaritalStatus;
import cl.magal.asistencia.util.Utils;

public final class LaborerHelper {
	
	final static private String NOMBRE = "Trabajador";
	
	public static Laborer newLaborer(){
		Laborer laborer = new Laborer();
//		laborer.setLaborerId((long) Utils.random());
		laborer.setFirstname(NOMBRE+Utils.random());
		laborer.setJob(Job.ALBAÑIL);
		laborer.setAfp(Afp.MODELO);
		laborer.setMaritalStatus(MaritalStatus.CASADO);
		//TODO estado
		return laborer;
	}

	public static void verify(Laborer laborer) {
		
		assertNotNull("El trabajador no puede ser nulo",laborer);
		assertNotNull("El id del trabajador no puede ser nulo",laborer.getLaborerId());
		assertNotNull("El nombre del trabajador no puede ser nulo",laborer.getFirstname());
	}
}
