package cl.magal.asistencia.entities.enums;

public enum UserStatus {
	ACTIVE(1,"Activado"),
	DESACTIVADO(2,"Desactivado");
	
	int i;
	String description;
	private UserStatus(int i,String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static UserStatus getUserStatus(int i){
		for(UserStatus e : UserStatus.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("invalid Status");
	}
	
	@Override
	public String toString(){
		return description;
	}
	
}
