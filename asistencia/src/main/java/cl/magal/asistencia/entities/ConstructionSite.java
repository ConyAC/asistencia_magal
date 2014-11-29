/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import cl.magal.asistencia.entities.converter.StatusConverter;
import cl.magal.asistencia.entities.enums.Status;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "construction_site")
@NamedQueries({
    @NamedQuery(name = "ConstructionSite.findAll", query = "SELECT c FROM ConstructionSite c"),
    @NamedQuery(name = "ConstructionSite.findByConstructionsiteId", query = "SELECT c FROM ConstructionSite c WHERE c.constructionsiteId = :constructionsiteId"),
    @NamedQuery(name = "ConstructionSite.findByAddress", query = "SELECT c FROM ConstructionSite c WHERE c.address = :address"),
    @NamedQuery(name = "ConstructionSite.findByStatus", query = "SELECT c FROM ConstructionSite c WHERE c.status = :status"),    
    @NamedQuery(name = "ConstructionSite.findByDeleted", query = "SELECT c FROM ConstructionSite c WHERE c.deleted = :deleted")})
	
public class ConstructionSite implements Serializable {
	
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "construction_siteId")
    private Long constructionsiteId;
    @Column(name = "address",nullable=true)
    private String address;
    @Column(name = "deleted")
    private Boolean deleted = Boolean.FALSE;
    
    @Column(name = "name",nullable=false)
    @NotNull
    String name;
    
    @JoinColumn(name="personInChargeId")
    @ManyToOne
    User personInCharge;
    
    @Convert(converter = StatusConverter.class)
    @Column(name = "status",nullable=false)
    @NotNull
    private Status status = Status.ACTIVE;
    
    @JoinTable(name="laborer_constructionsite",
    joinColumns = { 
    		@JoinColumn(name = "construction_siteId", referencedColumnName = "construction_siteId")
     }, 
     inverseJoinColumns = { 
            @JoinColumn(name = "laborerId", referencedColumnName = "laborerId")
     }
	)
    @ManyToMany(targetEntity=Laborer.class,fetch=FetchType.EAGER)
    List<Laborer> laborers = new LinkedList<Laborer>();
    
    /**
     * Obliga a que status sea activo, si no viene uno seteado
     */
    @PrePersist
    void preInsert() {
       if(status == null)
    	   status = Status.ACTIVE;
       if(deleted == null)
    	   deleted = Boolean.FALSE;
    }
    
    public ConstructionSite() {
    }

    public ConstructionSite(Long constructionsiteId) {
        this.constructionsiteId = constructionsiteId;
    }

    public ConstructionSite(Long constructionsiteId, String address, Status status) {
        this.constructionsiteId = constructionsiteId;
        this.address = address;
        this.status = status;
    }

    public Long getConstructionsiteId() {
        return constructionsiteId;
    }

    public void setConstructionsiteId(Long constructionsiteId) {
        this.constructionsiteId = constructionsiteId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Laborer> getLaborers() {
		if(laborers == null )
			laborers = new LinkedList<Laborer>();
		return laborers;
	}

	public void setLaborers(List<Laborer> laborers) {
		this.laborers = laborers;
	}

	public User getPersonInCharge() {
		return personInCharge;
	}

	public void setPersonInCharge(User personInCharge) {
		this.personInCharge = personInCharge;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (constructionsiteId != null ? constructionsiteId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConstructionSite)) {
            return false;
        }
        ConstructionSite other = (ConstructionSite) object;
        if ((this.constructionsiteId == null && other.constructionsiteId != null) || (this.constructionsiteId != null && !this.constructionsiteId.equals(other.constructionsiteId))) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public String toString() {
        return "jpa.magal.entities.ConstructionSite[ constructionsiteId=" + constructionsiteId + " ]";
    }

    public void addLaborer(Laborer laborer) {
        if (!getLaborers().contains(laborer)) {
        	getLaborers().add(laborer);
        }
        if (!laborer.getConstructionSites().contains(this)) {
            laborer.getConstructionSites().add(this);
        }
    }
    
}
