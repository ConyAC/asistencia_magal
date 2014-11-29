package cl.magal.asistencia.helpers;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.util.Utils;

public final class LaborerHelper {
	
	final static private String NOMBRE = "Trabajador";
	
	public static Laborer newLaborer(){
		Laborer laborer = new Laborer();
		laborer.setLaborerId((long) Utils.random());
		laborer.setFirstname(NOMBRE+Utils.random());
		laborer.setJob(Job.ALBAÑIL);
		//TODO estado
		return laborer;
	}
}
