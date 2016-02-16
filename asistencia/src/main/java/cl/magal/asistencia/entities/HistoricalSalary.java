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

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name="historical_salary")
public class HistoricalSalary implements Serializable {
	
	
	transient static Logger logger = LoggerFactory.getLogger(HistoricalSalary.class);
	
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
	
	@Column(name="lastJornalPromedio")
	Integer lastJornalPromedio = 0;
	
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
	
	@JoinColumn(name="costaccountId",nullable=false)
	CostAccount costAccount;
	
//	@JoinColumn(name="attendanceId",nullable=false)
//	Attendance attendance;
	
	@Column(name="diaTrab")
	int diaTrab;
	
	@Column(name="sep")
	int sep;
	
	@Column(name="dpd")
	int dpd;
	
	@Column(name="col")
	int col;
	
	@Column(name="collationConfig")
	int collationConfig;
	
	@Column(name="mov1Config")
	int mov1Config;
	
	@Column(name="mov2Export")
	int mov2Export;
	
	@Column(name="mov2DayExport")
	int mov2DayExport;
	
	@Column(name="sab")
	int sab;
	
	@Column(name="dps")
	int dps;
	
	@Column(name="jornalBaseMes")
	double jornalBaseMes;
	
	@Column(name="vtrato")
	double vtrato;

	@Column(name="valorSabado")
	double valorSabado;
	
	@Column(name="vsCorrd")
	double vsCorrd;
	
	@Column(name="sobreTiempo")
	double sobreTiempo;
	
	@Column(name="descHoras")
	double descHoras;
	
	@Column(name="bonifImpo")
	double bonifImpo;
	
	@Column(name="glegal")
	double glegal;
	
	@Column(name="afecto")
	double afecto;
	
	@Column(name="sobreAfecto")
	double sobreAfecto;
	
	@Column(name="asigFamiliar")
	double asigFamiliar;
	
	@Column(name="colacion")
	double colacion;
	
	@Column(name="mov")
	double mov;
	
	@Column(name="mov2")
	double mov2;
	
	@Column(name="tnoAfecto")
	double tnoAfecto;
	
	@Column(name="loan")
	double loan;
	
	@Column(name="tools")
	double tools;
	
	public HistoricalSalary(){
		
	}
	
	public HistoricalSalary(Salary salary){
		if(salary == null )
			throw new RuntimeException("No se puede crear un sueldo histórico si el sueldo es nulo.");
		if(salary.getSalaryCalculator() == null )
			throw new RuntimeException("No se puede crear un sueldo histórico si el calculador de sueldo es nulo.");
		
		jornalPromedio = salary.getJornalPromedio();
		lastJornalPromedio = salary.getLastJornalPromedio();
		laborerConstructionSite = salary.getLaborerConstructionSite();
		date = salary.getDate();
		suple = salary.getSuple();
		this.salary = salary.getSalary();
		calculatedSuple = salary.isCalculatedSuple();
		bondMov2 = salary.getBondMov2();
		specialBond = salary.getSpecialBond();
		overtimeHours = salary.getOvertimeHours();
		descHours = salary.getDescHours();
		loanBond = salary.getLoanBond();
		diaTrab = salary.getSalaryCalculator().getDiaTrab();
		sep = salary.getSalaryCalculator().getSep().intValue();
		dpd = salary.getSalaryCalculator().getDpd().intValue();
		col = salary.getSalaryCalculator().getCol().intValue();
		collationConfig = Double.valueOf(salary.getSalaryCalculator().getCollationConfig()).intValue();
		mov1Config = Double.valueOf(salary.getSalaryCalculator().getMov1Config()).intValue();
		mov2Export = salary.getSalaryCalculator().getMov2Export().intValue();
		mov2DayExport = salary.getSalaryCalculator().getMov2DayExport().intValue();
		sab = salary.getSalaryCalculator().getSab();
		dps = salary.getSalaryCalculator().getDps();
		jornalBaseMes = salary.getJornalBaseMes();
		vtrato = salary.getVtrato();
		valorSabado = salary.getValorSabado();
		vsCorrd = salary.getVsCorrd();
		sobreTiempo = salary.getSobreTiempo();
		descHoras = salary.getDescHoras();
		bonifImpo = salary.getBonifImpo();
		glegal = salary.getGlegal();
		afecto = salary.getAfecto();
		sobreAfecto  = salary.getSobreAfecto();
		asigFamiliar = salary.getAsigFamiliar();
		colacion = salary.getColacion();
		mov = salary.getMov();
		mov2 = salary.getMov2();
		tnoAfecto = salary.getTnoAfecto();
		loan = salary.getLoan();
		costAccount = salary.getCostAccount();
		
		suple = suple.isNaN()?0:suple;
		this.salary = this.salary.isNaN() ? 0 :this.salary;
		descHours = descHours.isNaN() ? 0 : descHours;
		
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
		return suple;
	}
	public void setSuple(double suple) {
		this.suple = suple;
	}
	public double getSalary() {
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
        return loanBond;
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
		return jornalBaseMes;
	}
	
	public double getVtrato(){
		return vtrato;
	}
	
	public double getValorSabado(){
		return valorSabado;
	}
	
	public double getVsCorrd(){
		return vsCorrd;
	}
	
