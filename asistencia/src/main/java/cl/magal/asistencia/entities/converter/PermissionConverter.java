package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.Permission;

@Converter(autoApply=true)
public class PermissionConverter implements AttributeConverter<Permission, Integer>{

	@Override
	public Integer convertToDatabaseColumn(Permission arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public Permission convertToEntityAttribute(Integer arg0) {
		return Permission.getPermission(arg0);
	}

}
