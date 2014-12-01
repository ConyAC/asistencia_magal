package cl.magal.asistencia.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "configurations")
public class Configurations {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "configurationsId")
	Long configurationsId;
	
	@Column(name="minWage")
	Double minWage;
	
	@Column(name="collation")
	Double collation;
	
	@Column(name="mobilization")
	Double mobilization;

	public Long getConfigurationsId() {
		return configurationsId;
	}

	public void setConfigurationsId(Long configurationsId) {
		this.configurationsId = configurationsId;
	}

	public Double getMinWage() {
		return minWage;
	}

	public void setMinWage(Double minWage) {
		this.minWage = minWage;
	}

	public Double getCollation() {
		return collation;
	}

	public void setCollation(Double collation) {
		this.collation = collation;
	}

	public Double getMobilization() {
		return mobilization;
	}

	public void setMobilization(Double mobilization) {
		this.mobilization = mobilization;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((configurationsId == null) ? 0 : configurationsId.hashCode());
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
		Configurations other = (Configurations) obj;
		if (configurationsId == null) {
			if (other.configurationsId != null)
				return false;
		} else if (!configurationsId.equals(other.configurationsId))
			return false;
		return true;
	}

	
}
