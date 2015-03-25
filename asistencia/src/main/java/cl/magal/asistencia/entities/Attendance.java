package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import cl.magal.asistencia.entities.converter.AttendanceMarkConverter;
import cl.magal.asistencia.entities.enums.AttendanceMark;

@Entity
@Table(name="attendance")
public class Attendance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2755191834377196594L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	@Column(name="attendanceId")
	Long attendanceId;
	
	@NotNull(message="La fecha es necesaria.")
	@Temporal(TemporalType.DATE)
	@Column(name = "date" ,nullable = false )
	Date date;
	
	@ManyToOne
	@JoinColumn(name="LABORER_CONSTRUCTIONSITEID",nullable=false)
	LaborerConstructionsite laborerConstructionSite;
	
	@Column(name="jornal")
	Integer jornalPromedio = 0;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d1" ,nullable = false )
	AttendanceMark d1 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d2" ,nullable = false )
	AttendanceMark d2 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d3" ,nullable = false )
	AttendanceMark d3 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d4" ,nullable = false )
	AttendanceMark d4 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d5" ,nullable = false )
	AttendanceMark d5 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d6" ,nullable = false )
	AttendanceMark d6 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d7" ,nullable = false )
	AttendanceMark d7 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d8" ,nullable = false )
	AttendanceMark d8 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d9" ,nullable = false )
	AttendanceMark d9 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d10" ,nullable = false )
	AttendanceMark d10 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d11" ,nullable = false )
	AttendanceMark d11 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d12" ,nullable = false )
	AttendanceMark d12 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d13" ,nullable = false )
	AttendanceMark d13 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d14" ,nullable = false )
	AttendanceMark d14 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d15" ,nullable = false )
	AttendanceMark d15 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d16" ,nullable = false )
	AttendanceMark d16 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d17" ,nullable = false )
	AttendanceMark d17 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d18" ,nullable = false )
	AttendanceMark d18 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d19" ,nullable = false )
	AttendanceMark d19 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d20" ,nullable = false )
	AttendanceMark d20 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d21" ,nullable = false )
	AttendanceMark d21 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d22" ,nullable = false )
	AttendanceMark d22 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d23" ,nullable = false )
	AttendanceMark d23 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d24" ,nullable = false )
	AttendanceMark d24 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d25" ,nullable = false )
	AttendanceMark d25 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d26" ,nullable = false )
	AttendanceMark d26 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d27" ,nullable = false )
	AttendanceMark d27 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "d28" ,nullable = false )
	AttendanceMark d28 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark d29 = AttendanceMark.ATTEND;
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark d30 = AttendanceMark.ATTEND;
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark d31 = AttendanceMark.ATTEND;
	
	public List<AttendanceMark> getMarksAsList() {
		ArrayList<AttendanceMark> tmp = new ArrayList<AttendanceMark>();
		tmp.add(d1);tmp.add(d2);
		tmp.add(d3);tmp.add(d4);
		tmp.add(d5);tmp.add(d6);
		tmp.add(d7);tmp.add(d8);
		tmp.add(d9);tmp.add(d10);
		tmp.add(d11);tmp.add(d12);
		tmp.add(d13);tmp.add(d14);
		tmp.add(d15);tmp.add(d16);
		tmp.add(d17);tmp.add(d18);
		tmp.add(d19);tmp.add(d20);
		tmp.add(d21);tmp.add(d22);
		tmp.add(d23);tmp.add(d24);
		tmp.add(d25);tmp.add(d26);
		tmp.add(d27);tmp.add(d28);
		tmp.add(d29);tmp.add(d30);tmp.add(d31);
		
		return Collections.unmodifiableList(tmp);
	}
	
	public Integer getJornalPromedio() {
		return jornalPromedio == null ? 0 : jornalPromedio;
	}

	public void setJornalPromedio(Integer jornalPromedio) {
		this.jornalPromedio = jornalPromedio;
	}

	public Long getAttendanceId() {
		return attendanceId;
	}
	public void setAttendanceId(Long attendanceId) {
		this.attendanceId = attendanceId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public LaborerConstructionsite getLaborerConstructionSite() {
		return laborerConstructionSite;
	}
	public void setLaborerConstructionSite(
			LaborerConstructionsite laborerConstructionSite) {
		this.laborerConstructionSite = laborerConstructionSite;
	}
	public AttendanceMark getD1() {
		return d1;
	}
	public void setD1(AttendanceMark d1) {
		this.d1 = d1;
	}
	public AttendanceMark getD2() {
		return d2;
	}
	public void setD2(AttendanceMark d2) {
		this.d2 = d2;
	}
	public AttendanceMark getD3() {
		return d3;
	}
	public void setD3(AttendanceMark d3) {
		this.d3 = d3;
	}
	public AttendanceMark getD4() {
		return d4;
	}
	public void setD4(AttendanceMark d4) {
		this.d4 = d4;
	}
	public AttendanceMark getD5() {
		return d5;
	}
	public void setD5(AttendanceMark d5) {
		this.d5 = d5;
	}
	public AttendanceMark getD6() {
		return d6;
	}
	public void setD6(AttendanceMark d6) {
		this.d6 = d6;
	}
	public AttendanceMark getD7() {
		return d7;
	}
	public void setD7(AttendanceMark d7) {
		this.d7 = d7;
	}
	public AttendanceMark getD8() {
		return d8;
	}
	public void setD8(AttendanceMark d8) {
		this.d8 = d8;
	}
	public AttendanceMark getD9() {
		return d9;
	}
	public void setD9(AttendanceMark d9) {
		this.d9 = d9;
	}
	public AttendanceMark getD10() {
		return d10;
	}
	public void setD10(AttendanceMark d10) {
		this.d10 = d10;
	}
	public AttendanceMark getD11() {
		return d11;
	}
	public void setD11(AttendanceMark d11) {
		this.d11 = d11;
	}
	public AttendanceMark getD12() {
		return d12;
	}
	public void setD12(AttendanceMark d12) {
		this.d12 = d12;
	}
	public AttendanceMark getD13() {
		return d13;
	}
	public void setD13(AttendanceMark d13) {
		this.d13 = d13;
	}
	public AttendanceMark getD14() {
		return d14;
	}
	public void setD14(AttendanceMark d14) {
		this.d14 = d14;
	}
	public AttendanceMark getD15() {
		return d15;
	}
	public void setD15(AttendanceMark d15) {
		this.d15 = d15;
	}
	public AttendanceMark getD16() {
		return d16;
	}
	public void setD16(AttendanceMark d16) {
		this.d16 = d16;
	}
	public AttendanceMark getD17() {
		return d17;
	}
	public void setD17(AttendanceMark d17) {
		this.d17 = d17;
	}
	public AttendanceMark getD18() {
		return d18;
	}
	public void setD18(AttendanceMark d18) {
		this.d18 = d18;
	}
	public AttendanceMark getD19() {
		return d19;
	}
	public void setD19(AttendanceMark d19) {
		this.d19 = d19;
	}
	public AttendanceMark getD20() {
		return d20;
	}
	public void setD20(AttendanceMark d20) {
		this.d20 = d20;
	}
	public AttendanceMark getD21() {
		return d21;
	}
	public void setD21(AttendanceMark d21) {
		this.d21 = d21;
	}
	public AttendanceMark getD22() {
		return d22;
	}
	public void setD22(AttendanceMark d22) {
		this.d22 = d22;
	}
	public AttendanceMark getD23() {
		return d23;
	}
	public void setD23(AttendanceMark d23) {
		this.d23 = d23;
	}
	public AttendanceMark getD24() {
		return d24;
	}
	public void setD24(AttendanceMark d24) {
		this.d24 = d24;
	}
	public AttendanceMark getD25() {
		return d25;
	}
	public void setD25(AttendanceMark d25) {
		this.d25 = d25;
	}
	public AttendanceMark getD26() {
		return d26;
	}
	public void setD26(AttendanceMark d26) {
		this.d26 = d26;
	}
	public AttendanceMark getD27() {
		return d27;
	}
	public void setD27(AttendanceMark d27) {
		this.d27 = d27;
	}
	public AttendanceMark getD28() {
		return d28;
	}
	public void setD28(AttendanceMark d28) {
		this.d28 = d28;
	}
	public AttendanceMark getD29() {
		return d29;
	}
	public void setD29(AttendanceMark d29) {
		this.d29 = d29;
	}
	public AttendanceMark getD30() {
		return d30;
	}
	public void setD30(AttendanceMark d30) {
		this.d30 = d30;
	}
	public AttendanceMark getD31() {
		return d31;
	}
	public void setD31(AttendanceMark d31) {
		this.d31 = d31;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((laborerConstructionSite == null) ? 0
						: laborerConstructionSite.getId().hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attendance other = (Attendance) obj;
		if (laborerConstructionSite == null) {
			if (other.laborerConstructionSite != null)
				return false;
		} else if (!laborerConstructionSite.getId()
				.equals(other.laborerConstructionSite.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Attendance [attendanceId=" + attendanceId + ", date=" + date
				+ ", laborerConstructionSite=" + laborerConstructionSite + "]";
	}
	
}
