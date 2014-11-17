package cl.magal.asistencia.entities.enums;

public enum Afp {

	MODELO(1),
	HABITAT(2),
	CUPRUM(3),
	CAPITAL(4);
	
	int i;
	private Afp(int i) {
		this.i = i;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Afp getAfp(int i){
		for(Afp e : Afp.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Afp invalid");
	}

}
