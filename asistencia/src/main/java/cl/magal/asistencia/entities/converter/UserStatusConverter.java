package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.UserStatus;

@Converter(autoApply=true)
public class UserStatusConverter implements AttributeConverter<UserStatus, Integer> {

	@Override
	public Integer convertToDatabaseColumn(UserStatus arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public UserStatus convertToEntityAttribute(Integer arg0) {
		return UserStatus.getUserStatus(arg0);
	}
}
