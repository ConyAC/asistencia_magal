/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import cl.magal.asistencia.entities.converter.StatusConverter;
import cl.magal.asistencia.entities.enums.Status;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "team")
@NamedQueries({
    @NamedQuery(name = "Team.findAll", query = "SELECT t FROM Team t"),
    @NamedQuery(name = "Team.findByTeamId", query = "SELECT t FROM Team t WHERE t.id = :teamId"),
    @NamedQuery(name = "Team.findByName", query = "SELECT t FROM Team t WHERE t.name = :name"),
    @NamedQuery(name = "Team.findByDate", query = "SELECT t FROM Team t WHERE t.date = :date"),
    @NamedQuery(name = "Team.findByUser", query = "SELECT t FROM Team t WHERE t.leader = :leader"),
    @NamedQuery(name = "Team.findByStatusId", query = "SELECT t FROM Team t WHERE t.status = :status"),
    @NamedQuery(name = "Team.findByDeleted", query = "SELECT t FROM Team t WHERE t.deleted = :deleted")})
public class Team implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7401353696020559155L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "teamId")
    private Long id;
	
    @Basic(optional = false)
    @NotNull(message="El nombre es necesario")
    @Column(name = "name", nullable=false)
    private String name;
    
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "leaderId")
    private Laborer leader;
    
    @Convert(converter = StatusConverter.class)
    @Column(name = "status",nullable=false)
    @NotNull
    private Status status;//FIXME ocuparemos el mismo o otro enum para el estado de las cuadrillas?
    
    @Column(name = "deleted")
    private Boolean deleted;    
    
    @JoinColumn(name="constructionsiteId")
    ConstructionSite constructionSite;
   
    @JoinTable(name="laborer_constructionsite_team",
    joinColumns = { 
    		@JoinColumn(name = "teamId", referencedColumnName = "teamId")
     }, 
     inverseJoinColumns = { 	
            @JoinColumn(name = "laborer_constructionsiteId", referencedColumnName = "laborer_constructionsiteId")
     }
	)
    @ManyToMany(targetEntity=LaborerConstructionsite.class,fetch=FetchType.EAGER)
    List<LaborerConstructionsite> laborerConstructionsites = new LinkedList<LaborerConstructionsite>();
    
    @PrePersist
    public void prePersist(){
    	if(deleted == null )
    		deleted = Boolean.FALSE;
    	if(status == null)
    		status = Status.ACTIVE;
    	if(date == null )
    		date = new Date();
    }
    
    public Team() {
    }

    public Team(Long teamId) {
        this.id = teamId;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long teamId) {
		this.id = teamId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Laborer getLeader() {
		return leader;
	}

	public void setLeader(Laborer leader) {
		this.leader = leader;
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
    
   	public List<LaborerConstructionsite> getLaborerConstructionsites() {
		return laborerConstructionsites;
	}

	public void setLaborerConstructionsites(
			List<LaborerConstructionsite> laborerConstructionsites) {
		this.laborerConstructionsites = laborerConstructionsites;
	}

//	@Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (teamId != null ? teamId.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof Team)) {
//            return false;
//        }
//        Team other = (Team) object;
//        if ((this.teamId == null && other.teamId != null) || (this.teamId != null && !this.teamId.equals(other.teamId))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "jpa.magal.entities.Team[ teamId=" + id + " ]";
    }
    
    public void addLaborerConstructionsite(LaborerConstructionsite laborerconstructionsite) {
        if (!getLaborerConstructionsites().contains(laborerconstructionsite)) {
        	getLaborerConstructionsites().add(laborerconstructionsite);
        }
        if (!laborerconstructionsite.getTeams().contains(this)) {
            laborerconstructionsite.getTeams().add(this);
        }
    }

	public ConstructionSite getConstructionSite() {
		return constructionSite;
	}

	public void setConstructionSite(ConstructionSite construcionSite) {
		this.constructionSite = construcionSite;
	}
    
}
