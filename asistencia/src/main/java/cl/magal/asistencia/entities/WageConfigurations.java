package cl.magal.asistencia.entities;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

@Entity
@Table(name="wage_configurations")
public class WageConfigurations {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "wage_configurationsId")
	Long wageConfigurationsId;
	
	@Digits(fraction = 0, integer = 0)
	@Column(name ="minimum_wage")
	Double minimumWage;
	
	@Digits(fraction = 0, integer = 0)
	@Column(name ="collation")
	Double collation;
	
	@Digits(fraction = 0, integer = 0)
	@Column(name ="mobilization")
	Double mobilization;
	
	@OneToMany(mappedBy="constructionSite")
	List<Mobilization2> mobilizations2 = new LinkedList<Mobilization2>();
	
	public Long getWageConfigurationsId() {
		return wageConfigurationsId;
	}

	public void setWageConfigurationsId(Long wageConfigurationsId) {
		this.wageConfigurationsId = wageConfigurationsId;
	}

	public Double getMinimumWage() {
		return minimumWage;
	}

	public void setMinimumWage(Double minimumWage) {
		this.minimumWage = minimumWage;
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

	public List<Mobilization2> getMobilizations2() {
		return mobilizations2;
	}

	public void setMobilizations2(List<Mobilization2> mobilizations2) {
		this.mobilizations2 = mobilizations2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((wageConfigurationsId == null) ? 0 : wageConfigurationsId
						.hashCode());
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
		WageConfigurations other = (WageConfigurations) obj;
		if (wageConfigurationsId == null) {
			if (other.wageConfigurationsId != null)
				return false;
		} else if (!wageConfigurationsId.equals(other.wageConfigurationsId))
			return false;
		return true;
	}
	
}
