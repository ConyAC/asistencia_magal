package cl.magal.asistencia.entities.validator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class AgeMaxValidator implements ConstraintValidator<AgeMax, Date> {

	@Override
	public void initialize(AgeMax constraintAnnotation) {
		
	}

	@Override
	public boolean isValid(Date value, ConstraintValidatorContext context) {
		if(value == null)
			return false;
		if(age(value) < 20)
			return false;
		else
			return true;
	}

	public int age(Date date) {
		Calendar birth = new GregorianCalendar();
        Calendar today = new GregorianCalendar();

        Date currentDate = new Date();
        birth.setTime(date);
        today.setTime(currentDate);
     
        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);     
        boolean isMonthGreater = birth.get(Calendar.MONTH) > today.get(Calendar.MONTH);     
        boolean isMonthSameButDayGreater = birth.get(Calendar.MONTH) == today.get(Calendar.MONTH) && birth.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH);     
        if (isMonthGreater || isMonthSameButDayGreater)
            age = age-1;
        
        return age;
   }    
}
