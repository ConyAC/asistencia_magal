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
	AttendanceMark dmp1 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp2 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp3 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp4 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp5 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp6 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp7 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp8 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp9 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp10 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp11 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp12 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp13 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp14 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp15 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp16 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp17 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp18 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp19 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp20 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp21 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp22 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp23 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp24 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp25 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp26 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp27 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp28 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp29 = AttendanceMark.ATTEND;
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp30 = AttendanceMark.ATTEND;
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dmp31 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma1" ,nullable = false )
	AttendanceMark dma1 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma2" ,nullable = false )
	AttendanceMark dma2 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma3" ,nullable = false )
	AttendanceMark dma3 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma4" ,nullable = false )
	AttendanceMark dma4 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma5" ,nullable = false )
	AttendanceMark dma5 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma6" ,nullable = false )
	AttendanceMark dma6 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma7" ,nullable = false )
	AttendanceMark dma7 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma8" ,nullable = false )
	AttendanceMark dma8 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma9" ,nullable = false )
	AttendanceMark dma9 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma10" ,nullable = false )
	AttendanceMark dma10 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma11" ,nullable = false )
	AttendanceMark dma11 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma12" ,nullable = false )
	AttendanceMark dma12 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma13" ,nullable = false )
	AttendanceMark dma13 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma14" ,nullable = false )
	AttendanceMark dma14 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma15" ,nullable = false )
	AttendanceMark dma15 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma16" ,nullable = false )
	AttendanceMark dma16 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma17" ,nullable = false )
	AttendanceMark dma17 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma18" ,nullable = false )
	AttendanceMark dma18 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma19" ,nullable = false )
	AttendanceMark dma19 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma20" ,nullable = false )
	AttendanceMark dma20 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma21" ,nullable = false )
	AttendanceMark dma21 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma22" ,nullable = false )
	AttendanceMark dma22 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma23" ,nullable = false )
	AttendanceMark dma23 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma24" ,nullable = false )
	AttendanceMark dma24 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma25" ,nullable = false )
	AttendanceMark dma25 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma26" ,nullable = false )
	AttendanceMark dma26 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma27" ,nullable = false )
	AttendanceMark dma27 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	@Column(name = "dma28" ,nullable = false )
	AttendanceMark dma28 = AttendanceMark.ATTEND;
	
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dma29 = AttendanceMark.ATTEND;
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dma30 = AttendanceMark.ATTEND;
	@Convert(converter = AttendanceMarkConverter.class)
	AttendanceMark dma31 = AttendanceMark.ATTEND;
	
	public List<AttendanceMark> getMarksAsList() {
		ArrayList<AttendanceMark> tmp = new ArrayList<AttendanceMark>();
		tmp.add(dma1);tmp.add(dma2);
		tmp.add(dma3);tmp.add(dma4);
		tmp.add(dma5);tmp.add(dma6);
		tmp.add(dma7);tmp.add(dma8);
		tmp.add(dma9);tmp.add(dma10);
		tmp.add(dma11);tmp.add(dma12);
		tmp.add(dma13);tmp.add(dma14);
		tmp.add(dma15);tmp.add(dma16);
		tmp.add(dma17);tmp.add(dma18);
		tmp.add(dma19);tmp.add(dma20);
		tmp.add(dma21);tmp.add(dma22);
		tmp.add(dma23);tmp.add(dma24);
		tmp.add(dma25);tmp.add(dma26);
		tmp.add(dma27);tmp.add(dma28);
		tmp.add(dma29);tmp.add(dma30);tmp.add(dma31);
		
		return Collections.unmodifiableList(tmp);
	}
	
	public List<AttendanceMark> getLastMarksAsList() {
		ArrayList<AttendanceMark> tmp = new ArrayList<AttendanceMark>();
		tmp.add(dmp1);tmp.add(dmp2);
		tmp.add(dmp3);tmp.add(dmp4);
		tmp.add(dmp5);tmp.add(dmp6);
		tmp.add(dmp7);tmp.add(dmp8);
		tmp.add(dmp9);tmp.add(dmp10);
		tmp.add(dmp11);tmp.add(dmp12);
		tmp.add(dmp13);tmp.add(dmp14);
		tmp.add(dmp15);tmp.add(dmp16);
		tmp.add(dmp17);tmp.add(dmp18);
		tmp.add(dmp19);tmp.add(dmp20);
		tmp.add(dmp21);tmp.add(dmp22);
		tmp.add(dmp23);tmp.add(dmp24);
		tmp.add(dmp25);tmp.add(dmp26);
		tmp.add(dmp27);tmp.add(dmp28);
		tmp.add(dmp29);tmp.add(dmp30);tmp.add(dmp31);
		
		return Collections.unmodifiableList(tmp);
	}
	
	public Integer getJornalPromedio() {
		return jornalPromedio;
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
	
	public AttendanceMark getDmp1() {
		return dmp1;
	}

	public void setDmp1(AttendanceMark dmp1) {
		this.dmp1 = dmp1;
	}

	public AttendanceMark getDmp2() {
		return dmp2;
	}

	public void setDmp2(AttendanceMark dmp2) {
		this.dmp2 = dmp2;
	}

	public AttendanceMark getDmp3() {
		return dmp3;
	}

	public void setDmp3(AttendanceMark dmp3) {
		this.dmp3 = dmp3;
	}

	public AttendanceMark getDmp4() {
		return dmp4;
	}

	public void setDmp4(AttendanceMark dmp4) {
		this.dmp4 = dmp4;
	}

	public AttendanceMark getDmp5() {
		return dmp5;
	}

	public void setDmp5(AttendanceMark dmp5) {
		this.dmp5 = dmp5;
	}

	public AttendanceMark getDmp6() {
		return dmp6;
	}

	public void setDmp6(AttendanceMark dmp6) {
		this.dmp6 = dmp6;
	}

	public AttendanceMark getDmp7() {
		return dmp7;
	}

	public void setDmp7(AttendanceMark dmp7) {
		this.dmp7 = dmp7;
	}

	public AttendanceMark getDmp8() {
		return dmp8;
	}

	public void setDmp8(AttendanceMark dmp8) {
		this.dmp8 = dmp8;
	}

	public AttendanceMark getDmp9() {
		return dmp9;
	}

	public void setDmp9(AttendanceMark dmp9) {
		this.dmp9 = dmp9;
	}

	public AttendanceMark getDmp10() {
		return dmp10;
	}

	public void setDmp10(AttendanceMark dmp10) {
		this.dmp10 = dmp10;
	}

	public AttendanceMark getDmp11() {
		return dmp11;
	}

	public void setDmp11(AttendanceMark dmp11) {
		this.dmp11 = dmp11;
	}

	public AttendanceMark getDmp12() {
		return dmp12;
	}

	public void setDmp12(AttendanceMark dmp12) {
		this.dmp12 = dmp12;
	}

	public AttendanceMark getDmp13() {
		return dmp13;
	}

	public void setDmp13(AttendanceMark dmp13) {
		this.dmp13 = dmp13;
	}

	public AttendanceMark getDmp14() {
		return dmp14;
	}

	public void setDmp14(AttendanceMark dmp14) {
		this.dmp14 = dmp14;
	}

	public AttendanceMark getDmp15() {
		return dmp15;
	}

	public void setDmp15(AttendanceMark dmp15) {
		this.dmp15 = dmp15;
	}

	public AttendanceMark getDmp16() {
		return dmp16;
	}

	public void setDmp16(AttendanceMark dmp16) {
		this.dmp16 = dmp16;
	}

	public AttendanceMark getDmp17() {
		return dmp17;
	}

	public void setDmp17(AttendanceMark dmp17) {
		this.dmp17 = dmp17;
	}

	public AttendanceMark getDmp18() {
		return dmp18;
	}

	public void setDmp18(AttendanceMark dmp18) {
		this.dmp18 = dmp18;
	}

	public AttendanceMark getDmp19() {
		return dmp19;
	}

	public void setDmp19(AttendanceMark dmp19) {
		this.dmp19 = dmp19;
	}

	public AttendanceMark getDmp20() {
		return dmp20;
	}

	public void setDmp20(AttendanceMark dmp20) {
		this.dmp20 = dmp20;
	}

	public AttendanceMark getDmp21() {
		return dmp21;
	}

	public void setDmp21(AttendanceMark dmp21) {
		this.dmp21 = dmp21;
	}

	public AttendanceMark getDmp22() {
		return dmp22;
	}

	public void setDmp22(AttendanceMark dmp22) {
		this.dmp22 = dmp22;
	}

	public AttendanceMark getDmp23() {
		return dmp23;
	}

	public void setDmp23(AttendanceMark dmp23) {
		this.dmp23 = dmp23;
	}

	public AttendanceMark getDmp24() {
		return dmp24;
	}

	public void setDmp24(AttendanceMark dmp24) {
		this.dmp24 = dmp24;
	}

	public AttendanceMark getDmp25() {
		return dmp25;
	}

	public void setDmp25(AttendanceMark dmp25) {
		this.dmp25 = dmp25;
	}

	public AttendanceMark getDmp26() {
		return dmp26;
	}

	public void setDmp26(AttendanceMark dmp26) {
		this.dmp26 = dmp26;
	}

	public AttendanceMark getDmp27() {
		return dmp27;
	}

	public void setDmp27(AttendanceMark dmp27) {
		this.dmp27 = dmp27;
	}

	public AttendanceMark getDmp28() {
		return dmp28;
	}

	public void setDmp28(AttendanceMark dmp28) {
		this.dmp28 = dmp28;
	}

	public AttendanceMark getDmp29() {
		return dmp29;
	}

	public void setDmp29(AttendanceMark dmp29) {
		this.dmp29 = dmp29;
	}

	public AttendanceMark getDmp30() {
		return dmp30;
	}

	public void setDmp30(AttendanceMark dmp30) {
		this.dmp30 = dmp30;
	}

	public AttendanceMark getDmp31() {
		return dmp31;
	}

	public void setDmp31(AttendanceMark dmp31) {
		this.dmp31 = dmp31;
	}
	
	public AttendanceMark getDma1() {
		return dma1;
	}

	public void setDma1(AttendanceMark dma1) {
		this.dma1 = dma1;
	}

	public AttendanceMark getDma2() {
		return dma2;
	}

	public void setDma2(AttendanceMark dma2) {
		this.dma2 = dma2;
	}

	public AttendanceMark getDma3() {
		return dma3;
	}

	public void setDma3(AttendanceMark dma3) {
		this.dma3 = dma3;
	}

	public AttendanceMark getDma4() {
		return dma4;
	}

	public void setDma4(AttendanceMark dma4) {
		this.dma4 = dma4;
	}

	public AttendanceMark getDma5() {
		return dma5;
	}

	public void setDma5(AttendanceMark dma5) {
		this.dma5 = dma5;
	}

	public AttendanceMark getDma6() {
		return dma6;
	}

	public void setDma6(AttendanceMark dma6) {
		this.dma6 = dma6;
	}

	public AttendanceMark getDma7() {
		return dma7;
	}

	public void setDma7(AttendanceMark dma7) {
		this.dma7 = dma7;
	}

	public AttendanceMark getDma8() {
		return dma8;
	}

	public void setDma8(AttendanceMark dma8) {
		this.dma8 = dma8;
	}

	public AttendanceMark getDma9() {
		return dma9;
	}

	public void setDma9(AttendanceMark dma9) {
		this.dma9 = dma9;
	}

	public AttendanceMark getDma10() {
		return dma10;
	}

	public void setDma10(AttendanceMark dma10) {
		this.dma10 = dma10;
	}

	public AttendanceMark getDma11() {
		return dma11;
	}

	public void setDma11(AttendanceMark dma11) {
		this.dma11 = dma11;
	}

	public AttendanceMark getDma12() {
		return dma12;
	}

	public void setDma12(AttendanceMark dma12) {
		this.dma12 = dma12;
	}

	public AttendanceMark getDma13() {
		return dma13;
	}

	public void setDma13(AttendanceMark dma13) {
		this.dma13 = dma13;
	}

	public AttendanceMark getDma14() {
		return dma14;
	}

	public void setDma14(AttendanceMark dma14) {
		this.dma14 = dma14;
	}

	public AttendanceMark getDma15() {
		return dma15;
	}

	public void setDma15(AttendanceMark dma15) {
		this.dma15 = dma15;
	}

	public AttendanceMark getDma16() {
		return dma16;
	}

	public void setDma16(AttendanceMark dma16) {
		this.dma16 = dma16;
	}

	public AttendanceMark getDma17() {
		return dma17;
	}

	public void setDma17(AttendanceMark dma17) {
		this.dma17 = dma17;
	}

	public AttendanceMark getDma18() {
		return dma18;
	}

	public void setDma18(AttendanceMark dma18) {
		this.dma18 = dma18;
	}

	public AttendanceMark getDma19() {
		return dma19;
	}

	public void setDma19(AttendanceMark dma19) {
		this.dma19 = dma19;
	}

	public AttendanceMark getDma20() {
		return dma20;
	}

	public void setDma20(AttendanceMark dma20) {
		this.dma20 = dma20;
	}

	public AttendanceMark getDma21() {
		return dma21;
	}

	public void setDma21(AttendanceMark dma21) {
		this.dma21 = dma21;
	}

	public AttendanceMark getDma22() {
		return dma22;
	}

	public void setDma22(AttendanceMark dma22) {
		this.dma22 = dma22;
	}

	public AttendanceMark getDma23() {
		return dma23;
	}

	public void setDma23(AttendanceMark dma23) {
		this.dma23 = dma23;
	}

	public AttendanceMark getDma24() {
		return dma24;
	}

	public void setDma24(AttendanceMark dma24) {
		this.dma24 = dma24;
	}

	public AttendanceMark getDma25() {
		return dma25;
	}

	public void setDma25(AttendanceMark dma25) {
		this.dma25 = dma25;
	}

	public AttendanceMark getDma26() {
		return dma26;
	}

	public void setDma26(AttendanceMark dma26) {
		this.dma26 = dma26;
	}

	public AttendanceMark getDma27() {
		return dma27;
	}

	public void setDma27(AttendanceMark dma27) {
		this.dma27 = dma27;
	}

	public AttendanceMark getDma28() {
		return dma28;
	}

	public void setDma28(AttendanceMark dma28) {
		this.dma28 = dma28;
	}

	public AttendanceMark getDma29() {
		return dma29;
	}

	public void setDma29(AttendanceMark dma29) {
		this.dma29 = dma29;
	}

	public AttendanceMark getDma30() {
		return dma30;
	}

	public void setDma30(AttendanceMark dma30) {
		this.dma30 = dma30;
	}

	public AttendanceMark getDma31() {
		return dma31;
	}

	public void setDma31(AttendanceMark dma31) {
		this.dma31 = dma31;
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
