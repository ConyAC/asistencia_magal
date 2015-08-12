package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

@Entity
@Table(name="wage_configurations")
public class WageConfigurations implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2342274495337607698L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wage_configurationsId")
	Long id;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name ="minimum_wage")
	Double minimumWage = 0D;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name ="collation")
	Double collation = 0D;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name ="mobilization")
	Double mobilization = 0D;
	
	@Digits(fraction = 6 , integer = 6 )
	@Column(name = "max_imponible_factor")
	Double maxImponibleFactor;
	
	@ElementCollection(targetClass=Mobilization2.class)
	@CollectionTable(
	        name="mobilization2",
	        joinColumns=@JoinColumn(name="wage_configurationsId")
	  )
	List<Mobilization2> mobilizations2 = new LinkedList<Mobilization2>();
	
	public Long getWageConfigurationsId() {
		return id;
	}

	public void setWageConfigurationsId(Long wageConfigurationsId) {
		this.id = wageConfigurationsId;
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
	
	public void removeMobilizations2(Mobilization2 mobilizations2){
		if(getMobilizations2().contains(mobilizations2)){
			getMobilizations2().remove(mobilizations2);
		}
	}
	
	public Double getMaxImponibleFactor() {
		return maxImponibleFactor;
	}

	public void setMaxImponibleFactor(Double maxImponibleFactor) {
		this.maxImponibleFactor = maxImponibleFactor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((id == null) ? 0 : id
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WageConfigurations [wageConfigurationsId="
				+ id + ", minimumWage=" + minimumWage
				+ ", collation=" + collation + ", mobilization=" + mobilization
				+ ", mobilizations2=" + mobilizations2 + "]";
	}
	
	
	
}
