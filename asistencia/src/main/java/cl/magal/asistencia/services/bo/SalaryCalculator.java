package cl.magal.asistencia.services.bo;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.ExtraParams;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.Mobilization2;
import cl.magal.asistencia.entities.Overtime;
import cl.magal.asistencia.entities.TaxationConfigurations;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.entities.enums.AttendanceMark;

public class SalaryCalculator {
	
	Logger logger = LoggerFactory.getLogger(SalaryCalculator.class);
	
	/**
	 * PARAMETROS
	 */
	DateTime closingDateLastMonth;
	Double suple, tool, loan,
		   sueldoMinimo;
	Attendance attendance,lastMonthAttendance;
	Overtime overtime;
	ExtraParams extraParams;
	Date date;
	List<FamilyAllowanceConfigurations> famillyTable;
	List<TaxationConfigurations> taxTable;
	Integer jornalPromedio;
	
	double bonoImponibleEspecial,bonoCargoLoc2, km, bencina, horasDescuento, horasSobreTiempo,ufMes,mov2,collation,mov1;
	
	/**
	 * CALCULOS
	 */
	Double afecto;
	Double getAfecto(){
		if(afecto == null ) {
			afecto = calculateAfecto(closingDateLastMonth,getDiasHabiles(),attendance,lastMonthAttendance,overtime,getJornalBaseMes());
			logger.debug("afecto {}",afecto);
		}
		return afecto;
	}
		
	Double sobreAfecto;
	Double getSobreAfecto(){
		if(sobreAfecto == null ){
			sobreAfecto = calculateSobreAfecto(closingDateLastMonth,getAfecto(),getDiasHabiles(),attendance,lastMonthAttendance,overtime,getJornalBaseMes());
			logger.debug("sobreAfecto {}",sobreAfecto);
		}
		return sobreAfecto;
	}
	
	Double tNoAfecto;
	Double getTNoAfecto(){
		if(tNoAfecto == null ){
			tNoAfecto = calculateTNoAfecto(closingDateLastMonth,getAfecto(),getDiasHabiles(),attendance,lastMonthAttendance);
			logger.debug("tNoAfecto {}",tNoAfecto);
		}
		return tNoAfecto;
	}

	Double tDesc;
	Double getTDesc(){
		if ( tDesc == null ) {
			tDesc= calculateTDesc(getAfecto(),getSobreAfecto(),suple,tool,loan);
			logger.debug("tDesc {}",tDesc);
		}
		return tDesc;
	}
	
	Double jornalBaseMes;
	Double getJornalBaseMes(){
		if( jornalBaseMes == null )
			jornalBaseMes = sueldoMinimo  / 30;
		return jornalBaseMes;
	}
	
	Integer diasHabiles ;
	Integer getDiasHabiles(){
		if( diasHabiles == null )
			diasHabiles  = calculateDiasHabilesMes(date);
		return diasHabiles ;
	}
	
	Integer diaTrab;
	Integer getDiaTrab(){
		if(diaTrab == null ){
			diaTrab = calculateDiaTrab(closingDateLastMonth,attendance,lastMonthAttendance);
			logger.debug("diaTrab {}",diaTrab);
		}
		return diaTrab;
	}
	
	Double col;
	Double getCol(){
		if(col == null ){
			col = calculateCol(closingDateLastMonth,attendance,lastMonthAttendance);
			logger.debug("col {}",col);
		}
		return col;
	}
	
	Integer diasNoConsideradosMesAnterior;
	Integer getDiasNoConsideradosMesAnterior(){
		if(diasNoConsideradosMesAnterior == null ){
			diasNoConsideradosMesAnterior = calculateDiasNoConsideradosMesAnterior(closingDateLastMonth,attendance,lastMonthAttendance);
			logger.debug("diasNoConsideradosMesAnterior {}",diasNoConsideradosMesAnterior);
		}
		return diasNoConsideradosMesAnterior;
	}
	
	Double vTrato;
	Double getVTrato(){
		if(vTrato == null ){
			vTrato = calculateVTrato(closingDateLastMonth,attendance,lastMonthAttendance);
		}
		return vTrato;
	}
	
	/**
	 * fuerza a que se recalculen las variables reutilizables
	 * @return
	 */
	public boolean resetCal(){
		afecto= null;
		sobreAfecto= null;
		tNoAfecto = null;
		tDesc = null;
		jornalBaseMes = null;
		diasHabiles = null;
		diaTrab = null;
		col = null;
		diasNoConsideradosMesAnterior = null;
		vTrato = null;
		return true;
	}
	
