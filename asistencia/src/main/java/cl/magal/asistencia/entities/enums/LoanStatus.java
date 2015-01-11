package cl.magal.asistencia.entities.enums;

public enum LoanStatus {
	ACTIVE(1,"Activa"),
	INACTIVE(2,"Inactiva");
	
	int i;
	String description;
	
	private LoanStatus(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static LoanStatus getLoanStatus(int i){
		for(LoanStatus e : LoanStatus.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("invalid Loan Status");
	}
	
	@Override
	public String toString(){
		return description;
	}
	
}
