package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.Afp;

@Converter(autoApply=true)
public class AfpConverter implements AttributeConverter<Afp, Integer>{

	@Override
	public Integer convertToDatabaseColumn(Afp arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public Afp convertToEntityAttribute(Integer arg0) {
		return Afp.getAfp(arg0);
	}

}
