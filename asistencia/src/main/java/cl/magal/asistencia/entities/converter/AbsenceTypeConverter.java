package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.AbsenceType;

@Converter(autoApply=true)
public class AbsenceTypeConverter implements AttributeConverter<AbsenceType, Integer>{

	@Override
	public Integer convertToDatabaseColumn(AbsenceType arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public AbsenceType convertToEntityAttribute(Integer arg0) {
		if(arg0 == null )
			throw new RuntimeException("Atributo de usencia nulo.");
		return AbsenceType.getAbsencesType(arg0);
	}

}