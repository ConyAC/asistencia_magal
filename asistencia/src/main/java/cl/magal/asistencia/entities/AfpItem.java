package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;

import cl.magal.asistencia.entities.converter.AfpConverter;
import cl.magal.asistencia.entities.enums.Afp;

@Embeddable
public class AfpItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1881942842723477339L;
	
	@Convert(converter = AfpConverter.class)
	@Column(name="afp")
	Afp afp;
	
	@Digits(fraction=2,integer=3,message="Solo es posible definir 2 decimales")
	@Column(name="rate")
	Double rate;

	public Afp getAfp() {
		return afp;
	}

	public void setAfp(Afp afp) {
		this.afp = afp;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		return "AfpItem [" + (afp != null ? "afp=" + afp + ", " : "")
				+ (rate != null ? "rate=" + rate : "") + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((afp == null) ? 0 : afp.hashCode());
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
		AfpItem other = (AfpItem) obj;
		if (afp != other.afp)
			return false;
		return true;
	}
	
}
