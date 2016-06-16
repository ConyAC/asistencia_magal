package cl.magal.asistencia.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Date;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.util.Utils;

public final class TeamHelper {
	
	final static private String NOMBRE = "Cuadrilla 1";
	
	public static Team newTeam(){
		Team team = new Team();
		team.setName(NOMBRE);
		Laborer leader = LaborerHelper.newLaborer();
		team.setLeader(leader);
		team.setDate(new Date());
		team.setStatus(Status.ACTIVE);
		return team;
	}

	public static void verify(Team team) {
		
		assertNotNull("La cuadrilla no puede ser nulo", team);
		assertNotNull("El id de la cuadrilla no puede ser nulo", team.getId());
		assertNotNull("El nombre de la cuadrilla no puede ser nulo", team.getName());
	}
	
	public static void verify(Team t, Team bdt) {
		assertNotNull("El t no puede ser nulo.", t);
		assertNotNull("El bdt no puede ser nulo.", bdt);
		
		assertSame("El id debe ser el mismo.", t.getId(), bdt.getId());
		assertEquals("El nombre debe ser el mismo.", t.getName(), bdt.getName());
		
		//verificar tipo enum 	
		assertSame("El estado debe ser enum", t.getStatus().getClass() , bdt.getStatus().getClass());
		assertSame("El enum debe ser igual al guardado", t.getStatus(), bdt.getStatus());

	}
}
