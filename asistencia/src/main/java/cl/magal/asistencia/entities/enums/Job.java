package cl.magal.asistencia.entities.enums;

public enum Job {

	EXCAVADOR(2, "Excavador",1,399),
	CONCRETERO(19,"Concretero",1,399),
	JORNAL(1, "Jornalero",1,399),
	PORTERO(20, "Portero",1,399),
	LLAVERO(21, "Llavero",1,399),
	RONDIN(22, "Rondin",1,399),
	ASEADOR(23, "Aseador",1,399),
	ENFIERRADOR(5, "Enfierrador",600,699),
	CARPINTERO(4, "Carpintero",400,599),
	ALBAÑIL(7, "Albañil",800,899),
	ESTUCADOR(14, "Estucador",900,999),
	YESERO(8, "Yesero",800,899),
	GENERAL(24,"General",900,999),
	/**
	 * @deprecated no es uno de los oficinales, cambiar los 3 por 1
	 */
//	AYUDANTE_ENFIERRADOR(3,"Ayudante Enfierrador",1,399),	
	/**
	 * @deprecated no es uno de los oficinales, cambiar por general?
	 */
//	GASFITER(6, "Gasfiter",700,799),
	/**
	 * @deprecated no es uno de los oficinales, cambiar por general?
	 */
//	TRAZADOR(9, "Trazador",900,999),
	/**
	 * @deprecated no es uno de los oficinales, cambiar por general?
	 */
//	CAPATAZ(10, "Capataz",900,999),
	/**
	 * @deprecated no es uno de los oficinales, cambiar por general?
	 */
//	MANTENEDOR_BODEGA_CENTRAL(11,"Mant. B. Central",900,999),
	/**
	 * @deprecated no es uno de los oficinales, cambiar por general?
	 */
//	CERRAJERO(12,"Cerrajero",900,999),
	/**
	 * @deprecated no es uno de los oficinales, cambiar por general?
	 */
//	C_6(13,"C.6",900,999),
	/**
	 * @deprecated no es uno de los oficinales, cambiar por general?
	 */
//	JEFE_OBRA(15, "Jefe Obra",900,999),
	/**
	 * @deprecated no es uno de los oficinales, cambiar por general?
	 */
//	PREVENCIONISTA_RIESGOS(16,"Prev. Riesgos",900,999),
	/**
	 * @deprecated no es uno de los oficinales, cambiar por general?
	 */
//	VARIOS(17,"Varios",900,999),
	/**
	 * @deprecated no es uno de los oficinales, cambiar por general?
	 */
//	BODEGUERO(18,"Bodeguero",900,999)
	;
	
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