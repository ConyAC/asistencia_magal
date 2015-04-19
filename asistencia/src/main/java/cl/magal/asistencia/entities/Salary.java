package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.services.bo.SalaryCalculator;
import cl.magal.asistencia.services.bo.SupleCalculator;

@Entity
@Table(name="salary")
public class Salary implements Serializable {
	
	
	Logger logger = LoggerFactory.getLogger(Salary.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6333506715020601537L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	@Column(name="salarytId")
	Long id;
	
	@Column(name="jornal")
	Integer jornalPromedio = 0;
	
	@ManyToOne
	@JoinColumn(name="laborer_constructionsiteId",nullable=false)
	LaborerConstructionsite laborerConstructionSite;
	
	@NotNull(message="La fecha es necesaria.")
	@Temporal(TemporalType.DATE)
	@Column(name = "date" ,nullable = false )
	Date date;
	
	@Column(name = "suple")
	Double suple;
	
	@Column(name = "salary")
	Double salary;
	
	/**
	 * Objeto que permite el calculo de los sueldos
	 */
	transient SalaryCalculator salaryCalculator;
	transient SupleCalculator supleCalculator;
	
	public void setSalaryCalculator(SalaryCalculator calculator) {
		this.salaryCalculator = calculator;
	}
	public void setSupleCalculator(SupleCalculator supleCalculator) {
		this.supleCalculator = supleCalculator;
	}
	public void setSalaryCalculatorInformation(
			double tool , 
            double loan,
            Attendance attendance,
            Attendance lastMonthAttendance,
            Overtime overtime,
            ExtraParams extraParams){
		if(this.salaryCalculator == null )
			throw new RuntimeException("Es necesario que el objeto de calculo sea distinto a null");
		this.salaryCalculator.setInformation(getSuple(), tool, loan, attendance, lastMonthAttendance, overtime, extraParams);
	}
	
	public void setSupleCalculatorInformation(
			Attendance attendance,
			Integer supleCode){
		if(this.supleCalculator == null )
			throw new RuntimeException("Es necesario que el objeto de calculo sea distinto a null");
		this.supleCalculator.setInformation( attendance,supleCode);
	}
	
	transient Attendance attendance;
	public Attendance getAttendance(){
		return attendance;
	}
	public void setAttendance(Attendance attendance){
		if(this.supleCalculator == null || this.salaryCalculator == null )
			throw new RuntimeException("Es necesario que el objeto de calculo sea distinto a null");
		supleCalculator.setAttendance(attendance);
		salaryCalculator.setAttendance(attendance);
		getForceSalary();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long salaryId) {
		this.id = salaryId;
	}
	public LaborerConstructionsite getLaborerConstructionSite() {
		return laborerConstructionSite;
	}
	public void setLaborerConstructionSite(LaborerConstructionsite laborerConstructionSite) {
		this.laborerConstructionSite = laborerConstructionSite;
	}
	
	public Integer getJornalPromedio() {
		return jornalPromedio;
	}
	public void setJornalPromedio(Integer jornalPromedio) {
		this.jornalPromedio = jornalPromedio;
	}
	public double getSuple() {
		if(suple == null )
			suple = supleCalculator.calculateSuple();
		return suple;
	}
	public void setSuple(double suple) {
		this.suple = suple;
	}
	public double getSalary() {
		if(salary == null){
			salary = salaryCalculator.calculateSalary(getJornalPromedio(),getSuple());
		}
		return salary;
	}
	public void setSalary(double salary) {
		this.salary = salary;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public int getRoundSalary(){
		return (int) Math.round( (getSalary()) );
	}
	
	public int getTotalLiquido(){
		return (int) Math.round( (getSuple()+getSalary()) );
	}
	
	public boolean getForceSalary(){
		logger.debug("forceSalary");
		salary = null;
		salaryCalculator.resetCal();
		return salary == null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((laborerConstructionSite == null) ? 0
						: laborerConstructionSite.hashCode());
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
		ExtraParams other = (ExtraParams) obj;
		if (laborerConstructionSite == null) {
			if (other.laborerConstructionSite != null)
				return false;
		} else if (!laborerConstructionSite.getId()
				.equals(other.laborerConstructionSite.getId()))
			return false;
		return true;
	}
}
