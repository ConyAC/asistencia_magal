package cl.magal.asistencia.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Contract {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long contractId;
	
	String name;	
	Date timeDuration;
	Date startDate;
	Date terminationDate;
	long valueTreatment;
	
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getTimeDuration() {
		return timeDuration;
	}
	public void setTimeDuration(Date timeDuration) {
		this.timeDuration = timeDuration;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getTerminationDate() {
		return terminationDate;
	}
	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}
	public long getValueTreatment() {
		return valueTreatment;
	}
	public void setValueTreatment(long valueTreatment) {
		this.valueTreatment = valueTreatment;
	}
	
	@Override
	public String toString() {
		return "Contract [contractId=" + contractId + ", name=" + name
				+ ", timeDuration=" + timeDuration + ", startDate=" + startDate
				+ ", terminationDate=" + terminationDate + ", valueTreatment="
				+ valueTreatment + "]";
	}	
}
