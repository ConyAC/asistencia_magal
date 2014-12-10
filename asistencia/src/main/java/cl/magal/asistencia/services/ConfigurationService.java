package cl.magal.asistencia.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.repositories.WageConfigurationsRepository;
import cl.magal.asistencia.repositories.DateConfigurationsRepository;

@Service
public class ConfigurationService {

	@Autowired
	DateConfigurationsRepository repo;
	@Autowired
	WageConfigurationsRepository configRepo;
	
	public DateConfigurations findDateConfigurationsByDate(Date value) {
		return repo.findByDate(value);
	}

	public void save(DateConfigurations bean) {
		repo.save(bean);
	}

	public WageConfigurations findConfigurations() {
		List<WageConfigurations> configurations = (List<WageConfigurations>)configRepo.findAll();
		if(configurations.isEmpty())
			return null;
		return configurations.get(0);
	}

	public void save(WageConfigurations configurationDate) {
		configRepo.save(configurationDate);
	}

}
