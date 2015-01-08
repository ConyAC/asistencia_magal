package cl.magal.asistencia.entities.validator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RutDigitValidatorTest {

	String rut  = "19976949-0";
	
	@Test
	public void testRutDigitValidator(){
		RutDigitValidator rdv = new RutDigitValidator();
		
		boolean valid = rdv.isValid(rut, null);
		
		assertTrue("El rut debe ser válido",valid);
	}
}
