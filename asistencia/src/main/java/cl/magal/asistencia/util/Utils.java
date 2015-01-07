package cl.magal.asistencia.util;

import java.util.Random;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
}
