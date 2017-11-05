package cl.koritsu.asistenca_ws.controller;

import java.util.List;

import cl.koritsu.asistenca_ws.model.AttendanceClock;

public class AttendanceClockWrapper {
	
	Long constructionsiteId;
	List<AttendanceClock> attendances;
	public Long getConstructionsiteId() {
		return constructionsiteId;
	}
	public void setConstructionsiteId(Long constructionsiteId) {
		this.constructionsiteId = constructionsiteId;
	}
	public List<AttendanceClock> getAttendances() {
		return attendances;
	}
	public void setAttendances(List<AttendanceClock> attendances) {
		this.attendances = attendances;
	}
	
}
