package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cl.magal.asistencia.entities.WageConfigurations;

public final class WageConfigurationsHelper {
	
	final static Double MINIMUM_WAGE = 200000D;
	final static Double MOBILIZATION = 135000D;
	final static Double COLLATION = 20000D;

	public static WageConfigurations newWageConfigurations() {
		WageConfigurations config = new WageConfigurations();
		config.setMinimumWage(MINIMUM_WAGE);
		config.setMobilization(MOBILIZATION);
		config.setCollation(COLLATION);
		return config;
	}
	
	public static void verify(WageConfigurations conf){
		assertNotNull("El elemento guardado no puede ser null",conf);
		assertNotNull("El elemento guardado no puede tener id null",conf.getWageConfigurationsId());
		assertEquals("El sueldo minimo no es el seteado",MINIMUM_WAGE,conf.getMinimumWage());
		assertEquals("El mobilizacion no es el seteado",MOBILIZATION,conf.getMobilization());
		assertEquals("La colación no es el seteado",COLLATION,conf.getCollation());
	}

}
