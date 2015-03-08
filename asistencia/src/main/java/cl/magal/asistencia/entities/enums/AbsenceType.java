package cl.magal.asistencia.entities.enums;

public enum AbsenceType {
	
	LICENCIA(1,"Licencia"),
	ACCIDENTE(2,"Accidente"),
	VACACION(3,"Vacación");
	
	int code;
	String description;
	
	private AbsenceType(int code, String description ){
		this.code = code;
		this.description = description;
	}
	
	@Override
	public String toString(){
		return description;
	}

}
