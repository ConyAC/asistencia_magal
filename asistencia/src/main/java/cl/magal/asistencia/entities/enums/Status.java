package cl.magal.asistencia.entities.enums;

public enum Status {
	ACTIVE(1),
	FINALIZED(2);
	
	int i;
	private Status(int i) {
		this.i = i;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Status getStatus(int i){
		for(Status e : Status.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Status invalid");
	}
}
