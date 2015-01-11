package cl.magal.asistencia.entities.enums;

public enum ToolStatus {
	PAGADA(1,"Pagada"),
	EN_DEUDA(2,"En deuda");

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
