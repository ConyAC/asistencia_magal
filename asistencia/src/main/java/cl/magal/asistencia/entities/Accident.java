package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.joda.time.Days;

import cl.magal.asistencia.entities.converter.AccidentLevelConverter;

@Entity
@Table(name="accident")
public class Accident implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3199584809959506555L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	@Column(name="accidentId")
	Long accidentId;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "from_date" )
	Date fromDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "to_date" )
	Date toDate;
	
	String description;
	
	@ManyToOne
	@JoinColumn(name="LABORER_CONSTRUCTIONSITEID")
	LaborerConstructionsite laborerConstructionSite;
	
	@Column(name="accident_level")
	@Convert(converter = AccidentLevelConverter.class)
	AccidentLevel accidentLevel;
	
	boolean wasNegligence;

	public Long getAccidentId() {
		return accidentId;
	}

	public void setAccidentId(Long accidentId) {
		this.accidentId = accidentId;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LaborerConstructionsite getLaborerConstructionSite() {
		return laborerConstructionSite;
	}

	public void setLaborerConstructionSite(
			LaborerConstructionsite laborerConstructionSite) {
		this.laborerConstructionSite = laborerConstructionSite;
	}

	public AccidentLevel getAccidentLevel() {
		return accidentLevel;
	}

	public void setAccidentLevel(AccidentLevel accidentLevel) {
		this.accidentLevel = accidentLevel;
	}

	public boolean isWasNegligence() {
		return wasNegligence;
	}

	public void setWasNegligence(boolean wasNegligence) {
		this.wasNegligence = wasNegligence;
	}
	
	public int getTotal(){
		return Days.daysBetween(new DateTime(fromDate), new DateTime(toDate)).getDays();
	}	

}
