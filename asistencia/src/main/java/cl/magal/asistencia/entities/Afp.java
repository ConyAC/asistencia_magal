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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "afp")
@NamedQueries({
    @NamedQuery(name = "Afp.findAll", query = "SELECT a FROM Afp a"),
    @NamedQuery(name = "Afp.findByAfpId", query = "SELECT a FROM Afp a WHERE a.afpId = :afpId"),
    @NamedQuery(name = "Afp.findByName", query = "SELECT a FROM Afp a WHERE a.name = :name")})
public class Afp implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "afpId")
    private Integer afpId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;

    public Afp() {
    }

    public Afp(Integer afpId) {
        this.afpId = afpId;
    }

    public Afp(Integer afpId, String name) {
        this.afpId = afpId;
        this.name = name;
    }

    public Integer getAfpId() {
        return afpId;
    }

    public void setAfpId(Integer afpId) {
        this.afpId = afpId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (afpId != null ? afpId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Afp)) {
            return false;
        }
        Afp other = (Afp) object;
        if ((this.afpId == null && other.afpId != null) || (this.afpId != null && !this.afpId.equals(other.afpId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.magal.entities.Afp[ afpId=" + afpId + " ]";
    }
    
}
