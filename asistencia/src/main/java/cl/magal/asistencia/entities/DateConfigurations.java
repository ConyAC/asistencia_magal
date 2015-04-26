package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

@Entity
@Table(name = "date_configurations")
public class DateConfigurations implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -476018997848461271L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "date_configurationsId")
	Long id;
	
	@Column(name="date")
	@Temporal(TemporalType.DATE)
	Date date;
	
	@Column(name="advance")
	@Temporal(TemporalType.DATE)
	Date advance;
	
	@Column(name="assistance")
	@Temporal(TemporalType.DATE)
	Date assistance;
	
	@Column(name="begin_deal")
	@Temporal(TemporalType.DATE)
	Date beginDeal;
	
	@Column(name="finish_deal")
	@Temporal(TemporalType.DATE)
	Date finishDeal;
	
//	@Digits(fraction = 2, integer = 6,message="debe ser un número de máximo 2 decimales")
//	@Column(name ="benzine")
//	Double benzine = 0D;
//	
//	@Digits(fraction = 2, integer = 6,message="debe ser un número sin decimales")
//	@Column(name ="oil")
//	Double oil = 0D;
	
	@Digits(fraction = 2, integer = 6,message="debe ser un número de máximo 2 decimales")
	@Column(name ="uf")
	Double uf = 0D;

	public Long getDateConfigurationsId() {
		return id;
	}

	public void setDateConfigurationsId(Long dateConfigurationsId) {
		this.id = dateConfigurationsId;
	}
	
	public Date getAdvance() {
		return advance;
	}

	public void setAdvance(Date advance) {
		this.advance = advance;
	}

	public Date getAssistance() {
		return assistance;
	}

	public void setAssistance(Date assistance) {
		this.assistance = assistance;
	}

	public Date getBeginDeal() {
		return beginDeal;
	}

	public void setBeginDeal(Date beginDeal) {
		this.beginDeal = beginDeal;
	}

	public Date getFinishDeal() {
		return finishDeal;
	}

	public void setFinishDeal(Date finishDeal) {
		this.finishDeal = finishDeal;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
//	public Double getBenzine() {
//		return benzine;
//	}
//
//	public void setBenzine(Double benzine) {
//		this.benzine = benzine;
//	}

//	public Double getOil() {
//		return oil;
//	}
//
//	public void setOil(Double oil) {
//		this.oil = oil;
//	}

	public Double getUf() {
		return uf;
	}

	public void setUf(Double uf) {
		this.uf = uf;
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
		DateConfigurations other = (DateConfigurations) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
