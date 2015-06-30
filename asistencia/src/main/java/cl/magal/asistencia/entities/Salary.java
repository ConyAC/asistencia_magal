package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.enums.AttendanceMark;
import cl.magal.asistencia.services.bo.SalaryCalculator;
import cl.magal.asistencia.services.bo.SupleCalculator;

@Entity
@Table(name="salary")
public class Salary implements Serializable {
	
	
	transient static Logger logger = LoggerFactory.getLogger(Salary.class);
	
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
	//usado para visualizar el ultimo jornal promedio
	transient Integer lastJornalPromedio = 0;
	
	@ManyToOne
	@JoinColumn(name="laborer_constructionsiteId",nullable=false)
	LaborerConstructionsite laborerConstructionSite;
	
	@NotNull(message="La fecha es necesaria.")
	@Temporal(TemporalType.DATE)
	@Column(name = "date" ,nullable = false )
	Date date;
	
	@Column(name = "suple")
	Double suple;
	
//	@Column(name = "salary")
	transient Double salary;
	
	@Column(name = "calculated_suple")
	boolean calculatedSuple = true;
	
	@Column(name="mov2_bond")
	Integer bondMov2 = 0;
	
	@Column(name="special_bond")
	Integer specialBond = 0;
	
	@Column(name="overtime_hours")
	Integer overtimeHours = 0;
	
	@Column(name="desc_hours")
	Integer descHours = 0;
	
