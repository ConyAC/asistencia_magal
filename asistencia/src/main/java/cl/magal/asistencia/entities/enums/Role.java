package cl.magal.asistencia.entities.enums;

public enum Role {
	ADM_CENTRAL(1),
	ADM_CS(2);
	
	int i;
	private Role(int i) {
		this.i = i;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Role getRole(int i){
		for(Role e : Role.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Role invalid");
	}

}
