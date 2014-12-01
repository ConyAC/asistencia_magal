package cl.magal.asistencia.entities.enums;

public enum Job {

	JORNAL(1, "Jornal"),
	CARPINTERO(2, "Carpintero"),
	ENFIERRADOR(3, "Enfierrador"),
	GASFITER(4, "Gasfiter"),
	ALBAÑIL(5, "Albañil"),
	ESTUCADOR(6, "Estucador"),
	JEFE_OBRA(7, "Jefe Obra");
	
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
}
