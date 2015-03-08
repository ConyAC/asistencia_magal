package cl.magal.asistencia.ui.vo;

import java.io.Serializable;
import java.util.Date;

import cl.magal.asistencia.entities.enums.AbsenceType;

/**
 * Objeto que permite encapsular los distintos tipos de ausencia : accidente, licencias y vacaciones 
 * una descripción, fechas de vigencia y la confirmación de oficina central
 * @author Pablo Carreño
 *
 */
public class AbsenceVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5807711225979130834L;
	
	private AbsenceType type;
	private Integer absenceId;
	private String description;
	private Date fromDate;
	private Date toDate;
	public AbsenceType getType() {
		return type;
	}
	public void setType(AbsenceType type) {
		this.type = type;
	}
	public Integer getAbsenceId() {
		return absenceId;
	}
	public void setAbsenceId(Integer absenceId) {
		this.absenceId = absenceId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
}