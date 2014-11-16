package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.MaritalStatus;

@Converter
public class MaritalStatusConverter implements AttributeConverter<MaritalStatus, Integer>{

	@Override
	public Integer convertToDatabaseColumn(MaritalStatus arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public MaritalStatus convertToEntityAttribute(Integer arg0) {
		return MaritalStatus.getMaritalStatus(arg0);
	}

}