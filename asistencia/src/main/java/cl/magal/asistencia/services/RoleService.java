package cl.magal.asistencia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.Role;
import cl.magal.asistencia.repositories.RoleRepository;

@Service
public class RoleService {

	@Autowired
	RoleRepository rep;
	
	public void saveRole(Role r) {
		rep.save(r);
	}
	
	public Role findRole(Long id){
		return rep.findOne(id);
	}
	
	public void deleteRole(Long id){
		rep.delete(id);
	}

}
