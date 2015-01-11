package cl.magal.asistencia.entities.enums;

public enum Job {

	JORNAL(1, "Jornal"),
	EXCAVADOR(2, "Excavador"),
	AYUDANTE_ENFIERRADOR(3,"Ayudante Enfierrador"),
	CARPINTERO(4, "Carpintero"),
	ENFIERRADOR(5, "Enfierrador"),
	GASFITER(6, "Gasfiter"),
	ALBAÑIL(7, "Albañil"),
	YESERO(8, "Yesero"),
	TRAZADOR(9, "Trazador"),
	CAPATAZ(10, "Capataz"),
	MANTENEDOR_BODEGA_CENTRAL(11,"Mant. B. Central"),
	CERRAJERO(12,"Cerrajero"),
	C_6(13,"C.6"),
	ESTUCADOR(14, "Estucador"),
	JEFE_OBRA(15, "Jefe Obra"),
	PREVENCIONISTA_RIESGOS(16,"Prev. Riesgos"),
	VARIOS(17,"Varios"),
	BODEGUERO(18,"Bodeguero");
	
	int i;
	String description;
	
	private Job(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Job getJob(int i){
		for(Job e : Job.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Job invalid");
	}

	@Override
	public String toString(){
		return description;
	}

	public static Job getJob(String value) {
		for(Job e : Job.values())
			if(e.description.compareToIgnoreCase(value) == 0 )
				return e;
		throw new RuntimeException("Job invalid");
	}	
}