	/**
	 * Usar este constructor cuando se quiera calcular un unico sueldo
	 * @param closingDateLastMonth
	 * @param suple
	 * @param tool
	 * @param loan
	 * @param attendance
	 * @param lastMonthAttendance
	 * @param overtime
	 * @param extraParams
	 * @param wageConfigurations
	 * @param dateConfigurations
	 * @param famillyTable
	 * @param taxTable
	 */
	public SalaryCalculator(DateTime closingDateLastMonth, 
			                double suple , 
			                double tool , 
			                double loan,
			                Attendance attendance,
			                Attendance lastMonthAttendance,
			                Overtime overtime,
			                ExtraParams extraParams,
			                WageConfigurations wageConfigurations,
			                DateConfigurations dateConfigurations,
			                List<FamilyAllowanceConfigurations> famillyTable,
			                List<TaxationConfigurations> taxTable){
		
		
		setInformation(suple, tool, loan, attendance, lastMonthAttendance, overtime, extraParams);
		init(closingDateLastMonth, wageConfigurations, dateConfigurations, famillyTable, taxTable);
		
	}
	
	/**
	 * asigna la información necesaria para calcular un sueldo especifico
	 * @param suple
	 * @param tool
	 * @param loan
	 * @param attendance
	 * @param lastMonthAttendance
	 * @param overtime
	 * @param extraParams
	 */
	public void setInformation(double suple , 
            double tool , 
            double loan,
            Attendance attendance,
            Attendance lastMonthAttendance,
            Overtime overtime,
            ExtraParams extraParams){
		
		this.attendance = attendance;	
		this.date = attendance.getDate();	
		this.lastMonthAttendance = lastMonthAttendance;		
		this.overtime = overtime;	
		this.extraParams = extraParams;
		
		this.bonoImponibleEspecial = extraParams.getSpecialBond();
		this.bonoCargoLoc2 = extraParams.getBondMov2();
		this.km = extraParams.getKm();
		this.horasDescuento = extraParams.getDescHours();
		this.horasSobreTiempo = extraParams.getOvertimeHours();
		
		this.suple = suple;
		this.tool = tool;
		this.loan = loan;
		
	}
	
	/**
	 * 
	 * @param attendance2
	 */
	public void setAttendance(Attendance attendance2) {
		this.attendance = attendance2;
	}

	
	/**
	 * 
	 * @param closingDateLastMonth
	 * @param wageConfigurations
	 * @param dateConfigurations
	 * @param famillyTable
	 * @param taxTable
	 */
	public void setConfiguration(DateTime closingDateLastMonth, 
            WageConfigurations wageConfigurations,
            DateConfigurations dateConfigurations,
            List<FamilyAllowanceConfigurations> famillyTable,
            List<TaxationConfigurations> taxTable){
		init(closingDateLastMonth, wageConfigurations, dateConfigurations, famillyTable, taxTable);
	}
	
	/**
	 * Permite validar la información necesaria para correr el calculo del sueldo
	 */
	private void validateInformation(){
		
		if(closingDateLastMonth == null )
			throw new RuntimeException("Aún no se define una fecha de cierre del mes anterior, no se puede calcular el sueldo.");
		
		if(famillyTable == null || famillyTable.isEmpty() )
			throw new RuntimeException("Aún no se definen los valores de asignación familiar, no se puede calcular el sueldo.");
		
		if(taxTable == null || taxTable.isEmpty() )
			throw new RuntimeException("Aún no se definen los valores de impuestos, no se puede calcular el sueldo.");

		if(attendance == null )
			throw new RuntimeException("La asistencia no está definida(null)");
		
		if(attendance.getDate() == null )
			throw new RuntimeException("La asistencia tiene una fecha definida(null)");
		
		if(lastMonthAttendance == null )
			throw new RuntimeException("La asistencia del mes anterior no está definida(null)");
		
		if(overtime == null )
			throw new RuntimeException("Las horas de sobre tiempo, no están definidas(null)");
		
		if(extraParams == null )
			throw new RuntimeException("Los parámetros extras (bono especial, km ,etc), no están definidos(null)");
		
		if(suple == null )
			throw new RuntimeException("El suple no está definido(null)");
		if(tool == null )
			throw new RuntimeException("La deuda por herramientas no está definida(null)");
		if(loan == null )
			throw new RuntimeException("La deuda por prestamos no está definida(null)");
		if(jornalPromedio == null )
			throw new RuntimeException("Jornal Promedio no definido(null)");
	}
	
