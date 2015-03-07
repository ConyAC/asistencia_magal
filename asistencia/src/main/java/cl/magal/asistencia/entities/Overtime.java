package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
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

@Entity
@Table(name="overtime")
public class Overtime implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2755191834377196594L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	@Column(name="overtimeId")
	Long overtimeId;
	
	@NotNull(message="La fecha es necesaria.")
	@Temporal(TemporalType.DATE)
	@Column(name = "date" ,nullable = false )
	Date date;
	
	@ManyToOne
	@JoinColumn(name="LABORER_CONSTRUCTIONSITEID",nullable=false)
	LaborerConstructionsite laborerConstructionSite;
	
	@Column(name = "d1" ,nullable = false )
	Integer d1 = 0;
	
	@Column(name = "d2" ,nullable = false )
	Integer d2 = 0;
	
	@Column(name = "d3" ,nullable = false )
	Integer d3 = 0;
	
	@Column(name = "d4" ,nullable = false )
	Integer d4 = 0;
	
	@Column(name = "d5" ,nullable = false )
	Integer d5 = 0;
	
	@Column(name = "d6" ,nullable = false )
	Integer d6 = 0;
	
	@Column(name = "d7" ,nullable = false )
	Integer d7 = 0;
	
	@Column(name = "d8" ,nullable = false )
	Integer d8 = 0;
	
	@Column(name = "d9" ,nullable = false )
	Integer d9 = 0;
	
	@Column(name = "d10" ,nullable = false )
	Integer d10 = 0;
	
	@Column(name = "d11" ,nullable = false )
	Integer d11 = 0;
	
	@Column(name = "d12" ,nullable = false )
	Integer d12 = 0;
	
	@Column(name = "d13" ,nullable = false )
	Integer d13 = 0;
	
	@Column(name = "d14" ,nullable = false )
	Integer d14 = 0;
	
	@Column(name = "d15" ,nullable = false )
	Integer d15 = 0;
	
	@Column(name = "d16" ,nullable = false )
	Integer d16 = 0;
	
	@Column(name = "d17" ,nullable = false )
	Integer d17 = 0;
	
	@Column(name = "d18" ,nullable = false )
	Integer d18 = 0;
	
	@Column(name = "d19" ,nullable = false )
	Integer d19 = 0;
	
	@Column(name = "d20" ,nullable = false )
	Integer d20 = 0;
	
	@Column(name = "d21" ,nullable = false )
	Integer d21 = 0;
	
	@Column(name = "d22" ,nullable = false )
	Integer d22 = 0;
	
	@Column(name = "d23" ,nullable = false )
	Integer d23 = 0;
	
	@Column(name = "d24" ,nullable = false )
	Integer d24 = 0;
	
	@Column(name = "d25" ,nullable = false )
	Integer d25 = 0;
	
	@Column(name = "d26" ,nullable = false )
	Integer d26 = 0;
	
	@Column(name = "d27" ,nullable = false )
	Integer d27 = 0;
	
	@Column(name = "d28" ,nullable = false )
	Integer d28 = 0;
	
	Integer d29 = 0;
	Integer d30 = 0;
	Integer d31 = 0;
	public Long getOvertimeId() {
		return overtimeId;
	}
	public void setOvertimeId(Long overtimeId) {
		this.overtimeId = overtimeId;
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
	public Integer getD1() {
		return d1;
	}
	public void setD1(Integer d1) {
		this.d1 = d1;
	}
	public Integer getD2() {
		return d2;
	}
	public void setD2(Integer d2) {
		this.d2 = d2;
	}
	public Integer getD3() {
		return d3;
	}
	public void setD3(Integer d3) {
		this.d3 = d3;
	}
	public Integer getD4() {
		return d4;
	}
	public void setD4(Integer d4) {
		this.d4 = d4;
	}
	public Integer getD5() {
		return d5;
	}
	public void setD5(Integer d5) {
		this.d5 = d5;
	}
	public Integer getD6() {
		return d6;
	}
	public void setD6(Integer d6) {
		this.d6 = d6;
	}
	public Integer getD7() {
		return d7;
	}
	public void setD7(Integer d7) {
		this.d7 = d7;
	}
	public Integer getD8() {
		return d8;
	}
	public void setD8(Integer d8) {
		this.d8 = d8;
	}
	public Integer getD9() {
		return d9;
	}
	public void setD9(Integer d9) {
		this.d9 = d9;
	}
	public Integer getD10() {
		return d10;
	}
	public void setD10(Integer d10) {
		this.d10 = d10;
	}
	public Integer getD11() {
		return d11;
	}
	public void setD11(Integer d11) {
		this.d11 = d11;
	}
	public Integer getD12() {
		return d12;
	}
	public void setD12(Integer d12) {
		this.d12 = d12;
	}
	public Integer getD13() {
		return d13;
	}
	public void setD13(Integer d13) {
		this.d13 = d13;
	}
	public Integer getD14() {
		return d14;
	}
	public void setD14(Integer d14) {
		this.d14 = d14;
	}
	public Integer getD15() {
		return d15;
	}
	public void setD15(Integer d15) {
		this.d15 = d15;
	}
	public Integer getD16() {
		return d16;
	}
	public void setD16(Integer d16) {
		this.d16 = d16;
	}
	public Integer getD17() {
		return d17;
	}
	public void setD17(Integer d17) {
		this.d17 = d17;
	}
	public Integer getD18() {
		return d18;
	}
	public void setD18(Integer d18) {
		this.d18 = d18;
	}
	public Integer getD19() {
		return d19;
	}
	public void setD19(Integer d19) {
		this.d19 = d19;
	}
	public Integer getD20() {
		return d20;
	}
	public void setD20(Integer d20) {
		this.d20 = d20;
	}
	public Integer getD21() {
		return d21;
	}
	public void setD21(Integer d21) {
		this.d21 = d21;
	}
	public Integer getD22() {
		return d22;
	}
	public void setD22(Integer d22) {
		this.d22 = d22;
	}
	public Integer getD23() {
		return d23;
	}
	public void setD23(Integer d23) {
		this.d23 = d23;
	}
	public Integer getD24() {
		return d24;
	}
	public void setD24(Integer d24) {
		this.d24 = d24;
	}
	public Integer getD25() {
		return d25;
	}
	public void setD25(Integer d25) {
		this.d25 = d25;
	}
	public Integer getD26() {
		return d26;
	}
	public void setD26(Integer d26) {
		this.d26 = d26;
	}
	public Integer getD27() {
		return d27;
	}
	public void setD27(Integer d27) {
		this.d27 = d27;
	}
	public Integer getD28() {
		return d28;
	}
	public void setD28(Integer d28) {
		this.d28 = d28;
	}
	public Integer getD29() {
		return d29;
	}
	public void setD29(Integer d29) {
		this.d29 = d29;
	}
	public Integer getD30() {
		return d30;
	}
	public void setD30(Integer d30) {
		this.d30 = d30;
	}
	public Integer getD31() {
		return d31;
	}
	public void setD31(Integer d31) {
		this.d31 = d31;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((laborerConstructionSite == null) ? 0
						: laborerConstructionSite.hashCode());
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
		Overtime other = (Overtime) obj;
		if (laborerConstructionSite == null) {
			if (other.laborerConstructionSite != null)
				return false;
		} else if (!laborerConstructionSite.getId()
				.equals(other.laborerConstructionSite.getId()))
			return false;
		return true;
	}
	
	
}
