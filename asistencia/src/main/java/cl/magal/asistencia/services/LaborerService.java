package cl.magal.asistencia.services;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.repositories.AbsenceRepositoy;
import cl.magal.asistencia.repositories.AccidentRepository;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.ContractRepository;
import cl.magal.asistencia.repositories.LaborerConstructionsiteRepository;
import cl.magal.asistencia.repositories.LaborerRepository;
import cl.magal.asistencia.repositories.LoanRepository;
import cl.magal.asistencia.repositories.ToolRepository;
import cl.magal.asistencia.repositories.VacationRepository;
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
	@Autowired
	AbsenceRepositoy absenceRepo;
	@Autowired
	VacationRepository vacationRepo;
	@Autowired
	AccidentRepository accidentRepo;
	@Autowired
	ToolRepository toolRepo;
	@Autowired
	ContractRepository contractRepo;
	@Autowired
	LoanRepository loanRepo; 
	
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
	public Page<Laborer> findAllLaborer(Pageable page) {
		return laborerRepo.findAll(page);
	}

	public List<HistoryVO> getLaborerHistory(Laborer laborer) {
		
		List<HistoryVO> result =  new ArrayList<HistoryVO>();
		//busca todas las obras en que ha trabajado
		List<LaborerConstructionsite> laborerConstructionsites = laborerConstructionsiteRepo.findByLaborer(laborer);
//		laborer.setConstructionSites(constructionSites);
		//por cada una crea un vo
		for(LaborerConstructionsite laborerConstructionsite : laborerConstructionsites ){
			//por cada contrato
			for(Contract contract : laborerConstructionsite.getContracts() ){
				HistoryVO vo = new HistoryVO();
				vo.setConstructionSite(laborerConstructionsite.getConstructionsite());
				//considerar el job historico
				vo.setJob(contract.getJob()); 
				vo.setNumberOfAccidents(laborerConstructionsite.getAccidents().size());
				vo.setStartingDate(contract.getStartDate());
				vo.setEndingDate(contract.getTerminationDate());
				vo.setActive(contract.isActive());
				//TODO
				vo.setReward(Double.valueOf(Utils.random(19000, 150000)));
				vo.setAverageWage(Double.valueOf(Utils.random(9000, 15000)));
				
				result.add(vo);
			}
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
		//si es nulo, da error
		if(laborerConstructionSite == null)
			throw new RuntimeException("La relación trabajador-obra no puede ser nula");
		//es necesario que tenga algún contrato
		if(laborerConstructionSite.getContracts().isEmpty())
			throw new RuntimeException("La relación trabajador-obra debe tener al menos un contrato asociado");
		//guarda los contratos
		laborerConstructionsiteRepo.save(laborerConstructionSite);		
	}

	public List<Laborer> findAllLaborer() {
		return (List<Laborer>) laborerRepo.findAll();
	}

	public List<Laborer> getAllLaborerExceptThisConstruction(ConstructionSite constructionsite) {
		return laborerRepo.findAllExceptThisConstruction(constructionsite);
	}

	public void remove(LaborerConstructionsite laborerConstruction) {
		try{
			laborerConstructionsiteRepo.delete(laborerConstruction);
		}catch(TransactionSystemException e){
			ConstraintViolationException e1 = (ConstraintViolationException) e.getCause().getCause();
			logger.error("TransactionSystemException {}",e1);
		}
	}

	public Integer getNextJobCode(Job value, ConstructionSite constructionsite) {
		Integer jobCode =  contractRepo.findJobCodeByConstructionsiteAndBetweenMinAndMax( constructionsite, value.getMin(),value.getMax());
		return jobCode != null ? jobCode + 1 : value.getMin();
	}

	public List<LaborerConstructionsite> findAllLaborerConstructionsiteByLaborer(Laborer laborer) {
		return laborerConstructionsiteRepo.findByLaborer(laborer);
	}

	/**
	 * 
	 * @param laborer
	 * @return
	 */
	public ConstructionSite getLastConstructionSite(Laborer laborer) {
		LaborerConstructionsite cs = laborerConstructionsiteRepo.findFirstByLaborerOrderByContractsStartDateDesc(laborer);
		if(cs != null)
			return cs.getConstructionsite();
		return null;
	}

	
}
