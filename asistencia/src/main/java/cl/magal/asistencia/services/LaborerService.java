package cl.magal.asistencia.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.repositories.LaborerRepository;

@Service
public class LaborerService {

	@Autowired
	LaborerRepository rep;
	
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
	
	public List<Laborer> findAllLaborer() {
		return (List<Laborer>) rep.findAll();
	}

}
