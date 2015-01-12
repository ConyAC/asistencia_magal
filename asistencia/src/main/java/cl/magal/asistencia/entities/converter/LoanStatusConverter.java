package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.LoanStatus;

@Converter
public class LoanStatusConverter implements AttributeConverter<LoanStatus, Integer> {

	@Override
	public Integer convertToDatabaseColumn(LoanStatus arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public LoanStatus convertToEntityAttribute(Integer arg0) {
		return LoanStatus.getLoanStatus(arg0);
	}
}
