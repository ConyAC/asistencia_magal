package cl.magal.asistencia.ui.vo;

import java.io.Serializable;
import java.util.Date;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.enums.Job;

/**
 * Clase que permite agrupar los elementos necesarios para la vista de resumen de trabajador
 * "Obra","Rol","Jornal Promedio","Premio","N° Accidentes","Fecha Termino"
 * @author Pablo
 *
 */
public class HistoryVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3948289625303476869L;
	
	ConstructionSite constructionSite;
	Job job;
	Double averageWage;
	int reward;
	Integer numberOfAccidents;
	Date endingDate;
	Date startingDate;
	boolean active;
	
	public ConstructionSite getConstructionSite() {
		return constructionSite;
	}
	public void setConstructionSite(ConstructionSite constructionSite) {
		this.constructionSite = constructionSite;
	}
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public Double getAverageWage() {
		return averageWage;
	}
	public void setAverageWage(Double averageWage) {
		this.averageWage = averageWage;
	}
	public int getReward() {
		return reward;
	}
	public void setReward(int reward) {
		this.reward = reward;
	}
	public Integer getNumberOfAccidents() {
		return numberOfAccidents;
	}
	public void setNumberOfAccidents(Integer numberOfAccidents) {
		this.numberOfAccidents = numberOfAccidents;
	}
	public Date getEndingDate() {
		return endingDate;
	}
	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
	}
	public Date getStartingDate() {
		return startingDate;
	}
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
