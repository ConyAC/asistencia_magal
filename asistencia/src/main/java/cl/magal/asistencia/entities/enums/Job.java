package cl.magal.asistencia.entities.enums;

public enum Job {

	JORNAL(1),
	CARPINTERO(2),
	ENFIERRADOR(3),
	GASFITER(4),
	ALBAÑIL(5),
	ESTUCADOR(6),
	JEFE_OBRA(7);
	
	int i;
	private Job(int i) {
		this.i = i;
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

}
