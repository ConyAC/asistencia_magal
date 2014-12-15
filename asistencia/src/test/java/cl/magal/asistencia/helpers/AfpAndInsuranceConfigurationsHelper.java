package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cl.magal.asistencia.entities.AfpAndInsuranceConfigurations;

public final class AfpAndInsuranceConfigurationsHelper {
	
	final static Double SIS = 1.26;

	public static AfpAndInsuranceConfigurations newAfpAndInsuranceConfigurations() {
		AfpAndInsuranceConfigurations config = new AfpAndInsuranceConfigurations();
		config.setSis(SIS);
		return config;
	}
	
	public static void verify(AfpAndInsuranceConfigurations conf){
		assertNotNull("El elemento guardado no puede ser null",conf);
		assertEquals("El porcentaje de SIS no es el definido",SIS,conf.getSis());
	}

}
