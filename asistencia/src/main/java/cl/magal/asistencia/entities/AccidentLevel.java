package cl.magal.asistencia.entities;


public enum AccidentLevel {
	
	SERIOUS(1,"Grave"),
	NOT_SERIOUS(2,"Leve");

	int i;
	String description;
	
	private AccidentLevel(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static AccidentLevel getAccidentLevel(int i){
		for(AccidentLevel e : AccidentLevel.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Invalid accident");
	}
	@Override
	public String toString(){
		return description;
	}
}
