/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cl.magal.asistencia.entities.converter.JobConverter;
import cl.magal.asistencia.entities.enums.Job;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "contract")
@NamedQueries({
    @NamedQuery(name = "Contract.findAll", query = "SELECT c FROM Contract c"),
    @NamedQuery(name = "Contract.findByContractId", query = "SELECT c FROM Contract c WHERE c.contractId = :contractId"),
    @NamedQuery(name = "Contract.findByName", query = "SELECT c FROM Contract c WHERE c.name = :name"),
    @NamedQuery(name = "Contract.findByTimeduration", query = "SELECT c FROM Contract c WHERE c.timeduration = :timeduration"),
    @NamedQuery(name = "Contract.findByStartDate", query = "SELECT c FROM Contract c WHERE c.startDate = :startDate"),
    @NamedQuery(name = "Contract.findByTerminationDate", query = "SELECT c FROM Contract c WHERE c.terminationDate = :terminationDate"),
    @NamedQuery(name = "Contract.findByValueTreatment", query = "SELECT c FROM Contract c WHERE c.valueTreatment = :valueTreatment")})
public class Contract implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8847261508253427546L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "contractId")
    private Integer contractId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "timeduration")
    private int timeduration;
    @Basic(optional = false)
    @Column(name = "startDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Basic(optional = false)
    @Column(name = "terminationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date terminationDate;
    @Basic(optional = false)
    @Column(name = "valueTreatment")
    private int valueTreatment;
    @Column(name = "step")
    private String step;
    @Column(name = "settlement")
    private Integer settlement;
    @Column(name = "contractDescription")
    String contractDescription;
    @Convert(converter = JobConverter.class)
    @Column(name = "job")
    private Job job;
    
    @Column(name="jobCode")
    private Integer jobCode;
    
    private Boolean active;
    
    @ManyToOne
    @JoinColumn(name="LABORER_CONSTRUCTIONSITEID")
	LaborerConstructionsite laborerConstructionSite;
    
    @OneToMany(mappedBy="contract",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<Annexed> annexeds = new ArrayList<Annexed>();

    public Contract() {
    }

    public Contract(Integer contractId) {
        this.contractId = contractId;
    }

    public Contract(Integer contractId, String name, int timeduration, Date startDate, Date terminationDate, int valueTreatment) {
        this.contractId = contractId;
        this.name = name;
        this.timeduration = timeduration;
        this.startDate = startDate;
        this.terminationDate = terminationDate;
        this.valueTreatment = valueTreatment;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimeduration() {
        return timeduration;
    }

    public void setTimeduration(int timeduration) {
        this.timeduration = timeduration;
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

    public int getValueTreatment() {
        return valueTreatment;
    }

    public void setValueTreatment(int valueTreatment) {
        this.valueTreatment = valueTreatment;
    }
    public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public LaborerConstructionsite getLaborerConstructionSite() {
		return laborerConstructionSite;
	}

	public void setLaborerConstructionSite(
			LaborerConstructionsite laborerConstructionSite) {
		this.laborerConstructionSite = laborerConstructionSite;
	}

	public List<Annexed> getAnnexeds() {
		return annexeds;
	}

	public void setAnnexeds(List<Annexed> annexeds) {
		this.annexeds = annexeds;
	}
	
	public void addAnnexed(Annexed annexed){
		 if (!getAnnexeds().contains(annexed)) {
			 getAnnexeds().add(annexed);
	     	annexed.setContract(this);
	     }
	}

	public Integer getSettlement() {
		return settlement;
	}

	public void setSettlement(Integer settlement) {
		this.settlement = settlement;
		setActive(false);
	}
	
	public String getContractDescription() {
		return contractDescription;
	}

	public void setContractDescription(String contractDescription) {
		this.contractDescription = contractDescription;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Integer getJobCode() {
		return jobCode;
	}

	public void setJobCode(Integer jobCode) {
		this.jobCode = jobCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String getJobAndCode(){
		return (getJob() != null ? getJob().toString():"")+" ("+getJobCode()+")";
	}
	
	@Override
    public String toString() {
        return "jpa.magal.entities.Contract[ contractId=" + contractId + " ]";
    }

	public String dump() {
		return "Contract [contractId=" + contractId + ", name=" + name
				+ ", timeduration=" + timeduration + ", startDate=" + startDate
				+ ", terminationDate=" + terminationDate + ", valueTreatment="
				+ valueTreatment + ", step=" + step + ", settlement="
				+ settlement + ", contractDescription=" + contractDescription
				+ ", job=" + job + ", jobCode=" + jobCode + ", active="
				+ active + ", laborerConstructionSite="
				+ laborerConstructionSite + ", annexeds=" + annexeds + "]";
	}

	
    
}
