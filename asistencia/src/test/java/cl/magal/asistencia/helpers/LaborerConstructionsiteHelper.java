package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;

public final class LaborerConstructionsiteHelper {
	
	public static LaborerConstructionsite newLaborerConstructionsite(){
		
		Laborer laborer = LaborerHelper.newLaborer();
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		
		LaborerConstructionsite lc = new LaborerConstructionsite();
		lc.setLaborer(laborer);
		lc.setConstructionsite(cs);
		
		return lc;
	}

	public static void verify(LaborerConstructionsite dbu) {
		assertNotNull(dbu);
	}

	public static void verify(LaborerConstructionsite arg1, LaborerConstructionsite arg2) {
		assertNotNull("El l no puede ser nulo.",arg1);
		assertNotNull("El bdl no puede ser nulo.",arg2);		
	}

}
