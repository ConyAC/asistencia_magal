package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import cl.magal.asistencia.entities.Role;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.UserStatus;
import cl.magal.asistencia.util.Utils;

public final class UserHelper {
	
	final static private String RUT = "16127401-1";
	final static private String NOMBRE = "Nombre";
	final static private String APELLIDO = "Apellido";
	final static private String EMAIL = "a@b.com";
	
	public static User newUser(){
		User user = new User();
		Role role = RoleHelper.newRole();
		user.setRole(role);
		user.setRut(RUT);
		user.setFirstname(NOMBRE+Utils.random());
		user.setLastname(APELLIDO+Utils.random());
		user.setEmail(EMAIL);
		user.setDeleted(false);
		user.setStatus(UserStatus.ACTIVE);
		user.setPassword("123456");
		//TODO estado
		return user;
	}
	
	public static void verify(User u){
		verify(u,false); 
	}
	
	public static void verify(User u,boolean ignoreStatus) {
		
		assertNotNull("El usuario no puede ser nulo.", u);
		assertNotNull("El id de usuario no puede ser nulo.", u.getUserId());
		assertNotNull("El nombre no puede ser nulo.", u.getFirstname());
		assertNotNull("El apellido no puede ser nulo.", u.getLastname());
		assertNotNull("El rut no puede ser nulo.", u.getRut());
		//verificar que el tipo del estado sea del tipo enum Status
		assertTrue("El tipo de estado debe ser enum", u.getStatus().getClass() == UserStatus.class);
		if(!ignoreStatus)
			assertTrue("El enum debe ser igual al guardado", u.getStatus() == UserStatus.ACTIVE);	
	}
	
	public static void verify(User u, User bdu) {
		verify(u);
		verify(bdu);
		
		assertSame("Los ids deben ser iguales.", u.getUserId(), bdu.getUserId());
		assertEquals("El rut debe ser el mismo.", u.getRut(), bdu.getRut());
		//verificar que el tipo del estado sea del tipo enum Status
		assertSame("El tipo de estado debe ser el mismo", u.getStatus().getClass() , bdu.getStatus().getClass());
		assertSame("El enum debe ser el mismo", u.getStatus(), bdu.getStatus());
	}

}
