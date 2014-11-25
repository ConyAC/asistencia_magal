package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
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
		obra.setDeleted(false);
		return obra;
	}

	public static void verify(ConstructionSite cs) {
		
		assertNotNull("El cs no puede ser nulo.",cs);
		assertNotNull("El id de cs no puede ser nulo.",cs.getConstructionsiteId());
		assertNotNull("El nombre no puede ser nulo.",cs.getName());
		//verificar que el tipo del estado sea del tipo enum Status
		assertTrue("El tipo de estado debe ser enum", cs.getStatus().getClass() == Status.class);
		assertTrue("El enum debe ser igual al guardado", cs.getStatus() == Status.ACTIVE);
		
	}

	public static void verify(ConstructionSite cs, ConstructionSite bdcs) {
		assertNotNull("El cs no puede ser nulo.",cs);
		assertNotNull("El bdcs no puede ser nulo.",bdcs);
		
		assertSame("El id deben ser el mismo.",cs.getConstructionsiteId(),bdcs.getConstructionsiteId());
		assertSame("El nombre debe ser el mismo.",cs.getName(),bdcs.getName());
		//verificar que el tipo del estado sea del tipo enum Status
		assertSame("El tipo de estado debe ser el mismo", cs.getStatus().getClass() , bdcs.getStatus().getClass());
		assertSame("El enum debe ser el mismo", cs.getStatus() ,bdcs.getStatus() );
	}

}

