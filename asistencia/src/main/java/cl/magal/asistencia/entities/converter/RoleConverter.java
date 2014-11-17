package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.Role;

@Converter
public class RoleConverter implements AttributeConverter<Role, Integer> {

	@Override
	public Integer convertToDatabaseColumn(Role arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public Role convertToEntityAttribute(Integer arg0) {
		return Role.getRole(arg0);
	}
}
