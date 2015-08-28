package cl.magal.asistencia.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.Accident;
import cl.magal.asistencia.entities.AfpItem;
import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Holiday;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.License;
import cl.magal.asistencia.entities.Mobilization2;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.entities.WithdrawalSettlement;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.AttendanceMark;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

public class Utils {
	
	static Logger logger = LoggerFactory.getLogger(Utils.class);

	static DecimalFormatSymbols decimalFormatSymbols;
	static DecimalFormat decimalFormatSinDecimal;
	
	public static DecimalFormatSymbols getDecimalFormatSymbols(){
		if(decimalFormatSymbols == null){
			decimalFormatSymbols = new DecimalFormatSymbols();
			decimalFormatSymbols.setDecimalSeparator(',');
			decimalFormatSymbols.setGroupingSeparator('.');
		}
		return decimalFormatSymbols;
	}
	
	public static DecimalFormat getDecimalFormatSinDecimal(){
		if(decimalFormatSinDecimal == null){
			decimalFormatSinDecimal = new DecimalFormat("#,###", getDecimalFormatSymbols());
		}
		return decimalFormatSinDecimal;
	}
	
	final static Random r = new Random();
	
	public static int random(){
		return random(10,100);
	}
	public static int random(int Low,int High){
		return r.nextInt(High-Low) + Low;
	}
	public static String printConstraintMessages(Set<ConstraintViolation<?>> constraintViolations) {
		StringBuilder sb = new StringBuilder();
		for(ConstraintViolation constraintViolation : constraintViolations){
			sb.append(String.format(constraintViolation.getMessage(), constraintViolation.getInvalidValue()) ).append("\n");
			logger.error(" error en validar el objeto {}, mensaje : {} valor: {} ",constraintViolation.getRootBean(),constraintViolation.getMessage(),constraintViolation.getInvalidValue());
		}
		return sb.toString();
	}
	
	public static void setLabelValue(TextField tf, String value){
		tf.setReadOnly(false);
		tf.setValue(value);
		tf.setReadOnly(true);
	}
	
	DateTime dt = new DateTime();
	
	public static String date2String(Date date){
		if(date == null )
			return "";
		return DateTimeFormat.forPattern("dd/MM/yyyy").print(new DateTime(date));
	}
	public static boolean NotNullOrEmpty(String etapa) {
		return etapa != null && etapa.length() != 0;
	}
	public static boolean contains(List<Laborer> laborers, Long long1) {
		if( laborers == null)
			return false;
		for(Laborer laborer :laborers ){
			if(laborer.getId() == long1 ){
				return true;
			}
		}
		return false;
	}
	
	 public static boolean containsMonth(Set<Date> dates, Date date) {
	        if(dates == null )
	            return false;
	        if(date == null)
	            return false;
	        for(Date d : dates){
	            if(new DateTime(d).getMonthOfYear() == new DateTime(date).getMonthOfYear())
	                return true;
	        }
	        return false;
	    }
	 
	 public static boolean containsMonth(List<Date> dates, Date date) {
	        if(dates == null )
	            return false;
	        if(date == null)
	            return false;
	        for(Date d : dates){
	            if(new DateTime(d).getMonthOfYear() == new DateTime(date).getMonthOfYear())
	                return true;
	        }
	        return false;
	    }
	public static void catchCommitException(CommitException e) {
		logger.debug("Error al comitear",e);
		Map<Field<?>, InvalidValueException> fieldsInvalidos = e.getInvalidFields();
		if(fieldsInvalidos.isEmpty()){
			logger.debug("La lista de campos inválidos es vacia",e);
			return;
		}
		logger.debug("fields con error {} ",fieldsInvalidos);
		Field<?> primerFieldConError =  fieldsInvalidos.keySet().iterator().next();
		String caption = primerFieldConError.getCaption() != null ? primerFieldConError.getCaption() : null;
		String message = fieldsInvalidos.get(primerFieldConError).getMessage();
		//si el mensaje es nulo, intenta buscarlo entre sus causas (esto pasa con el bean validator)
		if( message == null ){
			if(fieldsInvalidos.get(primerFieldConError).getCauses() != null && fieldsInvalidos.get(primerFieldConError).getCauses().length > 0 )
				message = fieldsInvalidos.get(primerFieldConError).getCauses()[0].getMessage();
		}
		Notification.show( ( caption != null ? caption +": ":"") + (message != null ?message:"Debe ingresar todos los elementos requeridos"),Type.ERROR_MESSAGE);
		primerFieldConError.focus();
		return;
		
//		Map<Field<?>,InvalidValueException> invalidFields = e.getInvalidFields();
//		//busca el primer field con error
//		Field<?> firstField = invalidFields.keySet().iterator().next();
//		logger.error("Error al guardar la información del usuario");
//		Notification.show("Error al guardar la información del usuario : "+invalidFields.get(firstField).getMessage(), Type.ERROR_MESSAGE);
	}

