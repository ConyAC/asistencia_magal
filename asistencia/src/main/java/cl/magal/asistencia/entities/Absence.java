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

import cl.magal.asistencia.entities.converter.AbsenceTypeConverter;
import cl.magal.asistencia.entities.enums.AbsenceType;

@Entity
@Table(name="absence")
public class Absence implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2396669065703217090L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	@Column(name="absenceId")
	Long absenceId;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "from_date" )
	Date fromDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "to_date" )
	Date toDate;
	
	String description;
	
	@ManyToOne
	@JoinColumn(name="laborerId")
	Laborer laborer;
	
	@Column(name="absence_type")
	@Convert(converter = AbsenceTypeConverter.class)
	AbsenceType absenceType;

	public Long getAbsenceId() {
		return absenceId;
	}

	public void setAbsenceId(Long absencesId) {
		this.absenceId = absencesId;
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

	public Laborer getLaborer() {
		return laborer;
	}

	public void setLaborer(Laborer laborer) {
		this.laborer = laborer;
	}

	public AbsenceType getAbsenceType() {
		return absenceType;
	}

	public void setAbsenceType(AbsenceType absencesType) {
		this.absenceType = absencesType;
	}
	
	public int getTotal(){
		return Days.daysBetween(new DateTime(fromDate), new DateTime(toDate)).getDays();
	}
	
}
