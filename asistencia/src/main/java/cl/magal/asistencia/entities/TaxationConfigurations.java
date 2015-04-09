package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

@Entity
@Table(name="taxation_configurations")
public class TaxationConfigurations implements Serializable {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5652884837051929636L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "taxation_configurationsId")
	Long id;
	
	@Digits(fraction = 2, integer = 12)
	@Column(name ="fromr")
	Double from;
	
	@Digits(fraction = 2, integer = 12)
	@Column(name ="tor")
	Double to;
	
	@Digits(fraction = 2, integer = 12)
	@Column(name ="factor")
	Double factor;
	
	@Digits(fraction = 2, integer = 12)
	@Column(name ="reduction")
	Double reduction;
	
	@Digits(fraction = 2, integer = 12)
	@Column(name ="exempt")
	Double exempt;

	public Long getId() {
		return id;
	}

	public void setId(Long taxationConfigurationsId) {
		this.id = taxationConfigurationsId;
	}

	public Double getFrom() {
		return from;
	}

	public void setFrom(Double from) {
		this.from = from;
	}

	public Double getTo() {
		return to;
	}

	public void setTo(Double to) {
		this.to = to;
	}

	public Double getFactor() {
		return factor;
	}

	public void setFactor(Double factor) {
		this.factor = factor;
	}

	public Double getReduction() {
		return reduction;
	}

	public void setReduction(Double reduction) {
		this.reduction = reduction;
	}

	public Double getExempt() {
		return exempt;
	}

	public void setExempt(Double exempt) {
		this.exempt = exempt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((id == null) ? 0
						: id.hashCode());
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
		TaxationConfigurations other = (TaxationConfigurations) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id
				.equals(other.id))
			return false;
		return true;
	}
	
}
