package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.LoanToolStatus;

@Converter
public class LoanToolStatusConverter implements AttributeConverter<LoanToolStatus, Integer> {

	@Override
	public Integer convertToDatabaseColumn(LoanToolStatus arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public LoanToolStatus convertToEntityAttribute(Integer arg0) {
		return LoanToolStatus.getToolStatus(arg0);
	}
}
