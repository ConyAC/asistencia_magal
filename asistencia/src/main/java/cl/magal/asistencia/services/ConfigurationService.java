package cl.magal.asistencia.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

	



import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AfpAndInsuranceConfigurations;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.TaxationConfigurations;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.repositories.AdvancePaymentRepository;
import cl.magal.asistencia.repositories.AfpAndInsuranceRepository;
import cl.magal.asistencia.repositories.DateConfigurationsRepository;
import cl.magal.asistencia.repositories.FamilyAllowanceRepository;
import cl.magal.asistencia.repositories.TaxationRepository;
import cl.magal.asistencia.repositories.WageConfigurationsRepository;
import cl.magal.asistencia.util.Utils;

@Service
public class ConfigurationService {
	
	Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

	@Autowired
	DateConfigurationsRepository repo;
	@Autowired
	WageConfigurationsRepository wageconfigRepo;
	@Autowired
	AdvancePaymentRepository advancePaymentRepo;
	@Autowired
	AfpAndInsuranceRepository afpInsuranceRepo;
	@Autowired
	TaxationRepository taxationRepo;
	@Autowired
	FamilyAllowanceRepository familyAllowanceRepo;
	
	public DateConfigurations findDateConfigurationsByDate(Date value) {
		return repo.findByDate(value);
	}

	public void save(DateConfigurations bean) {
		repo.save(bean);
	}

	public WageConfigurations findWageConfigurations() {
		List<WageConfigurations> configurations = (List<WageConfigurations>)wageconfigRepo.findAll();
		if(configurations.isEmpty())
			return null;
		return configurations.get(0);
	}

	public void save(WageConfigurations configurationDate) {
		wageconfigRepo.save(configurationDate);
	}
	
	public AdvancePaymentConfigurations findAdvancePaymentConfigurations() {
		List<AdvancePaymentConfigurations> configurations = (List<AdvancePaymentConfigurations>)advancePaymentRepo.findAll();
		if(configurations.isEmpty())
			return null;
		return configurations.get(0);
	}

	public void save(AdvancePaymentConfigurations bean) {
		advancePaymentRepo.save(bean);
	}

	public void save(AfpAndInsuranceConfigurations afpAndInsuranceConfiguration) {
		afpInsuranceRepo.save(afpAndInsuranceConfiguration);
	}

	public AfpAndInsuranceConfigurations findAfpAndInsuranceConfiguration() {
		List<AfpAndInsuranceConfigurations> configurations = (List<AfpAndInsuranceConfigurations>)afpInsuranceRepo.findAll();
		if(configurations.isEmpty())
			return null;
		return configurations.get(0);
	}

	public void save(TaxationConfigurations tax) {
		taxationRepo.save(tax);
	}

	public List<TaxationConfigurations> findTaxationConfigurations() {
		return (List<TaxationConfigurations>) taxationRepo.findAll();
	}

	public void save(FamilyAllowanceConfigurations familyAllowance) {
		familyAllowanceRepo.save(familyAllowance);
	}

	public List<FamilyAllowanceConfigurations> findFamylyAllowanceConfigurations() {
		return (List<FamilyAllowanceConfigurations>) familyAllowanceRepo.findAll();
	}

}
