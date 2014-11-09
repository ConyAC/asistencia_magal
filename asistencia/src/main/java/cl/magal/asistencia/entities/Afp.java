package cl.magal.asistencia.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Afp {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long jobId;
	
	String name;

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Job [jobId=" + jobId + ", name=" + name + "]";
	}
}
