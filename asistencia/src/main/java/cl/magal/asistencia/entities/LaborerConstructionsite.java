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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

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
    
    /**
     * Define si un trabajador está confirmado en la obra o no
     */
    @Column(name = "confirmed")
    private boolean confirmed;
    
    @Digits(fraction=0,integer=1000000 , message ="El premio debe ser un valor numérico.")
    @Column(name="reward")
    private int reward;
    
    /**
     * Bloqueo de un obrero en determinada obra
     */
    @Column(name = "block")
    private boolean block;
    
    @JoinColumn(name="personBlockId")
    @OneToOne
    User personBlock;
    
    @Column(name = "comment")
    private String comment;
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<Vacation> vacations = new ArrayList<Vacation>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<Absence> absences = new ArrayList<Absence>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<Accident> accidents = new ArrayList<Accident>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.EAGER,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<Tool> tool = new ArrayList<Tool>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<Contract> contracts = new ArrayList<Contract>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.EAGER,cascade = { CascadeType.PERSIST, CascadeType.MERGE },orphanRemoval=true )
    List<Loan> loan = new ArrayList<Loan>();
   
    /**
     * Define la etapa para la cual está contratado el trabajador actual
     */
    transient private String step;
    /**
     * define el contrato activo o el primero
     */
    transient Contract activeContract;
	
    
//    @ManyToMany(mappedBy="laborers")
//    List<Team> teams = new ArrayList<Team>();
    
    public LaborerConstructionsite() {
    }


//    public List<Team> getTeams() {
//		return teams;
//	}
//
//	public void setTeams(List<Team> teams) {
//		this.teams = teams;
//	}

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
	
	public void addTool(Tool tool) {
        if (!getTool().contains(tool)) {
        	getTool().add(tool);
        	tool.setLaborerConstructionSite(this);
        }
    }
	
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
	
	public void addContract(Contract contract) {
		if (!getContracts().contains(contract)) {
			//marca todos los otros contratos inactivos
			for(Contract c : getContracts())
				c.setActive(false);
			//refresca el contrato activo
			refreshActiveContract();
			
			getContracts().add(contract);
			contract.setLaborerConstructionSite(this);
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
	
	public boolean isConfirmed() {
		return confirmed;
	}


	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}


	public int getReward() {
		return reward;
	}


	public void setReward(int reward) {
		this.reward = reward;
	}


	public List<Contract> getContracts() {
		return contracts;
	}


	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}
	
	public void refreshActiveContract(){
		activeContract = null;
	}
	
	public Contract getActiveContract(){
		if(activeContract == null ){
			// recorre los contratos buscando el activo
			// si la lista de contratos no es vacia entonces asigna el primera
			if(!getContracts().isEmpty()){
				activeContract = getContracts().get(0);
				for (int i = 0; i < getContracts().size(); i++) {
					if(getContracts().get(i).isActive()){
						activeContract = getContracts().get(i);
						break;
					}
				}
			}
		}
		return activeContract;
	}
	/**
	 * VARIABLES SOLO LECTURA PARA RESCATAR LA INFORMACION DEL CONTRACTO ACTIVO
	 * @return
	 */
	public Job getJob() {
		return getActiveContract() != null ? getActiveContract().getJob() : null;
	}

	public Integer getJobCode() {
		return getActiveContract() != null ? getActiveContract().getJobCode() : null;
	}

	public String getStep() {
		return step;
	}
	
	public List<Loan> getLoan() {
		return loan;
	}

	public void setLoan(List<Loan> loan) {
		this.loan = loan;
	}

	public void addLoan(Loan loan) {
		if (!getLoan().contains(loan)) {
        	getLoan().add(loan);
        	loan.setLaborerConstructionSite(this);
        }
	}
	
	/**
	 * 
	 * @return
	 */

	public boolean isBlock() {
		return block;
	}


	public void setBlock(boolean block) {
		this.block = block;
	}


	public User getPersonBlock() {
		return personBlock;
	}


	public void setPersonBlock(User personBlock) {
		this.personBlock = personBlock;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	@Override
    public String toString() {
        return "jpa.magal.entities.LaborerConstructionsite[ laborerId=" + (laborer != null ? laborer.getLaborerId() : "laborer nulo") + " ]";
    }
    
}
