package cl.magal.asistencia.util;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.Laborer;

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
			logger.debug(" error en validar mensaje : {} valor: {} ",constraintViolation.getMessage(),constraintViolation.getInvalidValue());
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
	
}
