package cl.magal.asistencia.util;

import java.util.Random;

public class Utils {
	
	final static Random r = new Random();
	
	
	public static int random(){
		return random(10,100);
	}
	public static int random(int Low,int High){
		return r.nextInt(High-Low) + Low;
	}
	
}
