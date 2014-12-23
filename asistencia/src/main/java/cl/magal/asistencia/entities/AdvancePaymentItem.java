package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Embeddable	
public class AdvancePaymentItem implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -308617978092836076L;

	@NotEmpty(message="El código de suple es obligatorio")
	@NotNull(message="El código de suple es obligatorio")
	@Basic(optional = false)
    @Column(name = "suple_code",unique=true)
	Integer supleCode;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name="suple_total_amount")
	Double supleTotalAmount;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name="suple_normal_amount")
	Double supleNormalAmount;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name="suple_increase_amount")
	Double supleIncreaseAmount;

	public Integer getSupleCode() {
		return supleCode;
	}

	public void setSupleCode(Integer supleCode) {
		this.supleCode = supleCode;
	}

	public Double getSupleTotalAmount() {
		return supleTotalAmount;
	}

	public void setSupleTotalAmount(Double supleTotalAmount) {
		this.supleTotalAmount = supleTotalAmount;
	}

	public Double getSupleNormalAmount() {
		return supleNormalAmount;
	}

	public void setSupleNormalAmount(Double supleNormalAmount) {
		this.supleNormalAmount = supleNormalAmount;
	}

	public Double getSupleIncreaseAmount() {
		return supleIncreaseAmount;
	}

	public void setSupleIncreaseAmount(Double supleAdvanceAmount) {
		this.supleIncreaseAmount = supleAdvanceAmount;
	}

	@Override
	public String toString() {
		return "AdvancePaymentItem [supleCode=" + supleCode
				+ ", supleTotalAmount=" + supleTotalAmount
				+ ", supleNormalAmount=" + supleNormalAmount
				+ ", supleAdvanceAmount=" + supleIncreaseAmount + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((supleCode == null) ? 0 : supleCode.hashCode());
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
		AdvancePaymentItem other = (AdvancePaymentItem) obj;
		if (supleCode == null) {
			if (other.supleCode != null)
				return false;
		} else if (!supleCode.equals(other.supleCode))
			return false;
		return true;
	}
	
}
