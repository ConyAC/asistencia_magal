package cl.magal.asistencia.services.bo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AdvancePaymentItem;
import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.enums.AttendanceMark;

public class SupleCalculator {
	
	Logger logger = LoggerFactory.getLogger(SupleCalculator.class);
	
	AdvancePaymentConfigurations supleTable; 
	Date supleCloseDate; 
	Integer supleCode;
	Attendance attendance;
	
	public void setAttendance(Attendance attendance2) {
		this.attendance = attendance2;
	}
	
	/**
	 * 
	 * @param supleTable
	 * @param supleCloseDate
	 * @param supleCode
	 */
	public SupleCalculator(AdvancePaymentConfigurations supleTable, Date supleCloseDate,Attendance attendance, Integer supleCode ){
		init(supleTable, supleCloseDate);
	}
	
	/**
	 * 
	 * @param supleCode
	 */
	public void setInformation(Attendance attendance,Integer supleCode){
		this.attendance = attendance;
		this.supleCode = supleCode;
	}
	/**
	 * 
	 */
	private void validateInformationForSuple(){
		if(supleCode == null)
			throw new RuntimeException("Es necesario definir el codigo suple para calcular el suple.");
		if(supleCloseDate == null)
			throw new RuntimeException("Es necesario definir la fecha de cierre de suple para calcular el suple.");
		if(supleTable == null || supleTable.getMapTable() == null || supleTable.getMapTable().isEmpty() )
			throw new RuntimeException("Aún no se definen los valores de suple, no se puede calcular el suple.");
		if(attendance == null )
			throw new RuntimeException("Aún no se definen los valores de la asistencia, no se puede calcular el suple.");
	}
	
	/**
	 * 
	 * @param supleTable
	 * @param supleCloseDate
	 */
	public SupleCalculator(AdvancePaymentConfigurations supleTable, Date supleCloseDate){
		init(supleTable, supleCloseDate);
	}
	
	/**
	 * 
	 * @param supleTable
	 * @param supleCloseDate
	 */
	private void init(AdvancePaymentConfigurations supleTable, Date supleCloseDate){
		this.supleTable = supleTable;
		this.supleCloseDate = supleCloseDate;
	}
	
	/**
	 * Permite calcular el anticipo
	 * @param integer 
	 * @return
	 */
	public double calculateSuple(Integer supleCode){
		
		this.supleCode = supleCode;
		
		validateInformationForSuple();

		Map<Integer,AdvancePaymentItem> supleItemTable = supleTable.getMapTable();
		//primero obtiene el monto de anticipo que le corresponde por tabla
		Double maxAmount = supleItemTable.get(supleCode).getSupleTotalAmount();
		//obtiene el día en que se cierra el suple
		Integer supleCloseDay = new DateTime(supleCloseDate).dayOfMonth().get();
		//luego descuenta por cada X S V D LL 
		Integer countNotAttendance = countMarks(supleCloseDay,attendance,AttendanceMark.SATURDAY,AttendanceMark.ATTEND,AttendanceMark.VACATION,AttendanceMark.SUNDAY,AttendanceMark.RAIN);
		logger.debug("(supleCloseDay {} - countNotAttendance {} ) * supleTable.getFailureDiscount() ",supleCloseDay,countNotAttendance,supleTable.getFailureDiscount());
		Integer firstDiscount = (int) ((supleCloseDay - countNotAttendance)*supleTable.getFailureDiscount());
		logger.debug("first discount {}",firstDiscount);		
		Integer countFails = countMarks(supleCloseDay,attendance,AttendanceMark.FAIL);
		Integer secondDiscount = (int) (countFails*supleTable.getPermissionDiscount());
		logger.debug("second discount {}",secondDiscount);
		return maxAmount - firstDiscount - secondDiscount;
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

	/**
	 * No hace nada
	 */
	public void resetCal() {
		
	}
	
	/*
	 * Seteamos el contenido de la tabla de suple
	 */
	public void setSupleTable(AdvancePaymentConfigurations supleTable){
		this.supleTable = supleTable;
	}

}
