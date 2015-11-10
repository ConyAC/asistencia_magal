package cl.magal.asistencia.services;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AdvancePaymentItem;
import cl.magal.asistencia.entities.AfpAndInsuranceConfigurations;
import cl.magal.asistencia.entities.AfpItem;
import cl.magal.asistencia.entities.Bank;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.TaxationConfigurations;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.repositories.AdvancePaymentRepository;
import cl.magal.asistencia.repositories.AfpAndInsuranceRepository;
import cl.magal.asistencia.repositories.AfpItemRepository;
import cl.magal.asistencia.repositories.BankRepository;
import cl.magal.asistencia.repositories.DateConfigurationsRepository;
import cl.magal.asistencia.repositories.FamilyAllowanceRepository;
import cl.magal.asistencia.repositories.TaxationRepository;
import cl.magal.asistencia.repositories.WageConfigurationsRepository;

@Service
public class ConfigurationService {
	
	Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

	@Autowired
	DateConfigurationsRepository dateConfigurationsRepo;
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
	@Autowired
	AfpItemRepository afpItemRepo;
	@Autowired
	BankRepository bankRepo;


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
		List<AfpItem> item = (List<AfpItem>) afpItemRepo.findAll();
		configurations.get(0).setAfpTable(item);
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
	
	public DateConfigurations findDateConfigurationsByDate(Date value) {
		return dateConfigurationsRepo.findByDate(value);
	}

	public DateConfigurations getDateConfigurationByCsAndMonth(ConstructionSite cs,DateTime date) {
//		logger.debug("buscando configuraciones de la fecha {}",date.toDate());
		DateConfigurations dateConfig = dateConfigurationsRepo.findByDate(date.toDate());
		//si no se ha seteado la fecha, elije el último día del mes anterior
		if( dateConfig == null ){
			dateConfig = new DateConfigurations();
		}
		return dateConfig;
	}
	
	public void save(DateConfigurations bean) {
		dateConfigurationsRepo.save(bean);
	}

	public Double getPermissionDiscount(ConstructionSite cs) {
		List<AdvancePaymentConfigurations> config = (List<AdvancePaymentConfigurations>) advancePaymentRepo.findAll();
		return config.get(0).getPermissionDiscount();
	}

	public Double getFailDiscount(ConstructionSite cs) {
		List<AdvancePaymentConfigurations> config = (List<AdvancePaymentConfigurations>) advancePaymentRepo.findAll();
		return config.get(0).getFailureDiscount();
	}

	public AdvancePaymentConfigurations getSupleTableByCs(ConstructionSite cs) {
		List<AdvancePaymentConfigurations> config = (List<AdvancePaymentConfigurations>) advancePaymentRepo.findAdvancePaymentConfigurationsByCS(cs);
		if(config.isEmpty()){
			AdvancePaymentConfigurations apc = new AdvancePaymentConfigurations();
			apc.setFailureDiscount(0d);
			apc.setPermissionDiscount(0d);
			AdvancePaymentItem api = new AdvancePaymentItem();
			api.setSupleCode(1);
			api.setSupleNormalAmount(105000d);
			Map<Integer, AdvancePaymentItem> map = new HashMap<Integer, AdvancePaymentItem>();
			map.put(1, api);
			apc.setAdvancePaymentTable(Arrays.asList(api));
			apc.setMapTable(map);
			return apc;
		}
		AdvancePaymentConfigurations table = config.get(0);

		Map<Integer,AdvancePaymentItem> map = new HashMap<Integer,AdvancePaymentItem>();
		for(AdvancePaymentItem advancePaymentItem : table.getAdvancePaymentTable() ){
			map.put(advancePaymentItem.getSupleCode(), advancePaymentItem);
		}
		table.setMapTable(map);

		return table;

	}

	public void delete(FamilyAllowanceConfigurations family) {
		familyAllowanceRepo.delete(family);
	}
	
	public AdvancePaymentConfigurations findAdvancePaymentConfigurationsByCS(ConstructionSite cs) {
		logger.debug("obteniendo tabla de suple");
		List<AdvancePaymentConfigurations> configurations = (List<AdvancePaymentConfigurations>)advancePaymentRepo.findAdvancePaymentConfigurationsByCS(cs);
		if(configurations.isEmpty())
			return new AdvancePaymentConfigurations();
		return configurations.get(0);
	}
	
	public void delete(AfpItem afpItem) {
		afpItemRepo.delete(afpItem);
	}
	
	public void save(AfpItem afpItem) {
		afpItemRepo.save(afpItem);
	}
	
	public void delete(Bank bank) {
		bankRepo.delete(bank);
	}
	
	public void save(Bank bank) {
		bankRepo.save(bank);
	}
	
	public List<Bank> findBank() {
		return (List<Bank>) bankRepo.findAll();
	}
	
	public void delete(TaxationConfigurations tcItem) {
		taxationRepo.delete(tcItem);
	}
}