	@Column(name="loan_bond")
	Integer loanBond = 0;
	
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
            int loans){
		if(this.salaryCalculator == null )
			throw new RuntimeException("Es necesario que el objeto de calculo sea distinto a null");
		this.salaryCalculator.setInformation(getSuple(), tool, loan, attendance, lastMonthAttendance, overtime, loans);
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
		return jornalPromedio == null? 0 : jornalPromedio;
	}
	public void setJornalPromedio(Integer jornalPromedio) {
		this.jornalPromedio = jornalPromedio;
	}
	public double getSuple() {
		if(suple == null && isCalculatedSuple() ){
			if(supleCalculator == null )
				throw new RuntimeException("El calculador de anticipos no puede ser nulo.");
			suple = supleCalculator.calculateSuple(getLaborerConstructionSite().getSupleCode());
		}else if( suple == null && !isCalculatedSuple() )
			suple = 0d;
		return suple;
	}
	public void setSuple(double suple) {
		this.suple = suple;
	}
	public double getSalary() {
		if(salary == null){
			if(salaryCalculator == null )
				throw new RuntimeException("El calculador de sueldos no puede ser nulo.");
			salary = salaryCalculator.calculateSalary(getJornalPromedio(),getSuple(),this);
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
		salary = null;
		salaryCalculator.resetCal();
		return salary == null;
	}
	
	public boolean getForceSuple(){
		suple = null;
		supleCalculator.resetCal();
		return suple == null;
	}
	
	public Integer getBondMov2() {
		return bondMov2 == null ? 0 : bondMov2;
	}
	public void setBondMov2(Integer bondMov2) {
		this.bondMov2 = bondMov2;
	}
	public Integer getOvertimeHours() {
		return overtimeHours;
	}
	public void setOvertimeHours(Integer overtimeHours) {
		this.overtimeHours = overtimeHours;
	}
	public Integer getDescHours() {
		return descHours == null ? 0 : descHours;
	}
	public void setDescHours(Integer descHours) {
		this.descHours = descHours;
	}
	public boolean isCalculatedSuple() {
		return calculatedSuple;
	}
	public void setCalculatedSuple(boolean calculatedSuple) {
		this.calculatedSuple = calculatedSuple;
	}
	
	public Integer getSpecialBond() {
		return specialBond == null ? 0 : specialBond;
	}
	public void setSpecialBond(Integer specialBond) {
		this.specialBond = specialBond;
	}

	public Integer getLastJornalPromedio() {
		return lastJornalPromedio == null ? 0 : lastJornalPromedio ;
	}
	
	public void setLastJornalPromedio(Integer lastJornalPromedio) {
		this.lastJornalPromedio = lastJornalPromedio;
	}
	
	public Integer getLoanBond() {
        return salaryCalculator.getLoans();
    }
	
	/**
	 * columnas ocultables
	 * @return
	 */
	public double getJornalBaseMes(){
		return salaryCalculator.getJornalBaseMes();
	}
	
	public double getVtrato(){
		return salaryCalculator.getVTrato();
	}
	
	public double getValorSabado(){
		return salaryCalculator.getValorSabado();
	}
	
	public double getVsCorrd(){
		return salaryCalculator.getVSCorrd();
	}
	
	public double getSobreTiempo(){
		return salaryCalculator.getSobreTiempo();
	}
	
	public double getDescHoras(){
		return salaryCalculator.getDescHoras();
	}
	
	public double getBonifImpo(){
		return salaryCalculator.getBonifImpo();
	}
	
	public double getGlegal(){
		return salaryCalculator.getGLegal();
	}
	
	public double getAfecto(){
		return salaryCalculator.getAfecto();
	}
	
	public double getSobreAfecto(){
		return salaryCalculator.getSobreAfecto();
	}
	
	public double getCargas(){
		return laborerConstructionSite.getLaborer().getDependents();
	}
	
	public double getAsigFamiliar(){
		return salaryCalculator.getAsigFamiliar();
	}
	
	public double getColacion(){
		return salaryCalculator.getColacion();
	}
	
	public double getMov(){
		return salaryCalculator.getMov();
	}
	
	public double getMov2(){
		return salaryCalculator.getMov2();
	}
	
	public double getTnoAfecto(){
		return salaryCalculator.getTNoAfecto();
	}
	
	public double getLoan(){
		return salaryCalculator.getLoanFee();
	}
	
	public double getTools(){
		return salaryCalculator.getToolFee();
	}
	public List<AttendanceMark> getAjusteMesAnterior(){
		return salaryCalculator.getAjusteMesAnterior();
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
		Salary other = (Salary) obj;
		if (laborerConstructionSite == null) {
			if (other.laborerConstructionSite != null)
				return false;
		} else if (!laborerConstructionSite.getId()
				.equals(other.laborerConstructionSite.getId()))
			return false;
		return true;
	}
	
	private void mask(StringBuilder sb,String code,Object value){
		sb.append("\"").append(((getLaborerConstructionSite().getConstructionsite().getCostCenter() * 1000) + getLaborerConstructionSite().getJobCode()))
		.append("\";\"").append(code).append("\"")
		.append(";\"").append(new DateTime(getDate()).toString("MM/yyyy")).append("\"")
		.append(";\"").append(value).append("\"#");
	}

	public String salaryToSofland(){
		
		StringBuilder sb = new StringBuilder();
		mask(sb,"P007",salaryCalculator.getDiaTrab());//Dias trabajados de lunes a viernes
		mask(sb,"P005",salaryCalculator.getSep().intValue());//Septimos
		mask(sb,"P011",salaryCalculator.getDpd().intValue());//Dp Septimos (DPD)
		mask(sb,"P006",salaryCalculator.getCol().intValue());//D Colacion (DPS)
		mask(sb,"P006",salaryCalculator.getMov().intValue());//D movil
		mask(sb,"H001",getJornalPromedio().intValue());//J Promedio
		mask(sb,"H011",salaryCalculator.getSobreTiempo().intValue());//Sobretiempo
		mask(sb,"H012",salaryCalculator.getBonifImpo().intValue());//Bonif Imponible
		mask(sb,"P024",(int)salaryCalculator.getCollationConfig());//$ Colacion Dia
		mask(sb,"P025",(int)salaryCalculator.getMov1Config());//$ Mov 1 Dia
		mask(sb,"P010",salaryCalculator.getMov2Export().intValue());//Dias Mov 2
		mask(sb,"P097",salaryCalculator.getMov2DayExport().intValue());//$Mov 2 Dia
		mask(sb,"P012",salaryCalculator.getSab());//Sabados
		mask(sb,"P013",salaryCalculator.getDps());//DP Sabados
		mask(sb,"P014",salaryCalculator.getDescHoras().intValue());//Descuento Horas
		// En el código generador de softland no está este codigo, revisar!
//		mask(sb,"P041","");//Cuota Herr
//		mask(sb,"P042","");//Cuota Prestamo
		
		return sb.toString();
	}
	
	public String supleToSofland(){
		StringBuilder sb = new StringBuilder();
		mask(sb,"D020",(int)getSuple());//DT L-V
		return sb.toString();
	}
}
