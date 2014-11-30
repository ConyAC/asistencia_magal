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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cl.magal.asistencia.entities.converter.AfpConverter;
import cl.magal.asistencia.entities.converter.JobConverter;
import cl.magal.asistencia.entities.converter.MaritalStatusConverter;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.MaritalStatus;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "laborer")
@NamedQueries({
    @NamedQuery(name = "Laborer.findAll", query = "SELECT l FROM Laborer l"),
    @NamedQuery(name = "Laborer.findByLaborerId", query = "SELECT l FROM Laborer l WHERE l.laborerId = :laborerId"),
    @NamedQuery(name = "Laborer.findByFirstname", query = "SELECT l FROM Laborer l WHERE l.firstname = :firstname"),
    @NamedQuery(name = "Laborer.findBySecondname", query = "SELECT l FROM Laborer l WHERE l.secondname = :secondname"),
    @NamedQuery(name = "Laborer.findByLastname", query = "SELECT l FROM Laborer l WHERE l.lastname = :lastname"),
    @NamedQuery(name = "Laborer.findBySecondlastname", query = "SELECT l FROM Laborer l WHERE l.secondlastname = :secondlastname"),
    @NamedQuery(name = "Laborer.findByRut", query = "SELECT l FROM Laborer l WHERE l.rut = :rut"),
    @NamedQuery(name = "Laborer.findByDateBirth", query = "SELECT l FROM Laborer l WHERE l.dateBirth = :dateBirth"),
    @NamedQuery(name = "Laborer.findByMaritalStatusId", query = "SELECT l FROM Laborer l WHERE l.maritalStatus = :maritalStatus"),
    @NamedQuery(name = "Laborer.findByAddress", query = "SELECT l FROM Laborer l WHERE l.address = :address"),
    @NamedQuery(name = "Laborer.findByMobileNumber", query = "SELECT l FROM Laborer l WHERE l.mobileNumber = :mobileNumber"),
    @NamedQuery(name = "Laborer.findByPhone", query = "SELECT l FROM Laborer l WHERE l.phone = :phone"),
    @NamedQuery(name = "Laborer.findByDateAdmission", query = "SELECT l FROM Laborer l WHERE l.dateAdmission = :dateAdmission"),
    @NamedQuery(name = "Laborer.findByContractId", query = "SELECT l FROM Laborer l WHERE l.contractId = :contractId"),
    @NamedQuery(name = "Laborer.findByJobId", query = "SELECT l FROM Laborer l WHERE l.job = :job"),
    @NamedQuery(name = "Laborer.findByAfpId", query = "SELECT l FROM Laborer l WHERE l.afp = :afp"),
    @NamedQuery(name = "Laborer.findByTeamId", query = "SELECT l FROM Laborer l WHERE l.teamId = :teamId")})
public class Laborer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "laborerId")
    private Long laborerId;
    @Basic(optional = false)
    @Column(name = "firstname", nullable=false)
    private String firstname;
    @Column(name = "secondname")
    private String secondname;
    @Column(name = "lastname", nullable=false)
    private String lastname;
    @Column(name = "secondlastname")
    private String secondlastname;
    @Column(name = "rut", nullable=false)
    private String rut;
    @Column(name = "dateBirth")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateBirth;
    @Column(name = "address")
    private String address;
    @Column(name = "mobileNumber")
    private String mobileNumber;
    @Column(name = "phone")
    private String phone;
    @Column(name = "dateAdmission")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdmission;
    @Column(name = "contractId")
    private Integer contractId;
    @Column(name = "teamId")
    private Integer teamId;

    @Convert(converter = JobConverter.class)
    private Job job;
    
    @Convert(converter = AfpConverter.class)
    private Afp afp;
    
    @Convert(converter = MaritalStatusConverter.class)
    private MaritalStatus maritalStatus;
    
    @ManyToMany(mappedBy="laborers",cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    List<ConstructionSite> constructionSites;
    
    @PrePersist
    public void prePersist(){
    	if(firstname == null)
    		firstname = "Nuevo trabajador";
    	if(afp == null)
    		afp = Afp.MODELO;
    	if(maritalStatus == null )
    		maritalStatus = MaritalStatus.SOLTERO;
    	if(job == null)
    		job = Job.JORNAL;
		
    }
    
    public Laborer() {
    	constructionSites = new ArrayList<ConstructionSite>();
    }

    public Laborer(Long laborerId) {
        this.laborerId = laborerId;
    }

    public Laborer(Long laborerId, String firstname) {
        this.laborerId = laborerId;
        this.firstname = firstname;
    }

    public Long getLaborerId() {
        return laborerId;
    }

    public void setLaborerId(Long laborerId) {
        this.laborerId = laborerId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSecondlastname() {
        return secondlastname;
    }

    public void setSecondlastname(String secondlastname) {
        this.secondlastname = secondlastname;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public Date getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(Date dateBirth) {
        this.dateBirth = dateBirth;
    }

	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDateAdmission() {
        return dateAdmission;
    }

    public void setDateAdmission(Date dateAdmission) {
        this.dateAdmission = dateAdmission;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

    public Afp getAfp() {
		return afp;
	}

	public void setAfp(Afp afp) {
		this.afp = afp;
	}

	public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
    
    public List<ConstructionSite> getConstructionSites() {
		return constructionSites;
	}

	public void setConstructionSites(List<ConstructionSite> constructionSites) {
		this.constructionSites = constructionSites;
	}

	public void addConstructionSite(ConstructionSite constructionSite) {
        if (!getConstructionSites().contains(constructionSite)) {
        	getConstructionSites().add(constructionSite);
        }
        if (!constructionSite.getLaborers().contains(this)) {
        	constructionSite.getLaborers().add(this);
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (laborerId != null ? laborerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Laborer)) {
            return false;
        }
        Laborer other = (Laborer) object;
        if ((this.laborerId == null && other.laborerId != null) || (this.laborerId != null && !this.laborerId.equals(other.laborerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.magal.entities.Laborer[ laborerId=" + laborerId + " ]";
    }
    
}
