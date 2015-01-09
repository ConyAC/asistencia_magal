package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	
}
