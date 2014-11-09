package cl.magal.asistencia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Obra;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.ObraRepository;

@Service
public class FichaService {

	@Autowired
	ConstructionSiteRepository rep;
	
	ObraRepository repo;
	
	public void saveConstructionSite(ConstructionSite cs) {
		rep.save(cs);
	}
	
	public void saveObra(Obra obra) {
		repo.save(obra);
	}

	public Obra findObra(Long id) {
		return repo.findOne(id);
	}

	public Page<Obra> findAllObra(Pageable page) {
		return repo.findAll(page);
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