	public double getSobreTiempo(){
		return sobreTiempo;
	}
	
	public double getDescHoras(){
		return descHoras;
	}
	
	public double getBonifImpo(){
		return bonifImpo;
	}
	
	public double getGlegal(){
		return glegal;
	}
	
	public double getAfecto(){
		return afecto;
	}
	
	public double getSobreAfecto(){
		return sobreAfecto;
	}
	
	public double getCargas(){
		return laborerConstructionSite.getLaborer().getDependents();
	}
	
	public double getAsigFamiliar(){
		return asigFamiliar;
	}
	
	public double getColacion(){
		return colacion;
	}
	
	public double getMov(){
		return mov;
	}
	
	public double getMov2(){
		return mov2;
	}
	
	public double getTnoAfecto(){
		return tnoAfecto;
	}
	
	public double getLoan(){
		return loan;
	}
	
	public double getTools(){
		return tools;
	}
	
//	public Attendance getAttendance() {
//		return attendance;
//	}
//	public void setAttendance(Attendance attendance) {
//		this.attendance = attendance;
//	}
	public int getDiaTrab() {
		return diaTrab;
	}
	public void setDiaTrab(int diaTrab) {
		this.diaTrab = diaTrab;
	}
	public int getSep() {
		return sep;
	}
	public void setSep(int sep) {
		this.sep = sep;
	}
	public int getDpd() {
		return dpd;
	}
	public void setDpd(int dpd) {
		this.dpd = dpd;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public int getCollationConfig() {
		return collationConfig;
	}
	public void setCollationConfig(int collationConfig) {
		this.collationConfig = collationConfig;
	}
	public int getMov1Config() {
		return mov1Config;
	}
	public void setMov1Config(int mov1Config) {
		this.mov1Config = mov1Config;
	}
	public int getMov2Export() {
		return mov2Export;
	}
	public void setMov2Export(int mov2Export) {
		this.mov2Export = mov2Export;
	}
	public int getMov2DayExport() {
		return mov2DayExport;
	}
	public void setMov2DayExport(int mov2DayExport) {
		this.mov2DayExport = mov2DayExport;
	}
	public int getSab() {
		return sab;
	}
	public void setSab(int sab) {
		this.sab = sab;
	}
	public int getDps() {
		return dps;
	}
	public void setDps(int dps) {
		this.dps = dps;
	}
	public void setSuple(Double suple) {
		this.suple = suple;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	public void setLoanBond(Integer loanBond) {
		this.loanBond = loanBond;
	}
	public void setJornalBaseMes(double jornalBaseMes) {
		this.jornalBaseMes = jornalBaseMes;
	}
	public void setVtrato(double vtrato) {
		this.vtrato = vtrato;
	}
	public void setValorSabado(double valorSabado) {
		this.valorSabado = valorSabado;
	}
	public void setVsCorrd(double vsCorrd) {
		this.vsCorrd = vsCorrd;
	}
	public void setSobreTiempo(double sobreTiempo) {
		this.sobreTiempo = sobreTiempo;
	}
	public void setDescHoras(double descHoras) {
		this.descHoras = descHoras;
	}
	public void setBonifImpo(double bonifImpo) {
		this.bonifImpo = bonifImpo;
	}
	public void setGlegal(double glegal) {
		this.glegal = glegal;
	}
	public void setAfecto(double afecto) {
		this.afecto = afecto;
	}
	public void setSobreAfecto(double sobreAfecto) {
		this.sobreAfecto = sobreAfecto;
	}
	public void setAsigFamiliar(double asigFamiliar) {
		this.asigFamiliar = asigFamiliar;
	}
	public void setColacion(double colacion) {
		this.colacion = colacion;
	}
	public void setMov(double mov) {
		this.mov = mov;
	}
	public void setMov2(double mov2) {
		this.mov2 = mov2;
	}
	public void setTnoAfecto(double tnoAfecto) {
		this.tnoAfecto = tnoAfecto;
	}
	public void setLoan(double loan) {
		this.loan = loan;
	}
	public void setTools(double tools) {
		this.tools = tools;
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
		HistoricalSalary other = (HistoricalSalary) obj;
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
		mask(sb,"P007",diaTrab);//Dias trabajados de lunes a viernes
		mask(sb,"P005",sep);//Septimos
		mask(sb,"P011",dpd);//Dp Septimos (DPD)
		mask(sb,"P006",col);//D Colacion (DPS)
		mask(sb,"P006",Double.valueOf(mov).intValue());//D movil
		mask(sb,"H001",getJornalPromedio().intValue());//J Promedio
		mask(sb,"H011",Double.valueOf(sobreTiempo).intValue());//Sobretiempo
		mask(sb,"H012",Double.valueOf(bonifImpo).intValue());//Bonif Imponible
		mask(sb,"P024",collationConfig);//$ Colacion Dia
		mask(sb,"P025",mov1Config);//$ Mov 1 Dia
		mask(sb,"P010",mov2Export);//Dias Mov 2
		mask(sb,"P097",mov2DayExport);//$Mov 2 Dia
		mask(sb,"P012",sab);//Sabados
		mask(sb,"P013",dps);//DP Sabados
		mask(sb,"P014",Double.valueOf(descHoras).intValue());//Descuento Horas
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
