package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import cl.magal.asistencia.entities.Role;
import cl.magal.asistencia.entities.enums.Permission;

public class RoleHelper {

	public static Role newRole() {
		Role role = new Role();
		role.setName("ADM");
		role.setRoleId(1L);		
		Set<Permission> perm = new HashSet<Permission>();	
		perm.add(Permission.CREAR_OBRA);
		perm.add(Permission.EDITAR_OBRA);
		perm.add(Permission.ELIMINAR_OBRA);
		role.setPermission(perm);			
		
		return role;
	}

	public static void verify(Role r) {
		
		assertNotNull("El rol no puede ser nulo.", r);
		assertNotNull("El id de role no puede ser nulo.", r.getRoleId());
		assertNotNull("El nombre no puede ser nulo.", r.getName());
		//verificar que el tipo del permiso sea del tipo enum Permission		
		for(Permission p : r.getPermission()){
			assertTrue("El permiso debe ser enum", p.getClass() == Permission.class);
		}			
	}
	
}
