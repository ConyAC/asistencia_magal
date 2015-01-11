package cl.magal.asistencia.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.repositories.LaborerRepository;
import cl.magal.asistencia.repositories.TeamRepository;

@Service
public class TeamService {

	Logger logger = LoggerFactory.getLogger(TeamService.class);
	
	@Autowired
	TeamRepository rep;
	
	@Autowired
	LaborerRepository labRepo;
	
	public void saveTeam(Team t) {
		rep.save(t);
	}
	
	public Team findTeam(Long id){
		return rep.findOne(id);
	}
	
	public void deleteTeam(Long id){
		rep.delete(id);
	}
	
	public Page<Team> findAllTeam(Pageable page) {
		return rep.findAll(page);
	}
	
	public void addLaborerToTeam(Laborer laborer, Team t) {		
		Team dbt = rep.findOne(t.getTeamId());
//		laborer.setTeamId(t.getTeamId());
		labRepo.save(laborer);
		rep.save(dbt);
	}
	
	public List<Laborer> getLaborerByTeam(Team t) {
		List<Laborer> l = labRepo.findByTeam(t.getTeamId());
		return l;
	}
}
