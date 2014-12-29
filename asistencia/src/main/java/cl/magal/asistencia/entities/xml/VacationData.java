package cl.magal.asistencia.entities.xml;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="VacationData")
public class VacationData {
	
	String laborerFullname;

	public String getLaborerFullname() {
		return laborerFullname;
	}

	public void setLaborerFullname(String laborerFullname) {
		this.laborerFullname = laborerFullname;
	}
	
}
