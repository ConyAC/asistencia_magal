package cl.magal.asistencia.entities.enums;

public enum LoanToolStatus {
	PAGADA(1,"Pagada"),
	EN_DEUDA(2,"En deuda"),
	APLAZADA(3,"Aplazada");

	int i;
	String description;
	
	private LoanToolStatus(int i,String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static LoanToolStatus getToolStatus(int i){
		for(LoanToolStatus e : LoanToolStatus.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("invalid Tool Status");
	}
	
	@Override
	public String toString(){
		return description;
	}
	
}