	public static String formatInteger(Integer number) {
		if(number == null )
			return "0";
		return getDecimalFormatSinDecimal().format(number);
	}
	
	public static String formatInteger(Double number) {
		if(number == null )
			return "0";
		return getDecimalFormatSinDecimal().format(number);
	}

	public static void notifyPropertyValueChanged(BeanItem<?> item,String... props) {
		if(props == null )
			throw new RuntimeException("La lista de propiedades no puede ser nula");
		if(item == null )
			throw new RuntimeException("El item no puede ser nulo");
		for(String propItem : props) {
			MethodProperty<?> statusProperty = (MethodProperty<?>)item.getItemProperty(propItem);
			if( statusProperty == null )
				continue;
			statusProperty.fireValueChange();
		}
		
	}

	public static boolean containsHoliday(List<Holiday> h, int day) {
		for(Holiday holiday : h){
			DateTime dt = new DateTime(holiday.getDate());
			if( dt.getDayOfMonth() == day ){
				return true;
			}
		}
		return false;
	}
	
	public static boolean containsVacation(List<Vacation> v, int day, LaborerConstructionsite lc, DateTime dt, int sabOrdom) {
		for(Vacation vacation : v){
		 Calendar calV = Calendar.getInstance();
		 calV.setTime(vacation.getFromDate());

		 if(sabOrdom != 6  && sabOrdom != 7 ){
				for (int i = 0; i <= vacation.getTotal();i++){
					 if(calV.get(Calendar.DAY_OF_MONTH) == day && new DateTime(calV.getTime()).getMonthOfYear() == dt.getMonthOfYear() && lc.getLaborer().getId() == vacation.getLaborerConstructionSite().getLaborer().getId()){
						return true;
					}
					 calV.add(Calendar.DATE, 1);
				}
		 	}
		}
		return false;
	}
	
	public static boolean containsLicense(List<License> l, int day, LaborerConstructionsite lc, DateTime dt) {
		for(License license : l){
		 Calendar calL = Calendar.getInstance();
		 calL.setTime(license.getFromDate());
		 for (int i = 0; i <= license.getTotal();i++){
			 if(calL.get(Calendar.DAY_OF_MONTH) == day && new DateTime(calL.getTime()).getMonthOfYear() == dt.getMonthOfYear() && lc.getLaborer().getId() == license.getLaborerConstructionSite().getLaborer().getId()){
					return true;
				}
			 calL.add(Calendar.DATE, 1);
			}
		}
		return false;
	}
	
	public static boolean containsAccident(List<Accident> a, int day, LaborerConstructionsite lc, DateTime dt) {
		for(Accident accident : a){
		 Calendar calA = Calendar.getInstance();
		 calA.setTime(accident.getFromDate());
		 for (int i = 0; i <= accident.getTotal() ;i++){
			 if(calA.get(Calendar.DAY_OF_MONTH) == day && new DateTime(calA.getTime()).getMonthOfYear() == dt.getMonthOfYear() && lc.getLaborer().getId() == accident.getLaborerConstructionSite().getLaborer().getId()){
					return true;
				}
			 calA.add(Calendar.DATE, 1);
			}	
		}		
		return false;
	}

	/**
	 * Permite buscar en el container si existe un item con la propiedad dada
	 * @param container
	 * @param propertyId
	 * @param value
	 * @return Verdadero si el contenedor contiene el valor dado en la propiedad dada, y falso si no.
	 */
	public static boolean containsContainer(BeanItemContainer container, String propertyId,String value) {
		if(!container.getContainerPropertyIds().contains(propertyId) )
			throw new RuntimeException("El contenedor no tiene la propiedad \""+propertyId+"\" dada.");
		for(Object itemId : container.getItemIds()){
			BeanItem item = container.getItem(itemId);
			if(item.getItemProperty(propertyId).getValue().equals(value))
				return true;
		}
		return false;
	}
	
