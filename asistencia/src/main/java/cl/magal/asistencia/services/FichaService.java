
package cl.magal.asistencia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;

@Service
public class FichaService {

	@Autowired
	ConstructionSiteRepository rep;
	
	public void saveConstructionSite(ConstructionSite cs) {
		rep.save(cs);
	}
	
	public ConstructionSite findConstructionSite(Long id){
		return rep.findOneNotDeleted(id);
	}
	
	public ConstructionSite findByAddress(String address){
		return rep.findByAddress(address).get(0);
	}
	
/*
	public Obra findObraByNombre(String nombre) {
		return repo.findByNombre(nombre);
	}

	public Obra findObraByDireccion(String direccion) {
		return repo.findByComplicada(direccion).get(0);
	}
*/
}
