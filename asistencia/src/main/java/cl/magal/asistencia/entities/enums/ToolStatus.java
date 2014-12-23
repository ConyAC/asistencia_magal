package cl.magal.asistencia.entities.enums;

public enum ToolStatus {
	OPERATIONAL(1,"Operativa"),
	INOPERATIVE(2,"No Operativa"),
	IN_WORKSHOP(3,"En Taller");

	int i;
	String description;
	private ToolStatus(int i,String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static ToolStatus getToolStatus(int i){
		for(ToolStatus e : ToolStatus.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("invalid Tool Status");
	}
	
	@Override
	public String toString(){
		return description;
	}
	
}
