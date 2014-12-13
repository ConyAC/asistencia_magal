package cl.magal.asistencia.entities.enums;

public enum Isapre {

	FONASA(1,"Fonasa"),
	BANMEDICA(2,"Banmedica"),
	CRUZ_BLANCA(3,"Cruz Blanca"),
	CONSALUD(4,"Consalud")
	;
	
	int i;
	String description;
	
	private Isapre(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Isapre getIsapre(int i){
		for(Isapre e : Isapre.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Isapre invalid");
	}
	@Override
	public String toString(){
		return description;
	}
}
