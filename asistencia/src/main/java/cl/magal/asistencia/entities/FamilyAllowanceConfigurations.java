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
@Table(name="family_allowance_configurations")
public class FamilyAllowanceConfigurations implements Serializable {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5652884837051929636L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "family_allowance_configurationsId")
	Long id;
	
	@Digits(fraction = 2, integer = 8)
	@Column(name ="fromr")
	Double from;
	
	@Digits(fraction = 2, integer = 8)
	@Column(name ="tor")
	Double to;
	
	@Digits(fraction = 2, integer = 8)
	@Column(name ="amount")
	Double amount;

	public Long getId() {
		return id;
	}

	public void setId(Long familyAllowanceConfigurationsId) {
		this.id = familyAllowanceConfigurationsId;
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
		FamilyAllowanceConfigurations other = (FamilyAllowanceConfigurations) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id
				.equals(other.id))
			return false;
		return true;
	}
	
	
}
