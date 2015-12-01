package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
	Long id;
	
	@NotNull(message="La fecha es necesaria.")
	@Temporal(TemporalType.DATE)
	@Column(name = "date" ,nullable = false )
	Date date;
	
	@ManyToOne
	@JoinColumn(name="laborer_constructionsiteId",nullable=false)
	LaborerConstructionsite laborerConstructionSite;
	
	@Column(name = "dmp1")
	Double dmp1;
	@Column(name = "dmp2")
	Double dmp2;
	@Column(name = "dmp3")
	Double dmp3;
	@Column(name = "dmp4")
	Double dmp4;
	@Column(name = "dmp5")
	Double dmp5;
	@Column(name = "dmp6")
	Double dmp6;
	@Column(name = "dmp7")
	Double dmp7;
	@Column(name = "dmp8")
	Double dmp8;
	@Column(name = "dmp9")
	Double dmp9;
	@Column(name = "dmp10")
	Double dmp10;
	@Column(name = "dmp11")
	Double dmp11;
	@Column(name = "dmp12")
	Double dmp12;
	@Column(name = "dmp13")
	Double dmp13;
	@Column(name = "dmp14")
	Double dmp14;
	@Column(name = "dmp15")
	Double dmp15;
	@Column(name = "dmp16")
	Double dmp16;
	@Column(name = "dmp17")
	Double dmp17;
	@Column(name = "dmp18")
	Double dmp18;
	@Column(name = "dmp19")
	Double dmp19;
	@Column(name = "dmp20")
	Double dmp20;
	@Column(name = "dmp21")
	Double dmp21;
	@Column(name = "dmp22")
	Double dmp22;
	@Column(name = "dmp23")
	Double dmp23;
	@Column(name = "dmp24")
	Double dmp24;
	@Column(name = "dmp25")
	Double dmp25;
	@Column(name = "dmp26")
	Double dmp26;
	@Column(name = "dmp27")
	Double dmp27;
	@Column(name = "dmp28")
	Double dmp28;
	@Column(name = "dmp29")
	Double dmp29;
	@Column(name = "dmp30")
	Double dmp30;
	@Column(name = "dmp31")
	Double dmp31;
	
	@Column(name = "dma1"  )
	Double dma1;
	
	@Column(name = "dma2"  )
	Double dma2;
	
	@Column(name = "dma3"  )
	Double dma3;
	
	@Column(name = "dma4"  )
	Double dma4;
	
	@Column(name = "dma5"  )
	Double dma5;
	
	@Column(name = "dma6"  )
	Double dma6;
	
	@Column(name = "dma7"  )
	Double dma7;
	
	@Column(name = "dma8"  )
	Double dma8;
	
	@Column(name = "dma9"  )
	Double dma9;
	
	@Column(name = "dma10"  )
	Double dma10;
	
	@Column(name = "dma11"  )
	Double dma11;
	
	@Column(name = "dma12"  )
	Double dma12;
	
	@Column(name = "dma13"  )
	Double dma13;
	
	@Column(name = "dma14"  )
	Double dma14;
	
	@Column(name = "dma15"  )
	Double dma15;
	
	@Column(name = "dma16"  )
	Double dma16;
	
	@Column(name = "dma17"  )
	Double dma17;
	
	@Column(name = "dma18"  )
	Double dma18;
	
	@Column(name = "dma19"  )
	Double dma19;
	
	@Column(name = "dma20"  )
	Double dma20;
	
	@Column(name = "dma21"  )
	Double dma21;
	
	@Column(name = "dma22"  )
	Double dma22;
	
	@Column(name = "dma23"  )
	Double dma23;
	
	@Column(name = "dma24"  )
	Double dma24;
	
	@Column(name = "dma25"  )
	Double dma25;
	
	@Column(name = "dma26"  )
	Double dma26;
	
	@Column(name = "dma27"  )
	Double dma27;
	
	@Column(name = "dma28"  )
	Double dma28;
	
	@Column(name = "dma29")
	Double dma29;
	@Column(name = "dma30")
	Double dma30;
	@Column(name = "dma31")
	Double dma31;
	
	public Double getTotal(){
		return sumIntegers(dma1,dma2,dma3,dma4
		,dma5,dma6,dma7,dma8,dma9,dma10
		,dma11,dma12,dma13,dma14,dma15,dma16
		,dma17,dma18,dma19,dma20,dma21,dma22
		,dma23,dma24,dma25,dma26,dma27,dma28
		,dma29,dma30,dma31,dmp1,dmp2,dmp3,dmp4
		,dmp5,dmp6,dmp7,dmp8,dmp9,dmp10,dmp11,dmp12
		,dmp13,dmp14,dmp15,dmp16,dmp17,dmp18
		,dmp19,dmp20,dmp21,dmp22,dmp23,dmp24
		,dmp25,dmp26,dmp27,dmp28,dmp29,dmp30,dmp31);
	}
	
	private Double sumIntegers(Double... values){
		double i = 0;
		for(Double v : values)
			if(v != null )
				i += v;
		return i;
	}
	
	public List<Double> getOvertimeAsList() {
		ArrayList<Double> tmp = new ArrayList<Double>();
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
	
	public List<Double> getLastMonthOvertimeAsList() {
		ArrayList<Double> tmp = new ArrayList<Double>();
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
	
	public Long getId() {
		return id;
	}
	public void setId(Long overtimeId) {
		this.id = overtimeId;
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
	
	public Double getDmp1() {
		return dmp1;
	}

	public void setDmp1(Double dmp1) {
		this.dmp1 = dmp1;
	}

	public Double getDmp2() {
		return dmp2;
	}

	public void setDmp2(Double dmp2) {
		this.dmp2 = dmp2;
	}

	public Double getDmp3() {
		return dmp3;
	}

	public void setDmp3(Double dmp3) {
		this.dmp3 = dmp3;
	}

	public Double getDmp4() {
		return dmp4;
	}

	public void setDmp4(Double dmp4) {
		this.dmp4 = dmp4;
	}

	public Double getDmp5() {
		return dmp5;
	}

	public void setDmp5(Double dmp5) {
		this.dmp5 = dmp5;
	}

	public Double getDmp6() {
		return dmp6;
	}

	public void setDmp6(Double dmp6) {
		this.dmp6 = dmp6;
	}

	public Double getDmp7() {
		return dmp7;
	}

	public void setDmp7(Double dmp7) {
		this.dmp7 = dmp7;
	}

	public Double getDmp8() {
		return dmp8;
	}

	public void setDmp8(Double dmp8) {
		this.dmp8 = dmp8;
	}

	public Double getDmp9() {
		return dmp9;
	}

	public void setDmp9(Double dmp9) {
		this.dmp9 = dmp9;
	}

	public Double getDmp10() {
		return dmp10;
	}

	public void setDmp10(Double dmp10) {
		this.dmp10 = dmp10;
	}

	public Double getDmp11() {
		return dmp11;
	}

	public void setDmp11(Double dmp11) {
		this.dmp11 = dmp11;
	}

	public Double getDmp12() {
		return dmp12;
	}

	public void setDmp12(Double dmp12) {
		this.dmp12 = dmp12;
	}

	public Double getDmp13() {
		return dmp13;
	}

	public void setDmp13(Double dmp13) {
		this.dmp13 = dmp13;
	}

	public Double getDmp14() {
		return dmp14;
	}

	public void setDmp14(Double dmp14) {
		this.dmp14 = dmp14;
	}

	public Double getDmp15() {
		return dmp15;
	}

	public void setDmp15(Double dmp15) {
		this.dmp15 = dmp15;
	}

	public Double getDmp16() {
		return dmp16;
	}

	public void setDmp16(Double dmp16) {
		this.dmp16 = dmp16;
	}

	public Double getDmp17() {
		return dmp17;
	}

	public void setDmp17(Double dmp17) {
		this.dmp17 = dmp17;
	}

	public Double getDmp18() {
		return dmp18;
	}

	public void setDmp18(Double dmp18) {
		this.dmp18 = dmp18;
	}

	public Double getDmp19() {
		return dmp19;
	}

	public void setDmp19(Double dmp19) {
		this.dmp19 = dmp19;
	}

	public Double getDmp20() {
		return dmp20;
	}

	public void setDmp20(Double dmp20) {
		this.dmp20 = dmp20;
	}

	public Double getDmp21() {
		return dmp21;
	}

	public void setDmp21(Double dmp21) {
		this.dmp21 = dmp21;
	}

	public Double getDmp22() {
		return dmp22;
	}

	public void setDmp22(Double dmp22) {
		this.dmp22 = dmp22;
	}

	public Double getDmp23() {
		return dmp23;
	}

	public void setDmp23(Double dmp23) {
		this.dmp23 = dmp23;
	}

	public Double getDmp24() {
		return dmp24;
	}

	public void setDmp24(Double dmp24) {
		this.dmp24 = dmp24;
	}

	public Double getDmp25() {
		return dmp25;
	}

	public void setDmp25(Double dmp25) {
		this.dmp25 = dmp25;
	}

	public Double getDmp26() {
		return dmp26;
	}

	public void setDmp26(Double dmp26) {
		this.dmp26 = dmp26;
	}

	public Double getDmp27() {
		return dmp27;
	}

	public void setDmp27(Double dmp27) {
		this.dmp27 = dmp27;
	}

	public Double getDmp28() {
		return dmp28;
	}

	public void setDmp28(Double dmp28) {
		this.dmp28 = dmp28;
	}

	public Double getDmp29() {
		return dmp29;
	}

	public void setDmp29(Double dmp29) {
		this.dmp29 = dmp29;
	}

	public Double getDmp30() {
		return dmp30;
	}

	public void setDmp30(Double dmp30) {
		this.dmp30 = dmp30;
	}

	public Double getDmp31() {
		return dmp31;
	}

	public void setDmp31(Double dmp31) {
		this.dmp31 = dmp31;
	}

	public Double getDma1() {
		return dma1;
	}

	public void setDma1(Double dma1) {
		this.dma1 = dma1;
	}

	public Double getDma2() {
		return dma2;
	}

	public void setDma2(Double dma2) {
		this.dma2 = dma2;
	}

	public Double getDma3() {
		return dma3;
	}

	public void setDma3(Double dma3) {
		this.dma3 = dma3;
	}

	public Double getDma4() {
		return dma4;
	}

	public void setDma4(Double dma4) {
		this.dma4 = dma4;
	}

	public Double getDma5() {
		return dma5;
	}

	public void setDma5(Double dma5) {
		this.dma5 = dma5;
	}

	public Double getDma6() {
		return dma6;
	}

	public void setDma6(Double dma6) {
		this.dma6 = dma6;
	}

	public Double getDma7() {
		return dma7;
	}

	public void setDma7(Double dma7) {
		this.dma7 = dma7;
	}

	public Double getDma8() {
		return dma8;
	}

	public void setDma8(Double dma8) {
		this.dma8 = dma8;
	}

	public Double getDma9() {
		return dma9;
	}

	public void setDma9(Double dma9) {
		this.dma9 = dma9;
	}

	public Double getDma10() {
		return dma10;
	}

	public void setDma10(Double dma10) {
		this.dma10 = dma10;
	}

	public Double getDma11() {
		return dma11;
	}

	public void setDma11(Double dma11) {
		this.dma11 = dma11;
	}

	public Double getDma12() {
		return dma12;
	}

	public void setDma12(Double dma12) {
		this.dma12 = dma12;
	}

	public Double getDma13() {
		return dma13;
	}

	public void setDma13(Double dma13) {
		this.dma13 = dma13;
	}

	public Double getDma14() {
		return dma14;
	}

	public void setDma14(Double dma14) {
		this.dma14 = dma14;
	}

	public Double getDma15() {
		return dma15;
	}

	public void setDma15(Double dma15) {
		this.dma15 = dma15;
	}

	public Double getDma16() {
		return dma16;
	}

	public void setDma16(Double dma16) {
		this.dma16 = dma16;
	}

	public Double getDma17() {
		return dma17;
	}

	public void setDma17(Double dma17) {
		this.dma17 = dma17;
	}

	public Double getDma18() {
		return dma18;
	}

	public void setDma18(Double dma18) {
		this.dma18 = dma18;
	}

	public Double getDma19() {
		return dma19;
	}

	public void setDma19(Double dma19) {
		this.dma19 = dma19;
	}

	public Double getDma20() {
		return dma20;
	}

	public void setDma20(Double dma20) {
		this.dma20 = dma20;
	}

	public Double getDma21() {
		return dma21;
	}

	public void setDma21(Double dma21) {
		this.dma21 = dma21;
	}

	public Double getDma22() {
		return dma22;
	}

	public void setDma22(Double dma22) {
		this.dma22 = dma22;
	}

	public Double getDma23() {
		return dma23;
	}

	public void setDma23(Double dma23) {
		this.dma23 = dma23;
	}

	public Double getDma24() {
		return dma24;
	}

	public void setDma24(Double dma24) {
		this.dma24 = dma24;
	}

	public Double getDma25() {
		return dma25;
	}

	public void setDma25(Double dma25) {
		this.dma25 = dma25;
	}

	public Double getDma26() {
		return dma26;
	}

	public void setDma26(Double dma26) {
		this.dma26 = dma26;
	}

	public Double getDma27() {
		return dma27;
	}

	public void setDma27(Double dma27) {
		this.dma27 = dma27;
	}

	public Double getDma28() {
		return dma28;
	}

	public void setDma28(Double dma28) {
		this.dma28 = dma28;
	}

	public Double getDma29() {
		return dma29;
	}

	public void setDma29(Double dma29) {
		this.dma29 = dma29;
	}

	public Double getDma30() {
		return dma30;
	}

	public void setDma30(Double dma30) {
		this.dma30 = dma30;
	}

	public Double getDma31() {
		return dma31;
	}

	public void setDma31(Double dma31) {
		this.dma31 = dma31;
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
