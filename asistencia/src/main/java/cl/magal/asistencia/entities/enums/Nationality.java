package cl.magal.asistencia.entities.enums;

public enum Nationality {

	CHILENA(1,"Chilena"),
	PERUANA(2,"Peruana"),
	BOLIVIANA(3,"Boliviana");
	
	int i;
	String description;
	
	private Nationality(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Nationality getNationality(int i){
		for(Nationality e : Nationality.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Nationality invalid");
	}

	@Override
	public String toString(){
		return description;
	}	
}
