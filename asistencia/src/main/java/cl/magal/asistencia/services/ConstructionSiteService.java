package cl.magal.asistencia.services;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Obra;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.LaborerRepository;
import cl.magal.asistencia.repositories.ObraRepository;
import cl.magal.asistencia.repositories.TeamRepository;
import cl.magal.asistencia.repositories.UserRepository;

@Service
public class ConstructionSiteService {

	Logger logger = LoggerFactory.getLogger(ConstructionSiteService.class);
	
	@Autowired
	ConstructionSiteRepository repo2;
	@Autowired
	LaborerRepository labRepo;
	@Autowired
	TeamRepository teamRepo;
	@Autowired
	UserRepository userRepo;
	
	@PostConstruct
	public void init(){
		//si no existe obras crea 2 de muestra
		
		List<ConstructionSite> obras = repo2.findAllNotDeteled();
		if( obras.isEmpty() ){

			ConstructionSite obra = new ConstructionSite();
			obra.setName("Edificio Jardines de Olivares");
			obra.setStatus(Status.ACTIVE);
			obra.setAddress("Av. Las Condes 93849");
			obra.setCode("21234");
			obra.setDeleted(false);
			
			repo2.save(obra);
			
			obra = new ConstructionSite();
			obra.setName("Edificio Parque Sebastián Elcano");
			obra.setStatus(Status.ACTIVE);
			obra.setAddress("Av. Pajaritos 3434");
			obra.setCode("564334");
			obra.setDeleted(false);
			
			repo2.save(obra);
		}
		
	}
	
	public void save(ConstructionSite obra) {
		repo2.save(obra);
	}
	
	public void save(Laborer laborer) {
		labRepo.save(laborer);
	}

	@Transactional
	public ConstructionSite findConstructionSite(Long id) {
		ConstructionSite cs =repo2.findOneNotDeleted(id);
		if(cs != null){
			//recupera la lista de trabajadores
			List<Laborer> lbs = labRepo.findByConstructionSite(id);
			cs.setLaborers(lbs);
		}
		return cs;
	}

	public Page<ConstructionSite> findAllConstructionSite(Pageable page) {
		return repo2.findAllNotDeteled(page);
	}

	public ConstructionSite findConstructionSiteByNombre(String nombre) {
		return repo2.findByName(nombre);
	}

	public ConstructionSite findConstructionSiteByDireccion(String direccion) {
		return repo2.findByComplicada(direccion).get(0);
	}
	
	public void deleteCS(Long id){
		ConstructionSite cs = repo2.findOne(id);
		if(cs == null )
			throw new RuntimeException("El elemento que se trata de eliminar no existe");
		cs.setDeleted(true);
		repo2.save(cs);
	}
	
	public Integer findRawStatusCS(Long id) {
		return (Integer) repo2.findRawStatusCS(id);
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
				Arrays.asList(new Laborer(),new Laborer(),new Laborer())
				);
		return page;
	}

	public List<Laborer> getLaborerByConstruction(ConstructionSite cs) {
		List<Laborer> laborers = labRepo.findByConstructionSite(cs.getConstructionsiteId());
		return laborers;
	}

	@Transactional
	public void addLaborerToConstructionSite(Laborer laborer, ConstructionSite cs) {
		
		ConstructionSite dbcs = repo2.findOne(cs.getConstructionsiteId());
		logger.debug("laborer "+laborer );
//		laborer.addConstructionSite(dbcs);
		labRepo.save(laborer); //FIXME
		dbcs.addLaborer(laborer);
		logger.debug("dbcs.getLaborer( ) "+dbcs.getLaborers() );
		repo2.save(dbcs);
	}

	@Transactional
	public void addTeamToConstructionSite(Team team, ConstructionSite cs) {
		
		ConstructionSite dbcs = repo2.findOne(cs.getConstructionsiteId());		
		logger.debug("dbcs "+dbcs);
		teamRepo.save(team);
		dbcs.addTeam(team);	
		logger.debug("dbcs team "+dbcs.getTeams());
	}

	public List<Team> getTeamsByConstruction(ConstructionSite cs) {
		logger.debug("cs "+cs);
		List<Team> teamAlls = (List<Team>) teamRepo.findAll();
		for(Team t : teamAlls){
			logger.debug("t "+t);
			logger.debug("t.getConstructionsite "+t.getConstructionsite());
		}
		List<Team> teams = teamRepo.findByConstructionsite(cs);
		logger.debug("teams "+teams);
		return teams;
	}

	public List<User> getAllUsers() {
		return (List<User>) userRepo.findAllNotDeteled();
	}
	
	/**
	 * permite borrar toda la tabla
	 */
	public void clear() {
		repo2.deleteAll();
	}

}
