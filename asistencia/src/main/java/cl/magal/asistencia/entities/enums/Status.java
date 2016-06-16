package cl.magal.asistencia.entities.enums;

public enum Status {
	ACTIVE(1,"Activa"),
	FINALIZED(2,"Finalizada");
	
	int i;
	String description;
	private Status(int i,String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Status getStatus(int i){
		for(Status e : Status.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("invalid Status");
	}
	
	@Override
	public String toString(){
		return description;
	}
	
}
