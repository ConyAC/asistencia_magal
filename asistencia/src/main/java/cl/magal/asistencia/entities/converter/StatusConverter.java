package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.Status;

@Converter
public class StatusConverter implements AttributeConverter<Status, Integer> {

	@Override
	public Integer convertToDatabaseColumn(Status arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public Status convertToEntityAttribute(Integer arg0) {
		return Status.getStatus(arg0);
	}
}
