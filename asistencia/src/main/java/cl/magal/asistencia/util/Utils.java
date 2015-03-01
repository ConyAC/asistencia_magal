package cl.magal.asistencia.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.Laborer;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

public class Utils {
	
	static Logger logger = LoggerFactory.getLogger(Utils.class);
	
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
			if(laborer.getLaborerId() == long1 ){
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
	
}
