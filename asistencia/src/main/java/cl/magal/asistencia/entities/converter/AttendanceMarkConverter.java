package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.AttendanceMark;

@Converter(autoApply=true)
public class AttendanceMarkConverter implements AttributeConverter<AttendanceMark, Integer> {

	@Override
	public Integer convertToDatabaseColumn(AttendanceMark arg0) {
		if(arg0 == null)
			return AttendanceMark.EMPTY.getCorrelative();
		return arg0.getCorrelative();
	}

	@Override
	public AttendanceMark convertToEntityAttribute(Integer arg0) {
		if(arg0 == null)
			return AttendanceMark.EMPTY;
		return AttendanceMark.getAttendanceMark(arg0);
	}
}
