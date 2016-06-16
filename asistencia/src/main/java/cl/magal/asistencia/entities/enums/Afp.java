package cl.magal.asistencia.entities.enums;

public enum Afp {

	MODELO(1, "Modelo"),
	HABITAT(2, "Habitat"),
	CUPRUM(3, "Cuprum"),
	CAPITAL(4, "Capital"),
	PROVIDA(5,"Provida"),
	PLANVITAL(6,"Plan Vital");
	
	int i;
	String description;
	
	private Afp(int i, String description) {
		this.i = i;
		this.description = description;
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
	
	public String getDescription(){
		return description;
	}
	
	@Override
	public String toString(){
		return description;
	}
}
