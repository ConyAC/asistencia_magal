package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;

public class FamilyAllowanceConfigurationsHelper {

	final static Double FROM = 0D;
	final static Double TO = 541147.5D;
	final static Double AMOUNT = 230222D;
	
	public static FamilyAllowanceConfigurations newFamilyAllowanceConfigurations() {
		FamilyAllowanceConfigurations f = new FamilyAllowanceConfigurations();
		f.setFrom(FROM);
		f.setTo(TO);
		f.setAmount(AMOUNT);
		return f;
	
	}
	
	public static void verify(FamilyAllowanceConfigurations tax){
		assertNotNull("El objeto guardado no puede ser nulo.",tax);
		assertEquals("El valor from no es el seteado.",FROM,tax.getFrom());
		assertEquals("El valor to no es el seteado.",TO,tax.getTo());
		assertEquals("El valor factor no es el seteado.",AMOUNT,tax.getAmount());
		
	}

}
