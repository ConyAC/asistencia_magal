package cl.magal.asistencia.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	transient Logger logger = LoggerFactory.getLogger(LaborerService.class);

	@Autowired
	LaborerRepository laborerRepo;
	@Autowired
	LaborerConstructionsiteRepository laborerConstructionsiteRepo;
	@Autowired
	ConstructionSiteRepository constructionSiteRepo;
	
	public Laborer saveLaborer(Laborer l) {
		Laborer laborer = laborerRepo.save(l);
		return laborer;
	}
	
	public Laborer findLaborer(Long id){
		return laborerRepo.findOne(id);
	}
	
	public Integer findRawJobLaborer(Long id) {
		return (Integer) laborerRepo.findRawJobLaborer(id);
	}
	
	public void delete(Laborer laborer){
		laborerRepo.delete(laborer);
	}
	
	public Page<LaborerConstructionsite> findAllLaborerConstructionsite(Pageable page) {
		return laborerConstructionsiteRepo.findAll(page);
	}

	public List<HistoryVO> getLaborerHistory(Laborer laborer) {
		
		List<HistoryVO> result =  new ArrayList<HistoryVO>();
		//busca todas las obras en que ha trabajado
		List<ConstructionSite> constructionSites = constructionSiteRepo.findByLaborers(laborer);
//		laborer.setConstructionSites(constructionSites);
		//por cada una crea un vo
		for(ConstructionSite constructionSite : constructionSites ){
			HistoryVO vo = new HistoryVO();
			vo.setConstructionSite(constructionSite);
			//considerar el job historico
//			vo.setJob(laborer.getJob()); 
			vo.setAverageWage(Double.valueOf(Utils.random(9000, 15000)));
			vo.setNumberOfAccidents(Utils.random(0, 15));
			vo.setReward(Double.valueOf(Utils.random(19000, 150000)));
			result.add(vo);
		}
		
		return result;
	}

	/**
	 * Permite recuperar la información de un obrero en una obra en particular
	 * @param constructionsite
	 * @param laborer
	 * @return
	 */
	public LaborerConstructionsite findLaborerOnConstructionSite(ConstructionSite constructionsite,Laborer laborer) {
		return laborerConstructionsiteRepo.findByConstructionsiteAndLaborer(constructionsite,laborer);
	}

	/**
	 * Permite guardar toda la información de un obrero dentro de una obra
	 * @param laborerConstructionSite
	 */
	public void save(LaborerConstructionsite laborerConstructionSite) {
		laborerConstructionsiteRepo.save(laborerConstructionSite);
//		//si no existe, guarda el objeto laborar-constructionsite
//		if(laborerConstructionSite.getId() == null )
//			laborerConstructionsiteRepo.save(laborerConstructionSite);
//		//guarda los elementos importantes
//		laborerRepo.save(laborerConstructionSite.getLaborer());
		
	}
}