	/**
	 * Permite crear un objeto con la configuración global para ser reutilizado
	 * @param closingDateLastMonth
	 * @param wageConfigurations
	 * @param dateConfigurations
	 * @param famillyTable
	 * @param taxTable
	 */
	public SalaryCalculator(DateTime closingDateLastMonth, 
            WageConfigurations wageConfigurations,
            DateConfigurations dateConfigurations,
            List<FamilyAllowanceConfigurations> famillyTable,
            List<TaxationConfigurations> taxTable){
		init(closingDateLastMonth, wageConfigurations, dateConfigurations, famillyTable, taxTable);
	}
	
	/**
	 * inicializa el objeto con la configuración global necesaria para los calculos
	 * @param closingDateLastMonth
	 * @param wageConfigurations
	 * @param dateConfigurations
	 * @param famillyTable
	 * @param taxTable
	 */
	private void init(DateTime closingDateLastMonth, 
            WageConfigurations wageConfigurations,
            DateConfigurations dateConfigurations,
            List<FamilyAllowanceConfigurations> famillyTable,
            List<TaxationConfigurations> taxTable){
		
		if(closingDateLastMonth == null )
			throw new RuntimeException("Aún no se define una fecha de cierre del mes anterior, no se puede calcular el sueldo.");
		this.closingDateLastMonth = closingDateLastMonth;
		
		if(wageConfigurations == null )
			throw new RuntimeException("Aún no se definen los valores de sueldo mínimo, colación y movilización, no se puede calcular el sueldo.");
		
		this.sueldoMinimo = wageConfigurations.getMinimumWage();
		this.collation = wageConfigurations.getCollation();
		this.mov1 = wageConfigurations.getMobilization();
		//asigna la movilización 2 si corresponde
		this.mov2 = 0;
		for(Mobilization2 m2 : wageConfigurations.getMobilizations2()){
			if(m2.getConstructionSite().equals(attendance.getLaborerConstructionSite().getConstructionsite())){
				mov2 = m2.getAmount();
				break;
			}
		}
		
		if(famillyTable == null || famillyTable.isEmpty() )
			throw new RuntimeException("Aún no se definen los valores de asignación familiar, no se puede calcular el sueldo.");
		this.famillyTable = famillyTable;
		
		if(taxTable == null || taxTable.isEmpty() )
			throw new RuntimeException("Aún no se definen los valores de impuestos, no se puede calcular el sueldo.");
		this.taxTable = taxTable;

		if(dateConfigurations == null )
			throw new RuntimeException("Aún no se definen los valores de fecha de cierres, uf, bencina, petroleo, etc., no se puede calcular el sueldo.");
		
		this.bencina = dateConfigurations.getBenzine();
		this.ufMes = dateConfigurations.getUf();
	}

	/**
	 * Permite calcular el sueldo de un trabajador
	 * @return
	 */
	public double calculateSalary(Integer jornalPromedio,Double suple) {
		this.jornalPromedio = jornalPromedio;
		this.suple = suple;
		logger.debug("jornalPromedio {}",jornalPromedio);
		//valida que este toda la información necesaria para el calculo
		validateInformation();
		double salary = getAfecto() + getSobreAfecto() + getTNoAfecto() - getTDesc();
		logger.debug("salario calculado {}",salary);
		return salary;
	}
	
