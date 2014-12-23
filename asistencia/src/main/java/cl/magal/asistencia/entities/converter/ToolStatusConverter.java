package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.ToolStatus;

@Converter
public class ToolStatusConverter implements AttributeConverter<ToolStatus, Integer> {

	@Override
	public Integer convertToDatabaseColumn(ToolStatus arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public ToolStatus convertToEntityAttribute(Integer arg0) {
		return ToolStatus.getToolStatus(arg0);
	}
}
