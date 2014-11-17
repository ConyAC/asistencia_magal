package cl.magal.asistencia.services;

import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.util.Utils;

public final class UserHelper {
	
	final static private String RUT = "16127401-1";
	final static private String NOMBRE = "Nombre";
	final static private String APELLIDO = "Apellido";
	final static private String EMAIL = "a@b.com";
	
	public static User newUser(){
		User user = new User();
		user.setRole(cl.magal.asistencia.entities.enums.Role.ADM_CENTRAL);
		user.setRut(RUT);
		user.setFirstname(NOMBRE+Utils.random());
		user.setLastname(APELLIDO+Utils.random());
		user.setEmail(EMAIL);
		//TODO estado
		return user;
	}
}