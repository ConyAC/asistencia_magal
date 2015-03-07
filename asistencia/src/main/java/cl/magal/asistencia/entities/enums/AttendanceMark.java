package cl.magal.asistencia.entities.enums;

public enum AttendanceMark {
	
	ATTEND(1,"X","Asistió"),
	ACCIDENT(2,"A","Accidente"),
	PERMISSION(3,"P","Permission"),
	SUNDAY(4,"D","Sunday"),
	SATURDAY(5,"S","Saturday"),
	RAIN(6,"LL","Lluvia"),
	SICK(7,"E","Enfermo"),
	FAIL(8,"F","Falla"),
	FILLER(9,"R","Relleno");
	
	Integer code;
	String description;
	String title;
	private AttendanceMark(Integer code, String description,String title){
		this.code = code;
		this.description = description;
		this.title = title;
	}
	
	public int getCorrelative(){
		return code;
	}
	
	public static AttendanceMark getAttendanceMark(int i){
		for(AttendanceMark e : AttendanceMark.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("invalid Attendance Mark");
	}
	
	@Override
	public String toString(){
		return description;
	}

}
