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
	Long familyAllowanceConfigurationsId;
	
	@Digits(fraction = 2, integer = 8)
	@Column(name ="fromr")
	Double from;
	
	@Digits(fraction = 2, integer = 8)
	@Column(name ="to")
	Double to;
	
	@Digits(fraction = 2, integer = 8)
	@Column(name ="amount")
	Double amount;

	public Long getFamilyAllowanceConfigurationsId() {
		return familyAllowanceConfigurationsId;
	}

	public void setFamilyAllowanceConfigurationsId(
			Long familyAllowanceConfigurationsId) {
		this.familyAllowanceConfigurationsId = familyAllowanceConfigurationsId;
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
				+ ((familyAllowanceConfigurationsId == null) ? 0
						: familyAllowanceConfigurationsId.hashCode());
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
		if (familyAllowanceConfigurationsId == null) {
			if (other.familyAllowanceConfigurationsId != null)
				return false;
		} else if (!familyAllowanceConfigurationsId
				.equals(other.familyAllowanceConfigurationsId))
			return false;
		return true;
	}
	
	
}
