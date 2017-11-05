package cl.koritsu.asistenca_ws.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="attendance_clock")
public class AttendanceClock  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5122738336559819305L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "attendanceClockId")
	Long attendanceClockId;
	@Temporal(TemporalType.DATE)
	@Column(name = "entryTime")
	Date entryTime;
	@Temporal(TemporalType.DATE)
	@Column(name = "departureTime")
	Date departureTime;
	@Column(name = "rut")
	String rut;
	@Column(name = "constructionsiteId")
	Long constructionsiteId;
	
	public Long getAttendanceClockId() {
		return attendanceClockId;
	}
//	public void setAttendanceClockId(Long attendanceClockId) {
//		this.attendanceClockId = attendanceClockId;
//	}
	public Date getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
	}
	public Date getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}
	public String getRut() {
		return rut;
	}
	public void setRut(String rut) {
		this.rut = rut;
	}
	public Long getConstructionsiteId() {
		return constructionsiteId;
	}
	public void setConstructionsiteId(Long constructionsiteId) {
		this.constructionsiteId = constructionsiteId;
	}
}
