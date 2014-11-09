/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
    @NamedQuery(name = "Laborer.findByRol", query = "SELECT l FROM Laborer l WHERE l.rol = :rol"),
    @NamedQuery(name = "Laborer.findByDateBirth", query = "SELECT l FROM Laborer l WHERE l.dateBirth = :dateBirth"),
    @NamedQuery(name = "Laborer.findByMaritalStatusId", query = "SELECT l FROM Laborer l WHERE l.maritalStatusId = :maritalStatusId"),
    @NamedQuery(name = "Laborer.findByAddress", query = "SELECT l FROM Laborer l WHERE l.address = :address"),
    @NamedQuery(name = "Laborer.findByMobileNumber", query = "SELECT l FROM Laborer l WHERE l.mobileNumber = :mobileNumber"),
    @NamedQuery(name = "Laborer.findByPhone", query = "SELECT l FROM Laborer l WHERE l.phone = :phone"),
    @NamedQuery(name = "Laborer.findByDateAdmission", query = "SELECT l FROM Laborer l WHERE l.dateAdmission = :dateAdmission"),
    @NamedQuery(name = "Laborer.findByContractId", query = "SELECT l FROM Laborer l WHERE l.contractId = :contractId"),
    @NamedQuery(name = "Laborer.findByJobId", query = "SELECT l FROM Laborer l WHERE l.jobId = :jobId"),
    @NamedQuery(name = "Laborer.findByAfpId", query = "SELECT l FROM Laborer l WHERE l.afpId = :afpId"),
    @NamedQuery(name = "Laborer.findByTeamId", query = "SELECT l FROM Laborer l WHERE l.teamId = :teamId")})
public class Laborer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "laborerId")
    private Integer laborerId;
    @Basic(optional = false)
    @Column(name = "firstname")
    private String firstname;
    @Column(name = "secondname")
    private String secondname;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "secondlastname")
    private String secondlastname;
    @Column(name = "rut")
    private String rut;
    @Column(name = "rol")
    private String rol;
    @Column(name = "dateBirth")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateBirth;
    @Column(name = "maritalStatusId")
    private Integer maritalStatusId;
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
    @Column(name = "jobId")
    private Integer jobId;
    @Column(name = "afpId")
    private Integer afpId;
    @Column(name = "teamId")
    private Integer teamId;

    public Laborer() {
    }

    public Laborer(Integer laborerId) {
        this.laborerId = laborerId;
    }

    public Laborer(Integer laborerId, String firstname) {
        this.laborerId = laborerId;
        this.firstname = firstname;
    }

    public Integer getLaborerId() {
        return laborerId;
    }

    public void setLaborerId(Integer laborerId) {
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Date getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(Date dateBirth) {
        this.dateBirth = dateBirth;
    }

    public Integer getMaritalStatusId() {
        return maritalStatusId;
    }

    public void setMaritalStatusId(Integer maritalStatusId) {
        this.maritalStatusId = maritalStatusId;
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

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getAfpId() {
        return afpId;
    }

    public void setAfpId(Integer afpId) {
        this.afpId = afpId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
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
