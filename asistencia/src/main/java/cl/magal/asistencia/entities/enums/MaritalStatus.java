package cl.magal.asistencia.entities.enums;

public enum MaritalStatus {

	SOLTERO(1),
	CASADO(2),
	VIUDO(3),
	SEPARADO(4);
	
	int i;
	private MaritalStatus(int i) {
		this.i = i;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static MaritalStatus getMaritalStatus(int i){
		for(MaritalStatus e : MaritalStatus.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("MaritalStatus invalid");
	}

}
