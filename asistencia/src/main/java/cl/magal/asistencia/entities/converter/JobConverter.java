package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.Job;

@Converter(autoApply=true)
public class JobConverter implements AttributeConverter<Job, Integer>{

	@Override
	public Integer convertToDatabaseColumn(Job arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public Job convertToEntityAttribute(Integer arg0) {
		return Job.getJob(arg0);
	}

}
