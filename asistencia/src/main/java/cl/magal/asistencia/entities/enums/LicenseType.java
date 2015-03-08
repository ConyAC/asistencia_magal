package cl.magal.asistencia.entities.enums;

public enum LicenseType {

	MEDICAL_LEAVE(1,"Licencia Médica"),
	ILLNESS(2,"Enfermedad"),
	ACCIDENT(3,"Accidente")	;
	
	int i;
	String description;
	
	private LicenseType(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static LicenseType getLicenseType(int i){
		for(LicenseType e : LicenseType.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Licencia invalida");
	}
	@Override
	public String toString(){
		return description;
	}
}