	/**
	 * Permite buscar en el container si existe un item con la propiedad dada
	 * @param container
	 * @param propertyId
	 * @param value
	 * @return Verdadero si el contenedor contiene el valor dado en la propiedad dada, y falso si no.
	 */
	public static boolean containsTwoContainer(BeanItemContainer container, String[] propertyIds,Object[] values) {
		//los arreglos deben ser distintos de null
		if(propertyIds == null || values == null )
			throw new RuntimeException("Los arreglos de propiedades y valores deben ser distintos de null");
		
		//el contenedor debe las propiedades
		for(String propertyId : propertyIds)
			if(!container.getContainerPropertyIds().contains(propertyId) )
				throw new RuntimeException("El contenedor no tiene la propiedad \""+propertyId+"\" dada.");
		// los largos de los arreglos deben coincidir
		if(propertyIds.length != values.length)
			throw new RuntimeException("Los arreglos de propiedades y valores deben tener el mismo largo.");
		int counts = 0;
		for(Object itemId : container.getItemIds()){
			BeanItem item = container.getItem(itemId);
			boolean isEquals = true;
			for(int i = 0 ; i < propertyIds.length ; i++){
				isEquals &= item.getItemProperty(propertyIds[i]).getValue().equals(values[i]); 
			}
			if(isEquals){
				counts++;
				if(counts > 1)
					return true;
			}
		}
		return false;
	}

	/**
	 * Función que retorna el número de sabados del mes
	 * @param date
	 * @return
	 */
	public static Integer countSat(DateTime date) {
	    return countDays(date,DateTimeConstants.SATURDAY);
	}

	/**
	 * cuenta los dias de lunes a viernes del mes dado
	 * @param date
	 * @return
	 */
	public static int countLaborerDays(DateTime date) {
	    return countDays(date,DateTimeConstants.MONDAY,DateTimeConstants.TUESDAY,DateTimeConstants.WEDNESDAY,DateTimeConstants.THURSDAY,DateTimeConstants.FRIDAY);
	}

	/**
	 * cuenta los domingos del mes de la fecha dada
	 * @param attendanceDate
	 * @return
	 */
	public static int countSun(DateTime date) {
	    return countDays(date,DateTimeConstants.SUNDAY);
	}
	
	/**
	 * Permite contar los dias de los tipos entregados como parametros de todo el mes asociado al date 
	 * @param date
	 * @param datetime
	 * @return
	 */
	private static int countDays(DateTime date,int... datetime){
		if(date == null )
			throw new RuntimeException("El valor de la fecha no puede ser nulo.");
		if(datetime == null )
			throw new RuntimeException("es necesario la lista de DateTimeConstants");
		final LocalDate start = date.withDayOfMonth(1).toLocalDate();
	    final LocalDate end = date.withDayOfMonth(date.dayOfMonth().getMaximumValue()).toLocalDate();
	    int saturdays = 0;
	    LocalDate current = start;
	    while (current.isBefore(end) || current.equals(end)) {
	    	boolean cond = false;
	    	for(int dtc : datetime ){
	    		cond |= (current.getDayOfWeek() == dtc); 
	    	}

	        if (cond)
	        	saturdays++;
	        
	        current = current.plusDays(1);
	    }
	    return saturdays;
	}
	
	public static boolean isLaborerDay(DateTime date){
		return date.getDayOfWeek() == DateTimeConstants.MONDAY ||
				date.getDayOfWeek() == DateTimeConstants.TUESDAY || 
						date.getDayOfWeek() == DateTimeConstants.WEDNESDAY ||
								date.getDayOfWeek() == DateTimeConstants.THURSDAY ||
										date.getDayOfWeek() == DateTimeConstants.FRIDAY;
	}
	
	
	/**
	 * Obtiene la movilización 2 de la empresa dada
	 * @return
	 */
	public static Double getMov2ConstructionSite(List<Mobilization2> mobilizacion2 , ConstructionSite cs){
		double mov2 = 0;
		for(Mobilization2 m2 : mobilizacion2){
			if(m2.getConstructionSite().equals(cs)){
				mov2 = m2.getAmount();
				break;
			}
		}
		return mov2;
	}
	
