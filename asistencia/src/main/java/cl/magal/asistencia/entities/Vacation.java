package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="vacation")
public class Vacation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 254150224024395580L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="vacationId")
	Long vacationId;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "from_date" )
	Date fromDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "to_date" )
	Date toDate;
	
	@ManyToOne
	@JoinColumn(name ="laborerId")
	Laborer laborer;
	
	@ManyToOne
	@JoinColumn(name="constructionSiteId")
	ConstructionSite constructionSite;

	public Long getVacationId() {
		return vacationId;
	}

	public void setVacationId(Long vacationId) {
		this.vacationId = vacationId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Laborer getLaborer() {
		return laborer;
	}

	public void setLaborer(Laborer laborer) {
		this.laborer = laborer;
	}

	public ConstructionSite getConstructionSite() {
		return constructionSite;
	}

	public void setConstructionSite(ConstructionSite constructionSite) {
		this.constructionSite = constructionSite;
	}
	
	

}
