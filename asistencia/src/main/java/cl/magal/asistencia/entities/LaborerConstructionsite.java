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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

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
	@Column(name="laborer_constructionsiteId")
	Long id;
	
    @ManyToOne
    @JoinColumn(name = "laborerId")
    private Laborer laborer;
    
    @ManyToOne
    @JoinColumn(name = "constructionsiteId",updatable=false,nullable=false)
    private ConstructionSite constructionsite;
    
    @Column(name = "active")
    private boolean active;
    
    /**
     * Define si un trabajador está confirmado en la obra o no
     */
    @Column(name = "confirmed")
    private boolean confirmed;
    
    @Digits(fraction=0,integer=1000000 , message ="El premio debe ser un valor numérico.")
    @Column(name="reward")
    private int reward;
    
    @Column(name="use_default_dates")
    private boolean useDefaultDates = true;
    
    @Temporal(TemporalType.DATE)
    @Column(name="reward_startdate")
    Date rewardStartDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name="reward_enddate")
    Date rewardEndDate;
    
    /**
     * Bloqueo de un obrero en determinada obra
     */
    @Column(name = "block")
    private boolean block;
    
    @JoinColumn(name="person_blockId")
    @OneToOne
    User personBlock;
    
    @Column(name = "comment")
    private String comment;
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<Vacation> vacations = new ArrayList<Vacation>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<ProgressiveVacation> progressiveVacation = new ArrayList<ProgressiveVacation>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<License> absences = new ArrayList<License>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<Accident> accidents = new ArrayList<Accident>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval=true )
    List<Tool> tool = new ArrayList<Tool>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade = { CascadeType.PERSIST, CascadeType.MERGE },orphanRemoval=true )
    List<Loan> loan = new ArrayList<Loan>();
    
    @OneToMany(mappedBy="laborerConstructionSite",fetch=FetchType.LAZY,cascade = { CascadeType.PERSIST, CascadeType.MERGE },orphanRemoval=true )
    List<WithdrawalSettlement> withdrawalSettlements = new ArrayList<WithdrawalSettlement>();
   
    @ManyToMany(mappedBy="laborerConstructionsites",cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    List<Team> teams = new ArrayList<Team>();
    
    /**
     * Define la etapa para la cual está contratado el trabajador actual
     */
    transient private String step;
    transient private double failureDiscount;
    transient private double othersDiscount;
    
    /**
     * define el contrato activo o el primero
     */
    @NotNull
    @OneToOne(mappedBy="laborerConstructionSite",cascade = {CascadeType.PERSIST,CascadeType.MERGE},optional=false)
    Contract activeContract;
    
    @Column(name = "suple_code",nullable=false)
  	Integer supleCode = 1;
    
    public double getFailureDiscount() {
		return failureDiscount;
	}
	public void setFailureDiscount(double failureDiscount) {
		this.failureDiscount = failureDiscount;
	}
	public double getOthersDiscount() {
		return othersDiscount;
	}
	public void setOthersDiscount(double othersDiscount) {
		this.othersDiscount = othersDiscount;
	}
	@PreUpdate
    public void preUpdate(){
    	defineRequired();
    }
    @PrePersist
    public void prePersist(){
    	defineRequired();
    	this.active = true;
    }
    
    public void defineRequired(){
    	if(rewardEndDate == null )
    		rewardEndDate = new Date();
    	if(rewardStartDate == null )
    		rewardStartDate = new Date();
    }
    
    public Integer getSupleCode() {
		return supleCode;
	}
	public void setSupleCode(Integer supleCode) {
		this.supleCode = supleCode;
	}
	public LaborerConstructionsite() {
    }
    
	public boolean isUseDefaultDates() {
		return useDefaultDates;
	}
	public void setUseDefaultDates(boolean useDefaultDates) {
		this.useDefaultDates = useDefaultDates;
	}
	public Date getRewardStartDate() {
		return rewardStartDate;
	}
	public void setRewardStartDate(Date rewardStartDate) {
		this.rewardStartDate = rewardStartDate;
	}
	public Date getRewardEndDate() {
		return rewardEndDate;
	}

	public void setRewardEndDate(Date rewardEndDate) {
		this.rewardEndDate = rewardEndDate;
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
	
	public List<ProgressiveVacation> getProgressiveVacation() {
		return progressiveVacation;
	}
	public void setProgressiveVacation(List<ProgressiveVacation> progressiveVacation) {
		this.progressiveVacation = progressiveVacation;
	}
	
	public void addProgressiveVacation(ProgressiveVacation vacation) {
        if (!getProgressiveVacation().contains(vacation)) {
        	getProgressiveVacation().add(vacation);
        	vacation.setLaborerConstructionSite(this);
        }
    }
	
	public void removeVacation(ProgressiveVacation vacation) {
        if (getProgressiveVacation().contains(vacation)) {
        	getProgressiveVacation().remove(vacation);
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
	
	public void removeTool(Tool tool) {
        if (getTool().contains(tool)) {
        	getTool().remove(tool);
        	tool.setLaborerConstructionSite(null);
        }
    }
	
	public List<License> getAbsences() {
		return absences;
	}

	public void setAbsences(List<License> absences) {
		this.absences = absences;
	}
	
	public void addAbsence(License absence) {
        if (!getAbsences().contains(absence)) {
        	getAbsences().add(absence);
        	absence.setLaborerConstructionSite(this);
        }
    }
	
	public void removeAbsence(License absence) {
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
	
//	public void addContract(Contract contract) {
//		if (!getContracts().contains(contract)) {
//			//marca todos los otros contratos inactivos
//			for(Contract c : getContracts())
//				c.setActive(false);
//			//refresca el contrato activo
////			refreshActiveContract();
//			
//			getContracts().add(contract);
//			contract.setLaborerConstructionSite(this);
//        }
//	}
	
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


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
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

	public Contract getActiveContract(){
		return activeContract;
	}
	
	public void setActiveContract(Contract contract){
		this.activeContract = contract;
		if(contract.getLaborerConstructionSite() == null )
			contract.setLaborerConstructionSite(this);
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
	
	public List<WithdrawalSettlement> getWithdrawalSettlements() {
		return withdrawalSettlements;
	}
	public void setWithdrawalSettlements(
			List<WithdrawalSettlement> withdrawalSettlements) {
		this.withdrawalSettlements = withdrawalSettlements;
	}
	
	public void addWithdrawalSettlement(WithdrawalSettlement withdrawalSettlement) {
		if (!getWithdrawalSettlements().contains(withdrawalSettlement)) {
			getWithdrawalSettlements().add(withdrawalSettlement);
			withdrawalSettlement.setLaborerConstructionSite(this);
        }
	}
	
	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}
	
	public void addTeam(Team team) {
        if (!getTeams().contains(team)) {
        	getTeams().add(team);
        }
        if (!team.getLaborerConstructionsites().contains(this)) {
        	team.getLaborerConstructionsites().add(this);
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
        return "jpa.magal.entities.LaborerConstructionsite[ id = "+id+", laborerId=" + (laborer != null ? laborer.getId() : "laborer nulo") + " ]";
    }
    
}
