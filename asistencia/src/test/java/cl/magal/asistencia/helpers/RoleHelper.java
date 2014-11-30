package cl.magal.asistencia.helpers;

import cl.magal.asistencia.entities.Role;

public class RoleHelper {

	public static Role newRole() {
		Role role = new Role();
		role.setName("ADM");
		role.setRoleId(1L);
		return role;
	}

	
}
