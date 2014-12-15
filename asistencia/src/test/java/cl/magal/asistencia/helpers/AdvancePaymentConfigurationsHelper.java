package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cl.magal.asistencia.entities.AdvancePaymentConfigurations;

public final class AdvancePaymentConfigurationsHelper {
	
	final static Double PERMISSION_DISCOUNT = 200000D;
	final static Double FAILURE_DISCOUNT = 135000D;

	public static AdvancePaymentConfigurations newAdvancePaymentConfigurations() {
		AdvancePaymentConfigurations config = new AdvancePaymentConfigurations();
		config.setPermissionDiscount(PERMISSION_DISCOUNT);
		config.setFailureDiscount(FAILURE_DISCOUNT);
		return config;
	}
	
	public static void verify(AdvancePaymentConfigurations conf){
		assertNotNull("El elemento guardado no puede ser null",conf);
		assertEquals("El descuento por permiso no es el seteado",PERMISSION_DISCOUNT,conf.getPermissionDiscount());
		assertEquals("El descuento por falla no es el seteado",FAILURE_DISCOUNT,conf.getFailureDiscount());
	}

}
