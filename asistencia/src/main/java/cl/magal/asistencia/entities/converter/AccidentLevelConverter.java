package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.AccidentLevel;

@Converter(autoApply=true)
public class AccidentLevelConverter implements AttributeConverter<AccidentLevel, Integer>{

	@Override
	public Integer convertToDatabaseColumn(AccidentLevel arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public AccidentLevel convertToEntityAttribute(Integer arg0) {
		if(arg0 == null )
			throw new RuntimeException("Atributo de accidente nulo.");
		return AccidentLevel.getAccidentLevel(arg0);
	}

}