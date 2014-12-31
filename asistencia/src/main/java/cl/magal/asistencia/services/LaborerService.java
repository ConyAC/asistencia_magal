package cl.magal.asistencia.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.LaborerConstructionsiteRepository;
import cl.magal.asistencia.repositories.LaborerRepository;
import cl.magal.asistencia.ui.workerfile.vo.HistoryVO;
import cl.magal.asistencia.util.Utils;

@Service
public class LaborerService {

	@Autowired
	LaborerRepository rep;
	@Autowired
	LaborerConstructionsiteRepository repConstruction;
	@Autowired
	ConstructionSiteRepository constructionSiteRepo;
	
	public void saveLaborer(Laborer l) {
		rep.save(l);
	}
	
	public Laborer findLaborer(Long id){
		return rep.findOne(id);
	}
	
	public Integer findRawJobLaborer(Long id) {
		return (Integer) rep.findRawJobLaborer(id);
	}
	
	public void deleteLaborer(Long id){
		rep.delete(id);
	}
	
	public Page<LaborerConstructionsite> findAllLaborerConstructionsite(Pageable page) {
		return repConstruction.findAll(page);
	}

	public List<HistoryVO> getLaborerHistory(Laborer laborer) {
		
		List<HistoryVO> result =  new ArrayList<HistoryVO>();
		//busca todas las obras en que ha trabajado
		List<ConstructionSite> constructionSites = constructionSiteRepo.findByLaborers(laborer);
		laborer.setConstructionSites(constructionSites);
		//por cada una crea un vo
		for(ConstructionSite constructionSite : constructionSites ){
			HistoryVO vo = new HistoryVO();
			vo.setConstructionSite(constructionSite);
			//considerar el job historico
			vo.setJob(laborer.getJob()); 
			vo.setAverageWage(Double.valueOf(Utils.random(9000, 15000)));
			vo.setNumberOfAccidents(Utils.random(0, 15));
			vo.setReward(Double.valueOf(Utils.random(19000, 150000)));
			result.add(vo);
		}
		
		return result;
	}

	public LaborerConstructionsite findLaborerOnConstructionSite(
			LaborerConstructionsite lc) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(LaborerConstructionsite laborer) {
		// TODO Auto-generated method stub
		
	}
}
