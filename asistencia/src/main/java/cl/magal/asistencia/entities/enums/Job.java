package cl.magal.asistencia.entities.enums;

public enum Job {

	JORNAL(1, "Jornal",1,399),
	EXCAVADOR(2, "Excavador",1,399),
	AYUDANTE_ENFIERRADOR(3,"Ayudante Enfierrador",1,399),
	CARPINTERO(4, "Carpintero",400,599),
	ENFIERRADOR(5, "Enfierrador",600,699),
	GASFITER(6, "Gasfiter",700,799),
	ALBAÑIL(7, "Albañil",800,899),
	YESERO(8, "Yesero",800,899),
	TRAZADOR(9, "Trazador",900,999),
	CAPATAZ(10, "Capataz",900,999),
	MANTENEDOR_BODEGA_CENTRAL(11,"Mant. B. Central",900,999),
	CERRAJERO(12,"Cerrajero",900,999),
	C_6(13,"C.6",900,999),
	ESTUCADOR(14, "Estucador",900,999),
	JEFE_OBRA(15, "Jefe Obra",900,999),
	PREVENCIONISTA_RIESGOS(16,"Prev. Riesgos",900,999),
	VARIOS(17,"Varios",900,999),
	BODEGUERO(18,"Bodeguero",900,999);
	
	int i,min,max;
	String description;
	
	private Job(int i, String description,int min,int max) {
		this.i = i;
		this.description = description;
		this.min = min;
		this.max = max;
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

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}
	
}