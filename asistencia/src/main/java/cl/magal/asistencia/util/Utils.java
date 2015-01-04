package cl.magal.asistencia.util;

import java.util.Random;
import java.util.Set;

import javax.validation.ConstraintViolation;

public class Utils {
	
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
		}
		return sb.toString();
	}
	
}
