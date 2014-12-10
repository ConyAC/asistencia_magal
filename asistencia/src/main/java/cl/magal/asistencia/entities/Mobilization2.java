package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

@Entity
@Table(name="mobilization2")
public class Mobilization2 implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8278432240240965377L;

	@Id
	@JoinColumn(name="construction_siteId")
	@ManyToOne
	ConstructionSite constructionSite;
	
	@Digits(fraction = 0, integer = 0)
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
	
}
