package cl.magal.asistencia.services;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.util.Utils;

public final class ConstructionSiteHelper {

	
	private static final String NAME = "Obra";
	private static final String ADDRESS = "Dirección Obra";
	
	public static ConstructionSite newConstrutionSite() {
		
		ConstructionSite obra = new ConstructionSite();
		obra.setName(NAME+Utils.random());
		obra.setStatus(Status.ACTIVE);
		obra.setAddress(ADDRESS+Utils.random());
		return obra;
	}

}

