package cl.magal.asistencia.entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
	
	@Digits(fraction = 0, integer = 6)
	@Column(name ="minimum_wage")
	Double minimumWage;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name ="collation")
	Double collation;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name ="mobilization")
	Double mobilization;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(
	        name="Mobilization2",
	        joinColumns=@JoinColumn(name="wage_configurationsId")
	  )
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
		if(mobilizations2 == null )
			mobilizations2 = new ArrayList<Mobilization2>();
		return mobilizations2;
	}

	public void setMobilizations2(List<Mobilization2> mobilizations2) {
		this.mobilizations2 = mobilizations2;
	}
	
	public void addMobilizations2(Mobilization2 mobilizations2){
		int index = getMobilizations2().lastIndexOf(mobilizations2);
		if( index == -1 ){
			getMobilizations2().add(mobilizations2);
		}else{ //si lo contiene lo reem
			getMobilizations2().set(index, mobilizations2);
		}
	}
	
	public void removedMobilizations2(Mobilization2 mobilizations2){
		if(getMobilizations2().contains(mobilizations2)){
			getMobilizations2().remove(mobilizations2);
		}
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

	@Override
	public String toString() {
		return "WageConfigurations [wageConfigurationsId="
				+ wageConfigurationsId + ", minimumWage=" + minimumWage
				+ ", collation=" + collation + ", mobilization=" + mobilization
				+ ", mobilizations2=" + mobilizations2 + "]";
	}
	
	
	
}
