package cl.magal.asistencia.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Obra;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.LaborerRepository;
import cl.magal.asistencia.repositories.ObraRepository;

@Service
public class ObrasService {

	
	@Autowired
	ConstructionSiteRepository repo2;
	@Autowired
	LaborerRepository labRepo;
	
	public void save(ConstructionSite obra) {
		repo2.save(obra);
	}
	
	public void save(Laborer laborer) {
		labRepo.save(laborer);
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

	public Page<Laborer> findLaborerByConstruction(ConstructionSite fisrt) {
		Page<Laborer> page = new PageImpl<Laborer>(
				Arrays.asList(LaborerHelper.newLaborer(),LaborerHelper.newLaborer(),LaborerHelper.newLaborer())
				);
		return page;
	}

	public List<Laborer> getLaborerByConstruction(ConstructionSite cs) {
		List<Laborer> laborers = labRepo.findByConstructionSite(cs.getConstructionsiteId());
		return laborers;
	}

	public void addLaborer(Laborer laborer, ConstructionSite cs) {
		labRepo.save(laborer);
		cs.getLaborers().add(laborer);
		repo2.save(cs);
		
	}

}
