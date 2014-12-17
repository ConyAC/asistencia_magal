package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.enums.Afp;

@Converter(autoApply=true)
public class AfpConverter implements AttributeConverter<Afp, Integer>{

	Logger logger = LoggerFactory.getLogger(AfpConverter.class);
	@Override
	public Integer convertToDatabaseColumn(Afp arg0) {
		Integer r = arg0.getCorrelative();
		logger.debug("convertToDatabaseColumn {} y {} ",arg0,r);
		return r;
	}

	@Override
	public Afp convertToEntityAttribute(Integer arg0) {
		return Afp.getAfp(arg0);
	}

}
