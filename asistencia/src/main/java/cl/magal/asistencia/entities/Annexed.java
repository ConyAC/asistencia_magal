package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cl.magal.asistencia.entities.enums.Job;

@Entity
@Table(name="annexed")
public class Annexed implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6711988992116421430L;
	
	@Id
	@Column(name="ANNEXEDID")
	Long annexedId;
	
	@Basic(optional = false)
    @Column(name = "startDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Basic(optional = false)
    @Column(name = "terminationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date terminationDate;
	
	String step;
	
	String annexedDescription;
	
	@ManyToOne
	@JoinColumn(name="CONTRACTID")
	Contract contract;

	public Long getAnnexedId() {
		return annexedId;
	}

	public void setAnnexedId(Long annexedId) {
		this.annexedId = annexedId;
	}

	public String getAnnexedDescription() {
		return annexedDescription;
	}

	public void setAnnexedDescription(String annexedDescription) {
		this.annexedDescription = annexedDescription;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
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

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}
	
	
}
