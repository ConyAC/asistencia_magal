package cl.magal.asistencia.ui.workerfile.vo;

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
	Double reward;
	Integer numberOfAccidents;
	Date endingDate;
	
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
	public Double getReward() {
		return reward;
	}
	public void setReward(Double reward) {
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
	
	

}