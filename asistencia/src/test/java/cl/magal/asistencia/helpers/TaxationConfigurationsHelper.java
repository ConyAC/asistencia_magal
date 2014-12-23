package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cl.magal.asistencia.entities.TaxationConfigurations;

public class TaxationConfigurationsHelper {

	final static Double FROM = 0D;
	final static Double TO = 541147.5D;
	final static Double FACTOR = 0D;
	final static Double REDUCTION = 0D;
	final static Double EXCEMPT = 0D;
	
	public static TaxationConfigurations newTaxationConfigurations() {
		TaxationConfigurations tax = new TaxationConfigurations();
		tax.setFrom(FROM);
		tax.setTo(TO);
		tax.setFactor(FACTOR);
		tax.setReduction(REDUCTION);
		tax.setExempt(EXCEMPT);
		return tax;
	}
	
	public static void verify(TaxationConfigurations tax){
		assertNotNull("El objeto guardado no puede ser nulo.",tax);
		assertEquals("El valor from no es el seteado.",FROM,tax.getFrom());
		assertEquals("El valor to no es el seteado.",TO,tax.getTo());
		assertEquals("El valor factor no es el seteado.",FACTOR,tax.getFactor());
		assertEquals("El valor reduction no es el seteado.",REDUCTION,tax.getReduction());
		assertEquals("El valor excempt no es el seteado.",EXCEMPT,tax.getExempt());
		
	}

}
