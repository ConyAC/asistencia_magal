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
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.joda.time.Days;

@Entity
@Table(name="progressive_vacation")
public class ProgressiveVacation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 254150224024395580L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="progressive_vacationId")
	Long id;
	
	@NotNull(message="La fecha inicial es necesaria.")
	@Temporal(TemporalType.DATE)
	@Column(name = "from_date" )
	Date fromDate;
	
	@NotNull(message="La fecha final es necesaria.")
	@Temporal(TemporalType.DATE)
	@Column(name = "to_date" )
	Date toDate;
	
	@Column(name = "progressive" )
	int progressive;
	
	@ManyToOne
	@JoinColumn(name ="laborer_constructionsiteId",updatable=false,nullable=false)
	LaborerConstructionsite laborerConstructionSite;
	
	@Column(name = "confirmed" )
	boolean confirmed = false;
	
	public int getProgressive() {
		return progressive;
	}

	public void setProgressive(int progressive) {
		this.progressive = progressive;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long vacationId) {
		this.id = vacationId;
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

	public LaborerConstructionsite getLaborerConstructionSite() {
		return laborerConstructionSite;
	}

	public void setLaborerConstructionSite(
			LaborerConstructionsite laborerConstructionSite) {
		this.laborerConstructionSite = laborerConstructionSite;
	}

	public int getTotal(){
		return Days.daysBetween(new DateTime(fromDate), new DateTime(toDate)).getDays() + 1;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

}
