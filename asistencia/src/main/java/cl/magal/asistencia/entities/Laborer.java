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
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import cl.magal.asistencia.entities.converter.AfpConverter;
import cl.magal.asistencia.entities.converter.IsapreConverter;
import cl.magal.asistencia.entities.converter.MaritalStatusConverter;
import cl.magal.asistencia.entities.converter.NationalityConverter;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.Isapre;
import cl.magal.asistencia.entities.enums.MaritalStatus;
import cl.magal.asistencia.entities.enums.Nationality;
import cl.magal.asistencia.entities.validator.RutDigit;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "laborer",
	uniqueConstraints = @UniqueConstraint(columnNames = { "rut" }) )
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
    @NamedQuery(name = "Laborer.findByAfpId", query = "SELECT l FROM Laborer l WHERE l.afp = :afp")
    })
public class Laborer implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8378442753721527646L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "laborerId")
    private Long laborerId;
    @Basic(optional = false)
    @NotNull(message="El nombre es necesario")
    @NotEmpty(message="El nombre es necesario")
    @Column(name = "firstname", nullable=false)
    private String firstname;
    @Column(name = "secondname")
    private String secondname;
    @NotNull(message="El apellido es necesario")
    @NotEmpty(message="El apellido es necesario")
    @Column(name = "lastname", nullable=false)
    private String lastname;
    @Column(name = "secondlastname")
    private String secondlastname;
    @NotNull(message="El rut es necesario")
    @NotEmpty(message="El rut es necesario")
    @Column(name = "rut", nullable=false,unique=true)
    @Pattern(regexp="^([0-9])+\\-([kK0-9])+$",message="El rut '%s' no es válido.")
    @RutDigit(message="El rut '%s' no es válido.")
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
    @Column(name="dependents")
    private Integer dependents;
    @Column(name="town")
    private String town;
    @Column(name="commune")
    private String commune;
    @Column(name="wedge")
    private Integer wedge;

    @Column(name = "afp" , nullable = false)
    @Convert(converter = AfpConverter.class)
    private Afp afp;
    
    @Column(name="isapre" , nullable = false)
    @Convert(converter = IsapreConverter.class)
    private Isapre isapre;
    
    @Convert(converter = MaritalStatusConverter.class)
    @Column(name = "maritalStatus", nullable = false)
    private MaritalStatus maritalStatus;
    
    @Convert(converter = NationalityConverter.class)
    @Column(name="nationality", nullable = false)
    private Nationality nationality;
    
    @Column(name="photo")
    private String photo;
    
    @OneToMany(mappedBy="laborer", orphanRemoval=true)//,fetch=FetchType.EAGER,cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    List<LaborerConstructionsite> laborerConstructionSites = new ArrayList<LaborerConstructionsite>();
    
//    @OneToMany(mappedBy="laborer",fetch=FetchType.EAGER,cascade = { CascadeType.PERSIST, CascadeType.MERGE },orphanRemoval=true )
//    List<Vacation> vacations = new ArrayList<Vacation>();
//    
//    @OneToMany(mappedBy="laborer",fetch=FetchType.EAGER,cascade = { CascadeType.PERSIST, CascadeType.MERGE },orphanRemoval=true )
//    List<Absence> absences = new ArrayList<Absence>();
//    
//    @OneToMany(mappedBy="laborer",fetch=FetchType.EAGER,cascade = { CascadeType.PERSIST, CascadeType.MERGE },orphanRemoval=true )
//    List<Accident> accidents = new ArrayList<Accident>();
//    
//    @OneToMany(targetEntity=Tool.class,fetch=FetchType.EAGER)
//    List<Tool> tool;
//    
//    @ManyToMany(mappedBy="laborers",cascade = { CascadeType.PERSIST, CascadeType.MERGE })
//    List<Team> teams = new LinkedList<Team>();
    
    @PrePersist
    public void prePersist(){
    	if(firstname == null)
    		firstname = "Nuevo trabajador";
    	if(afp == null)
    		afp = Afp.MODELO;
    	if(maritalStatus == null )
    		maritalStatus = MaritalStatus.SOLTERO;
    	if(isapre == null)
    		isapre = Isapre.FONASA;
    	if(nationality == null)
    		nationality = Nationality.CHILENA;
		
    }
    
    public Laborer() {
    }

    public Laborer(Long laborerId) {
        this.laborerId = laborerId;
    }

    public Laborer(Long laborerId, String firstname) {
        this.laborerId = laborerId;
        this.firstname = firstname;
    }
    
    public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Isapre getIsapre() {
		return isapre;
	}

	public void setIsapre(Isapre isapre) {
		this.isapre = isapre;
	}

	public Integer getWedge() {
		return wedge;
	}

	public void setWedge(Integer wedge) {
		this.wedge = wedge;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getCommune() {
		return commune;
	}

	public void setCommune(String commune) {
		this.commune = commune;
	}

	public Nationality getNationality() {
		return nationality;
	}

	public void setNationality(Nationality nationality) {
		this.nationality = nationality;
	}

	public Integer getDependents() {
		return dependents;
	}

	public void setDependents(Integer dependents) {
		this.dependents = dependents;
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

    public Afp getAfp() {
		return afp;
	}

	public void setAfp(Afp afp) {
		this.afp = afp;
	}

	public List<LaborerConstructionsite> getLaborerConstructionSites() {
		return laborerConstructionSites;
	}

	public void setLaborerConstructionSites(
			List<LaborerConstructionsite> laborerConstructionSites) {
		this.laborerConstructionSites = laborerConstructionSites;
	}

	public String getFullname(){
    	return (firstname != null ? firstname : "") + " " + (lastname != null ? lastname : "");
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
