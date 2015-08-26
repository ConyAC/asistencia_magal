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
    private Long id;
	
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

	@Override
	public String toString() {
		return "AfpItem [id=" + id + ", name=" + name + ", rate=" + rate + "]";
	}	
}
