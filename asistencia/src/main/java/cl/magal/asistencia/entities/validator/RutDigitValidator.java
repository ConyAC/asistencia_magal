package cl.magal.asistencia.entities.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RutDigitValidator implements ConstraintValidator<RutDigit, String> {

	@Override
	public void initialize(RutDigit constraintAnnotation) {
		
	}

	@Override
	public boolean isValid(String rut, ConstraintValidatorContext context) {
		if(rut == null )
			return false;
		String[] rutTemp = rut.split("-");
		if( rutTemp == null || rutTemp.length != 2 )
			return false;
		
		if (rutTemp[1].compareToIgnoreCase(Digito(Integer.valueOf(rutTemp[0]))) == 0 ) {
			return true;
		}
		return false;
	}
	
	public String Digito(int rut) {
		int suma = 0;
		int multiplicador = 1;
		while (rut != 0) {
		multiplicador++;
		if (multiplicador == 8)
		multiplicador = 2;
		suma += (rut % 10) * multiplicador;
		rut = rut / 10;
		}
		suma = 11 - (suma % 11);
		if (suma == 11)	{
		return "0";
		} else if (suma == 10) {
		return "K";
		} else {
		return suma+"";
		}
   }

    
}
