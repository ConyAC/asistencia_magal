package cl.magal.asistencia.services;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.util.Utils;

public final class LaborerHelper {
	
	final static private String NOMBRE = "Trabajador";
	final static private String ESTADO = "Contratado";
	
	public static Laborer newLaborer(){
		Laborer laborer = new Laborer();
		laborer.setLaborerId(Utils.random());
		laborer.setFirstname(NOMBRE+Utils.random());
		laborer.setJobId(Utils.random(0,800));
		//TODO estado
		return laborer;
	}
}
