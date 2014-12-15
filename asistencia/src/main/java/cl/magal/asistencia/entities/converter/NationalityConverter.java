package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.Nationality;

@Converter(autoApply=true)
public class NationalityConverter implements AttributeConverter<Nationality, Integer>{

	@Override
	public Integer convertToDatabaseColumn(Nationality arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public Nationality convertToEntityAttribute(Integer arg0) {
		return Nationality.getNationality(arg0);
	}

}