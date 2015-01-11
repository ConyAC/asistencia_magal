package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

@Embeddable
@Table(name="Mobilization2")
public class Mobilization2 implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8278432240240965377L;

	@JoinColumn(name="LINKED_CONSTRUCTION_SITEID")
	ConstructionSite constructionSite;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name ="amount")
	Double amount;

	public ConstructionSite getConstructionSite() {
		return constructionSite;
	}

	public void setConstructionSite(ConstructionSite constructionSite) {
		this.constructionSite = constructionSite;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((constructionSite == null) ? 0 : constructionSite.hashCode());
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
		Mobilization2 other = (Mobilization2) obj;
		if (constructionSite == null) {
			if (other.constructionSite != null)
				return false;
		} else if (!constructionSite.equals(other.constructionSite))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Mobilization2 [constructionSite=" + constructionSite
				+ ", amount=" + amount + "]";
	}
	
	
	
	
	
}
