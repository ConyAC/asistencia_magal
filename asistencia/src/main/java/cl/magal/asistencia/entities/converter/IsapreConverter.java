package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.Isapre;

@Converter(autoApply=true)
public class IsapreConverter implements AttributeConverter<Isapre, Integer>{

	@Override
	public Integer convertToDatabaseColumn(Isapre arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public Isapre convertToEntityAttribute(Integer arg0) {
		if(arg0 == null )
			throw new RuntimeException("Atributo Isapre nulo.");
		return Isapre.getIsapre(arg0);
	}

}