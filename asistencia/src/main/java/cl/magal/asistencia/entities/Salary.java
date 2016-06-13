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
	Double descHours = 0D;

	@Column(name="loan_bond")
	Integer loanBond = 0;

	@JoinColumn(name="costaccountId")
	CostAccount costAccount;

	/**
	 * Objeto que permite el calculo de los sueldos
	 */
	transient SalaryCalculator salaryCalculator;
	transient SupleCalculator supleCalculator;

	public SalaryCalculator getSalaryCalculator() {
		return salaryCalculator;
	}
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
		getForceSuple();
		getForceSalary();
	}

	public void setAdvancePaymentConfiguration(AdvancePaymentConfigurations api){
		if(this.supleCalculator == null || this.salaryCalculator == null )
			throw new RuntimeException("Es necesario que el objeto de calculo sea distinto a null");
		supleCalculator.setSupleTable(api);
		getForceSuple();
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
		boolean calculated = isCalculatedSuple();
		if(suple == null && calculated ){
			if(supleCalculator == null )
				throw new RuntimeException("El calculador de anticipos no puede ser nulo.");
			suple = supleCalculator.calculateSuple(getLaborerConstructionSite().getSupleCode());
		}else if( suple == null && !calculated )
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
		//lo resetea sólo si es calculado
		if(isCalculatedSuple())
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
	/**
	 * Número de horas de descuento
	 * @return
	 */
	public Double getDescHours() {
		return descHours == null ? 0D : descHours;
	}
	public void setDescHours(Double descHours) {
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

	public CostAccount getCostAccount() {
		return costAccount;
	}
	public void setCostAccount(CostAccount costAccount) {
		this.costAccount = costAccount;
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
	/**
	 * 	
	 * @return
	 */
	public String dump(){

		int salto = 1; 
		StringBuilder sb = new StringBuilder();
		sb.append("Total Liquido = Anticipos + A Pagar ").append(salto(salto));
		sb.append( getSalary() + getSuple() ).append(" = ").append(getSuple()).append(" + ").append(getSalary())
		.append(salto(salto));
		
		sb.append(salto(3)).append(salto(salto));
		
		sb.append("A Pagar = Afecto + Sobre Afecto + T No Afecto - T Desc ").append(salto(salto));
		sb.append(salary).append(" = ").append(salaryCalculator.getAfecto()).append(" + ").append(salaryCalculator.getSobreAfecto()).append(" + ").append(salaryCalculator.getTNoAfecto()).append(" - ").append(salaryCalculator.getTDesc()).append(salto(salto));
		
		sb.append(salto(3)).append(salto(salto));
		
		sb.append("Afecto = V Trato + Valor Sabado + V S Corrd + Sbto + Desc H + Bonif Imp + G Legal ").append(salto(salto));
		sb.append(salaryCalculator.getAfecto()).append(" = ").append(salaryCalculator.getVTrato()).append(" + ")
		.append(salaryCalculator.getValorSabado()).append(" + ").append(salaryCalculator.getVSCorrd())
		.append(" + ").append(salaryCalculator.getSobreTiempo()).append(" + ").append(salaryCalculator.getDescHoras())
		.append(" + ").append(salaryCalculator.getBonifImpo()).append(" + ").append(salaryCalculator.getGLegal()).append(salto(salto));
		
		sb.append(salto(3)).append(salto(salto));
		
		sb.append("V Trato = J Promedio * Dia Trab ").append(salto(salto));
		sb.append(salaryCalculator.getVTrato()).append(" = ").append(getJornalPromedio()).append(" * ")
		.append(salaryCalculator.getDiaTrab()).append(salto(salto));
		
		sb.append(salto(3)).append(salto(salto));
		
		sb.append("Valor Sabado = (V Trato + Descto Horas) / (Sab * DPS) ").append(salto(salto));
		sb.append(salaryCalculator.getValorSabado()).append(" = ( ").append(salaryCalculator.getVTrato()).append(" + ")
		.append(salaryCalculator.getDescHoras()).append(") / ( ").append(salaryCalculator.getSab()).append(" * ")
		.append(salaryCalculator.getDps()).append(" ) ").append(salto(salto));
		
		sb.append(salto(3)).append(salto(salto));
		
		sb.append("G Legal =  Gratif Legal * Dias Trab").append(salto(salto));
		sb.append(salaryCalculator.getGLegal()).append(" = ").append(salaryCalculator.getGratificacionLegalMes()).append(" * ")
		.append(salaryCalculator.getDiaTrab()).append(salto(salto));
		
		sb.append(salto(3)).append(salto(salto));
		
		sb.append("T Desc = Desc Imposicion + Imponible + Anticipo + Herramienta + Prestamo").append(salto(salto));
		sb.append(salaryCalculator.getTDesc()).append(" = ")
		.append(salaryCalculator.getDescImposicion()).append(" + ")
		.append(salaryCalculator.getImpto()).append(" + ").append(suple).append(" + ")
		.append(salaryCalculator.getToolFee()).append(" + ").append(salaryCalculator.getLoanFee()).append(salto(salto));
		
		sb.append(salto(3)).append(salto(salto));


		sb.append("Desc Imposicion = 7% Saludo (Isapre) + Adiciona Isapre (Monto cto UF) + $ AFP (AFP / %) ").append(salto(salto));
		sb.append(salaryCalculator.getDescImposicion()).append(" = ").append(salaryCalculator.get7Salud()).append(" + ").append(salaryCalculator.getSaludAdicional()).append("(").append(laborerConstructionSite.getLaborer().getIsapre()).append(")").append(" + ").append(salaryCalculator.getAfp()).append("(").append(laborerConstructionSite.getLaborer().getAfp().getName()).append(" - ").append(laborerConstructionSite.getLaborer().getAfp().getRate()).append(")").append(salto(salto));

		return sb.toString();
	}

	private String salto(int i) {
		switch (i){
		case 1 : 
			return "\n";
		case 2 :
			return "<br />";
		case 3 :
			return "----------------------------------------------------------";
		default : 
			return "\n";
		}
	}
	public String supleToSofland(){
		StringBuilder sb = new StringBuilder();
		mask(sb,"D020",(int)getSuple());//DT L-V
		return sb.toString();
	}
}
