package cl.magal.asistencia.entities.enums;

public enum Bank {

	ESTADO(1,"Banco Estado"),
	BBVA(2,"BBVA"),
	SANTANDER(3,"Santander");
	
	int i;
	String description;
	
	private Bank(int i, String description) {
		this.i = i;
		this.description = description;
	}
	
	public int getCorrelative(){
		return i;
	}
	
	public static Bank getBank(int i){
		for(Bank e : Bank.values())
			if(e.getCorrelative() == i )
				return e;
		throw new RuntimeException("Bank invalid");
	}

	@Override
	public String toString(){
		return description;
	}	
}
