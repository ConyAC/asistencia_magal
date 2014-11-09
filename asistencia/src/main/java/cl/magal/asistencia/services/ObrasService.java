package cl.magal.asistencia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Obra;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.ObraRepository;

@Service
public class ObrasService {

	
	@Autowired
	ConstructionSiteRepository repo2;
	
	public void save(ConstructionSite obra) {
		repo2.save(obra);
	}

	public ConstructionSite findConstructionSite(Long id) {
		return repo2.findOne(id);
	}

	public Page<ConstructionSite> findAllConstructionSite(Pageable page) {
		return repo2.findAll(page);
	}

	public ConstructionSite findConstructionSiteByNombre(String nombre) {
		return repo2.findByName(nombre);
	}

	public ConstructionSite findConstructionSiteByDireccion(String direccion) {
		return repo2.findByComplicada(direccion).get(0);
	}
	
	@Autowired
	@Deprecated
	ObraRepository repo;
	
	public void saveObra(Obra obra) {
		repo.save(obra);
	}

	public Obra findObra(Long id) {
		return repo.findOne(id);
	}

	public Page<Obra> findAllObra(Pageable page) {
		return repo.findAll(page);
	}

	public Obra findObraByNombre(String nombre) {
		return repo.findByNombre(nombre);
	}

	public Obra findObraByDireccion(String direccion) {
		return repo.findByComplicada(direccion).get(0);
	}

}
