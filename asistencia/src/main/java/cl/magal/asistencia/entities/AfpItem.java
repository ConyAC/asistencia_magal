package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "afp_item")
public class AfpItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1881942842723477339L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "afp_itemId")
    Long id;
	
    @Basic(optional = false)
    @NotNull(message="El nombre es necesario")
    @Column(name = "name")
    private String name;
	
	@Digits(fraction=4,integer=3,message="Solo es posible definir 4 decimales")
	@Column(name="rate")
	Double rate;
	
	@ManyToOne
	@JoinColumn(name ="afp_and_insuranceId",updatable=false,nullable=false)
	AfpAndInsuranceConfigurations afpAndInsuranceConfigurations;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}
	
	public AfpAndInsuranceConfigurations getAfpAndInsuranceConfigurations() {
		return afpAndInsuranceConfigurations;
	}

	public void setAfpAndInsuranceConfigurations(
			AfpAndInsuranceConfigurations afpAndInsuranceConfigurations) {
		this.afpAndInsuranceConfigurations = afpAndInsuranceConfigurations;
	}

	@Override
	public String toString() {
		return "AfpItem [id=" + id + ", name=" + name + ", rate=" + rate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
	
}
