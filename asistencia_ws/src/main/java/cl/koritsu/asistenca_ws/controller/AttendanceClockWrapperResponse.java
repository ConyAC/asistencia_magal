package cl.koritsu.asistenca_ws.controller;

import java.util.List;

import cl.koritsu.asistenca_ws.model.AttendanceClock;

public class AttendanceClockWrapperResponse {
	
	Long constructionsiteId;
	List<AttendanceClock> attendances;
	ResponseCode responseCode;
	
	
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
	public ResponseCode getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}
}
