package cl.magal.asistencia.services;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.LaborerConstructionsiteRepository;
import cl.magal.asistencia.repositories.LaborerRepository;
import cl.magal.asistencia.repositories.TeamRepository;
import cl.magal.asistencia.repositories.UserRepository;

@Service
public class ConstructionSiteService {

	Logger logger = LoggerFactory.getLogger(ConstructionSiteService.class);
	
	@Autowired
	ConstructionSiteRepository constructionSiterepo;
	@Autowired
	LaborerRepository labRepo;
	@Autowired
	LaborerConstructionsiteRepository labcsRepo;
	@Autowired
	TeamRepository teamRepo;
	@Autowired
	UserRepository userRepo;
	
	@PostConstruct
	public void init(){
	}
	
	public void save(ConstructionSite obra) {
		constructionSiterepo.save(obra);
	}
	
	public void save(Laborer laborer) {
		labRepo.save(laborer);
	}

	public ConstructionSite findConstructionSite(Long id) {
		ConstructionSite cs =constructionSiterepo.findOneNotDeleted(id);
		if(cs != null){
			//recupera la lista de trabajadores
			List<Laborer> lbs = labRepo.findByConstructionSite(id);
			cs.setLaborers(lbs);
		}
		return cs;
	}
	
	public List<ConstructionSite> findAllConstructionSite() {
		return constructionSiterepo.findAllNotDeteled();
	}

	public Page<ConstructionSite> findAllConstructionSite(Pageable page) {
		return constructionSiterepo.findAllNotDeteled(page);
	}

	public ConstructionSite findConstructionSiteByNombre(String nombre) {
		return constructionSiterepo.findByName(nombre);
	}

	public ConstructionSite findConstructionSiteByDireccion(String direccion) {
		return constructionSiterepo.findByComplicada(direccion).get(0);
	}
	
	public void deleteCS(Long id){
		ConstructionSite cs = constructionSiterepo.findOne(id);
		if(cs == null )
			throw new RuntimeException("El elemento que se trata de eliminar no existe");
		cs.setDeleted(true);
		constructionSiterepo.save(cs);
	}
	
//	public Integer findRawStatusCS(Long id) {
//		return (Integer) constructionSiterepo.findRawStatusCS(id);
//	}
	
	public Page<Laborer> findLaborerByConstruction(ConstructionSite fisrt) {
		Page<Laborer> page = new PageImpl<Laborer>(
				Arrays.asList(new Laborer(),new Laborer(),new Laborer())
				);
		return page;
	}

	public List<LaborerConstructionsite> getLaborerByConstruction(ConstructionSite cs) {
		List<LaborerConstructionsite> laborers = labcsRepo.findByConstructionsite(cs);
		return laborers;
	}

	public void addLaborerToConstructionSite(Laborer laborer, ConstructionSite cs) {
		
		ConstructionSite dbcs = constructionSiterepo.findOne(cs.getConstructionsiteId());
		logger.debug("laborer "+laborer );
//		laborer.addConstructionSite(dbcs);
		labRepo.save(laborer); //FIXME
//		dbcs.addLaborer(laborer);
		logger.debug("dbcs.getLaborer( ) "+dbcs.getLaborers() );
		constructionSiterepo.save(dbcs);
	}

	public void addTeamToConstructionSite(Team team, ConstructionSite cs) {
		
		ConstructionSite dbcs = constructionSiterepo.findOne(cs.getConstructionsiteId());		
		logger.debug("dbcs "+dbcs);
		teamRepo.save(team);
//		dbcs.addTeam(team);	
//		logger.debug("dbcs team "+dbcs.getTeams());
	}

//	public List<Team> getTeamsByConstruction(ConstructionSite cs) {
//		logger.debug("cs "+cs);
//		List<Team> teamAlls = (List<Team>) teamRepo.findAll();
//		for(Team t : teamAlls){
//			logger.debug("t "+t);
//		}
//		List<Team> teams = teamRepo.findByConstructionsite(cs);
//		logger.debug("teams "+teams);
//		return teams;
//	}

	public List<User> getAllUsers() {
		return (List<User>) userRepo.findAllNotDeteled();
	}
	
	/**
	 * permite borrar toda la tabla
	 */
	public void clear() {
		constructionSiterepo.deleteAll();
	}

}
