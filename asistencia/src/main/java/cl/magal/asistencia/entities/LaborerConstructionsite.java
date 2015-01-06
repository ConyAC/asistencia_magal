/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;

import cl.magal.asistencia.entities.converter.JobConverter;
import cl.magal.asistencia.entities.enums.Job;

/**
 * Clase que permite encapsular toda la información de un trabajador asociada a una obra
 * @author Constanza
 */
@Entity
@Table(name = "laborer_constructionsite")
@NamedQueries({
    @NamedQuery(name = "LaborerConstructionsite.findAll", query = "SELECT l FROM LaborerConstructionsite l"),
    @NamedQuery(name = "LaborerConstructionsite.findByLaborerId", query = "SELECT l FROM LaborerConstructionsite l WHERE l.laborer = :laborer"),
    @NamedQuery(name = "LaborerConstructionsite.findByConstructionsiteId", query = "SELECT l FROM LaborerConstructionsite l WHERE l.constructionsite = :constructionsiteId"),
    @NamedQuery(name = "LaborerConstructionsite.findByActive", query = "SELECT l FROM LaborerConstructionsite l WHERE l.active = :active")})
public class LaborerConstructionsite implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1246171188855570288L;
	
	@Id
    @Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="LABORER_CONSTRUCTIONSITEID")
	Long id;
	
    @ManyToOne
    @JoinColumn(name = "laborerId")
    private Laborer laborer;
    
    @ManyToOne
    @JoinColumn(name = "CONSTRUCTION_SITEID",updatable=false,nullable=false)
    private ConstructionSite constructionsite;
    
    @Column(name = "active")
    private Short active;
    
    @Convert(converter = JobConverter.class)
    @Column(name = "job")
    private Job job;
    
    @Column(name="jobCode")
    private Integer jobCode;
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,orphanRemoval=true )
    List<Vacation> vacations = new ArrayList<Vacation>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,orphanRemoval=true )
    List<Absence> absences = new ArrayList<Absence>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,orphanRemoval=true )
    List<Accident> accidents = new ArrayList<Accident>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,orphanRemoval=true )
    List<Tool> tool = new ArrayList<Tool>();
    
//    @ManyToMany(mappedBy="laborers")
//    List<Team> teams = new ArrayList<Team>();

    
    @PrePersist
    public void prePersist(){
    	if(job == null)
    		job = Job.JORNAL;
    }
    
    public LaborerConstructionsite() {
    }


//    public List<Team> getTeams() {
//		return teams;
//	}
//
//	public void setTeams(List<Team> teams) {
//		this.teams = teams;
//	}

	public Integer getJobCode() {
		return jobCode;
	}

	public void setJobCode(Integer jobCode) {
		this.jobCode = jobCode;
	}
	
	public List<Vacation> getVacations() {
		return vacations;
	}

	public void setVacations(List<Vacation> vacations) {
		this.vacations = vacations;
	}
	
	public void addVacation(Vacation vacation) {
        if (!getVacations().contains(vacation)) {
        	getVacations().add(vacation);
        	vacation.setLaborerConstructionSite(this);
        }
    }
	
	public void removeVacation(Vacation vacation) {
        if (getVacations().contains(vacation)) {
        	getVacations().remove(vacation);
        	vacation.setLaborerConstructionSite(null);
        }
    }
    	
	public List<Tool> getTool() {
		return tool;
	}

	public void setTool(List<Tool> tool) {
		this.tool = tool;
	}
	
//	public void addTeam(Team team) {
//        if (!getTeams().contains(team)) {
//        	getTeams().add(team);
//        }
//        if (!team.getLaborers().contains(this)) {
//        	team.getLaborers().add(this);
//        }
//    }
    
    public List<Absence> getAbsences() {
		return absences;
	}

	public void setAbsences(List<Absence> absences) {
		this.absences = absences;
	}
	
	public void addAbsence(Absence absence) {
        if (!getAbsences().contains(absence)) {
        	getAbsences().add(absence);
        	absence.setLaborerConstructionSite(this);
        }
    }
	
	public void removeAbsence(Absence absence) {
        if (getAbsences().contains(absence)) {
        	getAbsences().remove(absence);
        	absence.setLaborerConstructionSite(null);
        }
    }
    
	public List<Accident> getAccidents() {
		return accidents;
	}

	public void setAccidents(List<Accident> accidents) {
		this.accidents = accidents;
	}

	public void addAccident(Accident accident) {
		if (!getAccidents().contains(accident)) {
        	getAccidents().add(accident);
        	accident.setLaborerConstructionSite(this);
        }
	}
	
	public void removeAccident(Accident accident) {
        if (getAccidents().contains(accident)) {
        	getAccidents().remove(accident);
        	accident.setLaborerConstructionSite(null);
        }
    }
	
    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Laborer getLaborer() {
		return laborer;
	}


	public void setLaborer(Laborer laborer) {
		this.laborer = laborer;
	}


	public ConstructionSite getConstructionsite() {
		return constructionsite;
	}


	public void setConstructionsite(ConstructionSite constructionsite) {
		this.constructionsite = constructionsite;
	}


	public Short getActive() {
		return active;
	}


	public void setActive(Short active) {
		this.active = active;
	}


	public Job getJob() {
		return job;
	}


	public void setJob(Job job) {
		this.job = job;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((constructionsite == null) ? 0 : constructionsite.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((laborer == null) ? 0 : laborer.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LaborerConstructionsite other = (LaborerConstructionsite) obj;
		if (constructionsite == null) {
			if (other.constructionsite != null)
				return false;
		} else if (!constructionsite.equals(other.constructionsite))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (laborer == null) {
			if (other.laborer != null)
				return false;
		} else if (!laborer.equals(other.laborer))
			return false;
		return true;
	}


	@Override
    public String toString() {
        return "jpa.magal.entities.LaborerConstructionsite[ laborerId=" + (laborer != null ? laborer.getLaborerId() : "laborer nulo") + " ]";
    }
    
}
