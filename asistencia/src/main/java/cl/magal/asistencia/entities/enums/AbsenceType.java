package cl.magal.asistencia.entities.enums;

public enum AbsenceType {

	MEDICAL_LEAVE(1,"Licencia Médica"),
	ILLNESS(2,"Enfermedad"),
	ACCIDENT(3,"Accidente")	;
	
	int i;
	String description;
	
	private AbsenceType(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static AbsenceType getAbsencesType(int i){
		for(AbsenceType e : AbsenceType.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Ausencia invalid");
	}
	@Override
	public String toString(){
		return description;
	}
}