	/**
	 * Permite buscar el rate dependiendo de la afp dada
	 * @param afpList
	 * @param afp
	 * @return
	 */
	public static Double getAfpRate(List<AfpItem> afpList,Afp afp){
		
		for(AfpItem item : afpList)
			if(item.getAfp() == afp )
				return item.getRate()/100;
		return 0.1144;//FIX si no encuentra, retorna una por defecto?
	}

	/**
	 * Calcula el promedio de la lista
	 * @param list
	 * @return
	 */
	public static double avg(List<? extends Number> list) {
		double sum = 0;
		for(Number d : list ){
			if( d != null )
			sum += d.doubleValue();
		}
		return sum/(list.size() == 0 ? 1 : list.size());
	}

	/**
	 * 
	 * @param withdrawalSettlements
	 * @return
	 */
	public static int sum(List<WithdrawalSettlement> withdrawalSettlements) {
		if(withdrawalSettlements == null )
			return 0;
		
		int sum = 0;
		for(WithdrawalSettlement w : withdrawalSettlements)
			sum += w.getPrice();
		return sum;
	}
	
	/**
	 * 
	 * @param attendance2
	 * @return
	 */
	public static int calcularDiaFinal(Attendance attendance2, int maxDay){
		if(attendance2.getLaborerConstructionSite()
				.getActiveContract().getTerminationDate() == null )
			return maxDay;
		//obtiene el primer día del mes
		DateTime finMes = new DateTime(attendance2.getDate())
			.withDayOfMonth(new DateTime(attendance2.getDate())
			.dayOfMonth().getMaximumValue());
		//obtiene la fecha de ingreso
		DateTime finContrato = new DateTime(attendance2.getLaborerConstructionSite()
				.getActiveContract().getTerminationDate());
		//si la fecha de ingreso es luego del inicio del mes, entonces comienza a contar desde ese día
		if(finContrato.isBefore(finMes))
			return finContrato.getDayOfMonth() - 1;
		else
			return maxDay;
	}
	
	/**
	 * 
	 * @param attendance2
	 * @return
	 */
	public static int calcularDiaInicial(Attendance attendance2,int minVal) {
		//obtiene el primer día del mes
		DateTime inicioMes = new DateTime(attendance2.getDate()).withDayOfMonth(1);
		//obtiene la fecha de ingreso
		DateTime inicioContrato = new DateTime(attendance2.getLaborerConstructionSite().getActiveContract().getStartDate());
		//si la fecha de ingreso es luego del inicio del mes, entonces comienza a contar desde ese día
		if(inicioContrato.isAfter(inicioMes)){
			logger.debug("after");
			return inicioContrato.getDayOfMonth() - 1;
		}else {
			logger.debug("before");
			return minVal;
		}
	}
	
	/**
	 * Cuenta las marcas hasta el dia dada, si el dia dado es nulo, entonces cuenta todos los dias
	 * @param maxDay
	 * @param attendance
	 * @param marks
	 * @return
	 */
	public static Integer countMarks(Integer maxDay,Attendance attendance, AttendanceMark ... marks) {
		if(attendance == null )
			throw new RuntimeException("El objeto de asistencia no puede ser nulo.");

		logger.debug("maxDay  {}",maxDay);
		//si el número maximo es nulo, calcula el máximo según la fecha de la asistencia
		if(maxDay == null )
			maxDay = new DateTime(attendance.getDate()).dayOfMonth().getMaximumValue();
		
		//que considere la fecha inicial de contrato para considerar el día inical y el final
		int i = Utils.calcularDiaInicial(attendance,0);
		maxDay = Utils.calcularDiaFinal(attendance,maxDay);
		
		if( i >= maxDay )
			return 0;
		
		logger.debug("i : {} , maxDay  {}",i,maxDay);
		
		int count = 0;
		List<AttendanceMark> attendanceMarks = attendance.getMarksAsList();
		for( ; i < maxDay ; i++){
			AttendanceMark mark = attendanceMarks.get(i);
			if(ArrayUtils.contains(marks, mark))
				count++;
		}
		return count;
	}
}

