package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.LicenseType;

@Converter(autoApply=true)
public class LicenseTypeConverter implements AttributeConverter<LicenseType, Integer>{

	@Override
	public Integer convertToDatabaseColumn(LicenseType arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public LicenseType convertToEntityAttribute(Integer arg0) {
		if(arg0 == null )
			throw new RuntimeException("Atributo de licencia nulo.");
		return LicenseType.getLicenseType(arg0);
	}

}