	/**
	 * DONE
	 * Calcula el sobre afecto, si éste último fue mayor al maximo imponible 
	 * @return
	 */
	private double calculateSobreAfecto(DateTime closingDateLastMonth,double afecto,int diasHabiles,Attendance attendance,Attendance lastMonthAttendance,Overtime overtime,double jornalBaseMes) {
		double maxImponible = calculateMaxImponible();
		if( maxImponible == afecto )
			return 0;
		else{
			double sum = getVTrato() + calculateValorSabado(closingDateLastMonth,attendance,lastMonthAttendance) + calculateVSCorrd(closingDateLastMonth,attendance,lastMonthAttendance,overtime) + calculateSobreTiempo(overtime) + calculateDescHoras(closingDateLastMonth,attendance,lastMonthAttendance) 
					+ calculateBonifImpo(closingDateLastMonth,attendance,lastMonthAttendance,jornalBaseMes) + calculateGLegal(closingDateLastMonth,diasHabiles,attendance,lastMonthAttendance);
			return sum - afecto;
		}
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateAfecto(DateTime closingDateLastMonth, int diasHabiles,Attendance attendance,Attendance lastMonthAttendance,Overtime overtime,double jornalBaseMes) {
		double maxImponible = calculateMaxImponible();
		double sum = getVTrato() + calculateValorSabado(closingDateLastMonth,attendance,lastMonthAttendance) + calculateVSCorrd(closingDateLastMonth,attendance,lastMonthAttendance,overtime) + calculateSobreTiempo(overtime) + 
				calculateDescHoras(closingDateLastMonth,attendance,lastMonthAttendance) + calculateBonifImpo(closingDateLastMonth,attendance,lastMonthAttendance,jornalBaseMes) + calculateGLegal(closingDateLastMonth,diasHabiles,attendance,lastMonthAttendance);
		return sum > maxImponible ? maxImponible : sum ;
	}
	/**
	 * DONE
	 * @return
	 */
	private double calculateGLegal(DateTime closingDateLastMonth,int diasHabilesMes,Attendance attendance,Attendance lastMonthAttendance) {
		return calculateGratificacionLegalMes(diasHabilesMes) * getDiaTrab();
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateGratificacionLegalMes(int diasHabilesMes) {
		return (4.75*sueldoMinimo/12)/diasHabilesMes;
	}

	/**
	 * DONE
	 * @return
	 */
	private int calculateDiasHabilesMes(Date date) {
		DateTime dt = new DateTime(date);
		DateTime firstDayOfMonth = dt.withDayOfMonth(1);
		DateTime lastDayOfMonth = dt.withDayOfMonth(dt.dayOfMonth().getMaximumValue());
		int days = 0;
		//cuenta los dias habiles
		while(!firstDayOfMonth.equals(lastDayOfMonth.plusDays(1))){
			int indexOfWeek = firstDayOfMonth.dayOfWeek().get();
			if( indexOfWeek > 0 && indexOfWeek < 6 )
				days ++;
			firstDayOfMonth = firstDayOfMonth.plusDays(1);
		}
		logger.debug("date {} , days {}",date,days);
		return days;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateBonifImpo(DateTime closingDate,Attendance attendance,Attendance lastMonthAttendance,double jornalBaseMes) {
		return calculateBonoBencina() + bonoImponibleEspecial + calculateLLuvia(attendance,jornalBaseMes) + calculateLLuviaMesAnterior(closingDate,attendance,lastMonthAttendance,jornalBaseMes) + calculateAjusteAsistenciaMesAnterior(closingDate,attendance,lastMonthAttendance);
	}

	/**
	 * DONE
	 * @return
	 */
	private int calculateAjusteAsistenciaMesAnterior(DateTime closingDate,Attendance attendance,Attendance lastMonthAttendance) {
		//TODO validar que sean meses consecutivos
		//desde la fecha de cierre del mes anterior, verifica si hay diferencias en la asistencia en cuanto 
		int sumAsistenciaAjustadaMesAnterior = countDiffMarksFromDate(closingDate,attendance,lastMonthAttendance,AttendanceMark.ATTEND); 
		return sumAsistenciaAjustadaMesAnterior * getJPromedio();
	}

	/**
	 * DONE
	 * @param closingDate
	 * @param attendance
	 * @param lastMonthAttendance
	 * @param marks
	 * @return
	 */
	private int countDiffMarksFromDate(DateTime closingDate,Attendance attendance, Attendance lastMonthAttendance, AttendanceMark ... marks) {
		return countDiffMarksFromDay(closingDate.getDayOfMonth(),closingDate.dayOfMonth().getMaximumValue(),attendance,lastMonthAttendance,marks);
	}

	/**
	 * DONE
	 * @param lastClosingDate
	 * @param attendance
	 * @param lastMonthAttendance
	 * @param marks
	 * @return
	 */
	private int countDiffMarksFromDay(Integer lastClosingDate,int maxDays, Attendance attendance,Attendance lastMonthAttendance, AttendanceMark ... marks) {
		if(attendance == null || lastMonthAttendance == null )
			throw new RuntimeException("Los objeto de asistencia no pueden ser nulo.");

		List<AttendanceMark> lastRealMarks = attendance.getLastMarksAsList();
		List<AttendanceMark> projectionsMarks = lastMonthAttendance.getMarksAsList();
		int count = 0;
		for(int i = lastClosingDate != null ? lastClosingDate : 0 ; i < maxDays ; i ++){
			//si son distintos, lo contabiliza
			if(lastRealMarks.get(i) != projectionsMarks.get(i)){
				//lo cuenta solo si está dentro del grupo a contabilizar
				AttendanceMark mark = lastRealMarks.get(i);
				if(ArrayUtils.contains(marks, mark)){
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * DONE
	 * =DL32*$DF$7
	 * @return
	 */
	private double calculateLLuviaMesAnterior(DateTime closingDate,Attendance attendance, Attendance lastMonthAttendance, double jornalBaseMes) {
		int sumLLuviaAjuste = countDiffMarksFromDate(closingDate,attendance,lastMonthAttendance,AttendanceMark.RAIN);
		logger.debug("sumLLuviaAjuste {} ",sumLLuviaAjuste);
		return sumLLuviaAjuste * jornalBaseMes;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateLLuvia(Attendance attendance, double jornalBaseMes) {
		int sumLluvia = countMarks(null,attendance,AttendanceMark.RAIN); 
		return sumLluvia * jornalBaseMes * 1.2;
	}

	/**
	 * DONE
	 * =(64+$DC$7/8)*DD32
	 * @return
	 */
	private double calculateBonoBencina() {
		return (64 + bencina / 8 )*km;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateDescHoras(DateTime closingDateLastMonth,Attendance attendance,Attendance lastMonthAttendance) {
		return -1 * (getJPromedio() / 7.5 * horasDescuento + getDiasNoConsideradosMesAnterior() * 0.2 * getJPromedio() );
	}

	/**
	 * DONE calcula los días no considerados del mes anterior 
	 * @return
	 */
	private int calculateDiasNoConsideradosMesAnterior(DateTime closingDateLastMonth,Attendance attendance,Attendance lastMonthAttendance) {
		int sumAsistenciaAjustadaMesAnterior = countDiffMarksFromDate(closingDateLastMonth,attendance,lastMonthAttendance,
				AttendanceMark.RAIN,AttendanceMark.FAIL,AttendanceMark.SICK,AttendanceMark.PERMISSION,AttendanceMark.ACCIDENT);
		return sumAsistenciaAjustadaMesAnterior;
	}

	/**
	 * DONE 
	 * =AS32/7,5*1,5*(CZ32-DA32)
	 * @return
	 */
	private double calculateSobreTiempo(Overtime overtime) {
		return getJPromedio()/7.5*1.5*(calculateHorasSobrtpo(overtime) - horasSobreTiempo );
	}

	/**
	 * DONE Considera la suma de las horas por sobre tiempo
	 * @return
	 */
	private int calculateHorasSobrtpo(Overtime overtime) {
		int count = 0;
		for(Integer i : overtime.getOvertimeAsList()){
			count += i;
		}
		for(Integer i : overtime.getLastMonthOvertimeAsList()){
			count += i;
		}
		return count;
	}

	/**
	 * DONE 
	 * @return
	 */
	private double calculateVSCorrd(DateTime closingDateLastMonth,Attendance attendance,Attendance lastMonthAttendance,Overtime overtime) {
		return ( getVTrato() + calculateValorSabado(closingDateLastMonth,attendance,lastMonthAttendance) + calculateSobreTiempo(overtime) )/ calculateDPD(attendance) * calculateSep(attendance);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateSep(Attendance attendance) {
		return countMarks(null,attendance,AttendanceMark.SUNDAY); 
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateDPD(Attendance attendance) {
		return countMarks(null,attendance,AttendanceMark.ATTEND,AttendanceMark.SATURDAY,AttendanceMark.RAIN,AttendanceMark.PERMISSION,
				AttendanceMark.FILLER,AttendanceMark.FAIL);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateValorSabado(DateTime closingDateLastMonth,Attendance attendance,Attendance lastMonthAttendance) {
		return ( getVTrato() +  calculateDescHoras(closingDateLastMonth,attendance,lastMonthAttendance) ) / calculateDPS(attendance) * calculateSab(attendance);
	}

	/**
	 * DONE
	 * =CONTAR.SI($D32:$AI32;"X")+CONTAR.SI($D32:$AI32;"LL")+CONTAR.SI($D32:$AK32;"P")+CONTAR.SI($D32:$AI32;"R")+CONTAR.SI($D32:$AI32;"F")+CONTAR.SI($D32:$AI32;"V")
	 * @return
	 */
	private int calculateDPS(Attendance attendance) {
		return countMarks(null,attendance,AttendanceMark.ATTEND,AttendanceMark.RAIN,AttendanceMark.PERMISSION,
				AttendanceMark.FILLER,AttendanceMark.FAIL,AttendanceMark.VACATION);
	}

	/**
	 * DONE
	 * @return
	 */
	private int calculateSab(Attendance attendance) {
		return countMarks(null,attendance,AttendanceMark.SATURDAY);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateVTrato(DateTime closingDateLastMonth,Attendance attendance,Attendance lastMonthAttendance) {
		double vTrato =  getJPromedio()*getDiaTrab();
		logger.debug("vTrato {}",vTrato);
		return vTrato;
	}

	/**
	 * DONE
	 * @return
	 */
	private int getJPromedio() {
		return jornalPromedio;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateMaxImponible() {
		return 70.3*ufMes;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateTNoAfecto(DateTime closingDateLastMonth,double afecto,int diasHabiles,Attendance attendance,Attendance lastMonthAttendance) {
		double asigFam = calculateAsigFamiliar(closingDateLastMonth,afecto,attendance,lastMonthAttendance); 
		logger.debug("asigFam {}",asigFam);
		double colacion = calculateColacion(closingDateLastMonth,attendance,lastMonthAttendance);
		logger.debug("colacion {}",colacion);
		double mov = calculateMov(closingDateLastMonth,attendance,lastMonthAttendance); 
		logger.debug("mov {}",mov);
		double mov2 = calculateMov2(closingDateLastMonth, attendance, lastMonthAttendance); 
		logger.debug("mov2 {}",mov2);
		return  asigFam + colacion + mov + mov2;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateMov2(DateTime closingDateLastMonth,Attendance attendance,Attendance lastMonthAttendance) {
		return getCol()*mov2+ bonoCargoLoc2;
	}

	/**
	 * DONE
	 * =CONTAR.SI(D32:AK32;"X")+CONTAR.SI(D32:AK32;"ll")-C32-CONTAR.SI(DN32:DX32;"S")-CONTAR.SI(DN32:DX32;"V")
	 * @return
	 */
	private double calculateCol(DateTime closingDateLastMonth,Attendance attendance,Attendance lastMonthAttendance) {
		int sum1 = countMarks(null,attendance,AttendanceMark.ATTEND,AttendanceMark.RAIN);
		logger.debug("sum1 {}",sum1);
		int sumAsistenciaAjustadaMesAnterior = getDiasNoConsideradosMesAnterior();
		int sum2 = countDiffMarksFromDate(closingDateLastMonth,attendance,lastMonthAttendance,AttendanceMark.SATURDAY,AttendanceMark.VACATION);
		logger.debug("sum2 {}",sum2);
		
		return sum1 - sumAsistenciaAjustadaMesAnterior - sum2;
	}

	
	/**
	 * DONE
	 * @return
	 */
	private double calculateMov(DateTime closingDateLastMonth,Attendance attendance,Attendance lastMonthAttendance) {
		return mov1 * getCol();
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateColacion(DateTime closingDateLastMonth,Attendance attendance,Attendance lastMonthAttendance) {
		return collation * getCol();
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateAsigFamiliar(DateTime closingDateLastMonth,double afecto,Attendance attendance,Attendance lastMonthAttendance) {

		//busca en que rango está el calculo del impuesto
		Double result = 0D;
		double factor = afecto / getDiaTrab();
		logger.debug("asig fam factor {} ",factor);
		for( FamilyAllowanceConfigurations tax : famillyTable 	){
			if( factor >= tax.getFrom() && factor <= tax.getTo() ){
				result = tax.getAmount();
				break;
			}
		}
		int cargas = getCargas();
		logger.debug("result {} , carga {} ",result , cargas);
		return result * cargas;
	}

	/**
	 * DONE
	 * calcular los dias trabajados del obrero en el mes
	 * =CONTAR.SI(D32:AI32;"X")+CONTAR.SI(D32:AI32;"V")-C32
	 * @return
	 */
	private int calculateDiaTrab(DateTime closingDateLastMonth,Attendance attendance,Attendance lastMonthAttendance) {
		int diasTrabMesActual = countMarks(null,attendance,AttendanceMark.ATTEND,AttendanceMark.VACATION);
		logger.debug("diasTrabMesActual {}",diasTrabMesActual);
		int sumAsistenciaAjustadaMesAnterior = getDiasNoConsideradosMesAnterior();
		
		return diasTrabMesActual - sumAsistenciaAjustadaMesAnterior;
	}

	/**
	 * TODO obtiene las cargas del trabajador
	 * @return
	 */
	private int getCargas() {
		return attendance.getLaborerConstructionSite().getLaborer().getDependents();
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateTDesc(double afecto,double sobreAfecto,double suple , double tool , double loan) {
		double descImposiciones = calculateDescImposiciones(afecto);
		logger.debug("descImposiciones {}",descImposiciones);
		double impuesto = calculateImpuesto(afecto,sobreAfecto);
		logger.debug("impuesto {}",impuesto);
		logger.debug("suple {}, tool {}, loan {}",suple,tool,loan);
		return descImposiciones + impuesto + suple - tool - loan;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateImpuesto(double afecto,double sobreAfecto) {
		double tTribut = calculateTTribut(afecto,sobreAfecto);
		return tTribut *calculateImpuesto2Cat(tTribut)-calculateADescontar(tTribut);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateTTribut(double afecto,double sobreAfecto) {
		return afecto + sobreAfecto - calculateDescImposiciones(afecto);
	}

	/**
	 * DONE
	 * @return
	 */
	private Double calculateImpuesto2Cat(double tTribut) {
		//busca en que rango está el calculo del impuesto
		Double result = 0D;
		for( TaxationConfigurations tax : taxTable 	){
			if( tax.getFrom() >= tTribut && tax.getTo() <= tTribut ){
				result = tax.getFactor();
				break;
			}
		}
		return result;
	}

	/**
	 * DONE
	 * @return
	 */
	private Double calculateADescontar(double tTribut) {
		//busca en que rango está el calculo del impuesto
		Double result = 0D;
		for( TaxationConfigurations tax : taxTable 	){
			if( tax.getFrom() >= tTribut && tax.getTo() <= tTribut ){
				result = tax.getReduction();
				break;
			}
		}
		return result;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateDescImposiciones(double afecto) {
		return calculate7Salud(afecto) + calculateAdicionalSalud(afecto) + calculateAFP(afecto);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateAFP(double afecto) {
		return afecto * calculateAFPPorcentaje();
	}

	/**
	 * TODO Busca el % de la afp asociado al trabajador, si éste no es pensionado. 
	 * @return
	 */
	private double calculateAFPPorcentaje() {
		return 0.1127;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateAdicionalSalud(double afecto) {
		double result = calculateMontoCtoUF() * ufMes - calculate7Salud(afecto);
		return result > 0 ? result : 0 ;
	}

	/**
	 * TODO Monto Cto UF ???
	 * @return
	 */
	private double calculateMontoCtoUF() {
		return 0D;
	}
	
	/**
	 * DONE
	 * @return
	 */
	private double calculate7Salud(double afecto) {
		return getPorcentajeSalud()*afecto;
	}

	/**
	 * TODO
	 * Dato global % de salud 
	 */
	private double getPorcentajeSalud(){
		return 0.07D;
	}
	
	/**
	 * Cuenta las marcas hasta el dia dada, si el dia dado es nulo, entonces cuenta todos los dias
	 * @param maxDay
	 * @param attendance
	 * @param marks
	 * @return
	 */
	private Integer countMarks(Integer maxDay,Attendance attendance, AttendanceMark ... marks) {
		if(attendance == null )
			throw new RuntimeException("El objeto de asistencia no puede ser nulo.");

		//si el número maximo es nulo, calcula el máximo según la fecha de la asistencia
		if(maxDay == null )
			maxDay = new DateTime(attendance.getDate()).dayOfMonth().getMaximumValue();
		
		int count = 0;
		List<AttendanceMark> attendanceMarks = attendance.getMarksAsList();
		for(int i = 0; i < maxDay ; i++){
			AttendanceMark mark = attendanceMarks.get(i);
			if(ArrayUtils.contains(marks, mark))
				count++;
		}
		return count;
	}


}
