package cl.koritsu.asistenca_ws.service;

import java.util.List;

import cl.koritsu.asistenca_ws.model.AttendanceClock;

public interface AttendanceClockService {

	boolean constructionSiteExists(Long constructionsiteId);

	void saveAttendance(Long constructionsiteId, List<AttendanceClock> attendances);

}
