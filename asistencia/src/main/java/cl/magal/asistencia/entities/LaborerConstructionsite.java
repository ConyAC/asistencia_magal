/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "laborer_constructionsite")
@NamedQueries({
    @NamedQuery(name = "LaborerConstructionsite.findAll", query = "SELECT l FROM LaborerConstructionsite l"),
    @NamedQuery(name = "LaborerConstructionsite.findByLaborerId", query = "SELECT l FROM LaborerConstructionsite l WHERE l.laborerId = :laborerId"),
    @NamedQuery(name = "LaborerConstructionsite.findByConstructionsiteId", query = "SELECT l FROM LaborerConstructionsite l WHERE l.constructionsiteId = :constructionsiteId"),
    @NamedQuery(name = "LaborerConstructionsite.findByActive", query = "SELECT l FROM LaborerConstructionsite l WHERE l.active = :active")})
public class LaborerConstructionsite implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "laborerId")
    private Integer laborerId;
    @Basic(optional = false)
    @Column(name = "construction_siteId")
    private int constructionsiteId;
    @Column(name = "active")
    private Short active;

    public LaborerConstructionsite() {
    }

    public LaborerConstructionsite(Integer laborerId) {
        this.laborerId = laborerId;
    }

    public LaborerConstructionsite(Integer laborerId, int constructionsiteId) {
        this.laborerId = laborerId;
        this.constructionsiteId = constructionsiteId;
    }

    public Integer getLaborerId() {
        return laborerId;
    }

    public void setLaborerId(Integer laborerId) {
        this.laborerId = laborerId;
    }

    public int getConstructionsiteId() {
        return constructionsiteId;
    }

    public void setConstructionsiteId(int constructionsiteId) {
        this.constructionsiteId = constructionsiteId;
    }

    public Short getActive() {
        return active;
    }

    public void setActive(Short active) {
        this.active = active;
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
        if (!(object instanceof LaborerConstructionsite)) {
            return false;
        }
        LaborerConstructionsite other = (LaborerConstructionsite) object;
        if ((this.laborerId == null && other.laborerId != null) || (this.laborerId != null && !this.laborerId.equals(other.laborerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.magal.entities.LaborerConstructionsite[ laborerId=" + laborerId + " ]";
    }
    
}
